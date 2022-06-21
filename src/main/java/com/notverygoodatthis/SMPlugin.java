package com.notverygoodatthis;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class SMPlugin extends JavaPlugin implements Listener {
    //Important constants used in other classes
    public static int MAX_LIVES = 5;
    public static String LIFE_ITEM_NAME = "§a§lLife";
    public static String SPEED_ITEM_NAME = "§f§lSpeed";
    public static String REVIVAL_ITEM_NAME = "§4§oRevive item";
    List<String> items = new ArrayList<String>();
    List<Player> combattedPlayers = new ArrayList<Player>();
    List<Integer> currentRunnableID = new ArrayList<Integer>();

    //Getter for the speed item, used for efficiency and clean code
    private ItemStack getSpeed(int amount) {
        ItemStack speed = new ItemStack(Material.SUGAR, amount);
        ItemMeta meta = speed.getItemMeta();
        meta.setDisplayName(SPEED_ITEM_NAME);
        speed.setItemMeta(meta);
        return speed;
    }

    private ItemStack getRevivalItem(int amount) {
        ItemStack revivalItem = new ItemStack(Material.PLAYER_HEAD, amount);
        ItemMeta meta = revivalItem.getItemMeta();
        meta.setDisplayName(REVIVAL_ITEM_NAME);
        revivalItem.setItemMeta(meta);
        return revivalItem;
    }

    //Important getter for lives
    public static ItemStack getLife(int amount) {
        ItemStack life = new ItemStack(Material.POPPED_CHORUS_FRUIT, amount);
        ItemMeta meta = life.getItemMeta();
        meta.setDisplayName(LIFE_ITEM_NAME);
        life.setItemMeta(meta);
        return life;
    }

    @Override
    public void onEnable() {
        //Registers the event listeners
        Bukkit.getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();

        //Loads the available-items config into the items list
        FileConfiguration config = this.getConfig();
        items = (List<String>) config.getList("available-items");

        registerRecipes();
        registerCommands();
    }

    void registerRecipes() {
        //Adds all of the custom recipes
        Bukkit.addRecipe(reviveItem());
        Bukkit.addRecipe(enchantedGoldenApple());
        Bukkit.addRecipe(saddle());
        Bukkit.addRecipe(speedRecipe());
        Bukkit.addRecipe(netherWart());
        Bukkit.addRecipe(lifeItem());
    }

    void registerCommands() {
        //Registers all of the custom commands
        this.getCommand("catrevive").setExecutor(new CommandKit());
        this.getCommand("deposit").setExecutor(new LifeDeposit());
        this.getCommand("reset").setExecutor(new ResetCommand());
        this.getCommand("catlives").setExecutor(new GetCommand());
        this.getCommand("catset").setExecutor(new SetCommand());
        this.getCommand("catkit").setExecutor(new KitCommand());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            //Very long, kinda messy code to check if the item the player's holding is equal to the life item
            Player player = e.getPlayer();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if(itemInHand.getType() == Material.POPPED_CHORUS_FRUIT && itemInHand.getItemMeta().getDisplayName().equals(LIFE_ITEM_NAME)) {
                //Applies the life if everything checks out
                player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) - 1);
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            }
            //Again, kinda messy if statement to see if the player's holding the speed item
            if(itemInHand.getType() == Material.SUGAR && itemInHand.getItemMeta().getDisplayName().equals(SPEED_ITEM_NAME)) {
                //Adds the speed, absorption and saturation potion effects to the player
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12000, 3));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 12000, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 3000, 1));
                //Removes the used speed item from the player's inventory
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        //Checks if the entity that killed the player is a player
        Player player = e.getEntity();
        Entity killer = player.getKiller();
        if(killer instanceof Player || e.getDeathMessage().equals(e.getEntity().getName() + " died")) {
            getLogger().info(String.valueOf(player.getStatistic(Statistic.DEATHS)));
            //Checks if the player lost all of their lives
            if(player.getStatistic(Statistic.DEATHS) > MAX_LIVES - 1) {
                //Broadcasts a chat message and drops the life item, along with OP gear
                Bukkit.broadcastMessage(player.getDisplayName() + " has lost all of their lives. They will be banned until someone revives them.");
                player.getWorld().dropItemNaturally(player.getLocation(), getLife(1));
                //Gets a random piece of gear
                Random random = new Random();
                int randInt = random.nextInt(items.size());
                ItemStack randItem = new ItemStack(Material.getMaterial(items.get(randInt)));
                //Applies the OP enchants based on the gear that was chosen previously
                switch (randItem.getType()) {
                    case NETHERITE_CHESTPLATE:
                    case NETHERITE_LEGGINGS:
                        randItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
                        randItem.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
                        randItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                        break;
                    case NETHERITE_SWORD:
                        randItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
                        randItem.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 5);
                        randItem.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 5);
                        randItem.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
                        randItem.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, 4);
                        randItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                        break;
                    case NETHERITE_AXE:
                        randItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
                        randItem.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
                        randItem.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5);
                        randItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                        break;
                    case NETHERITE_HELMET:
                        randItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
                        randItem.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
                        randItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                        randItem.addUnsafeEnchantment(Enchantment.OXYGEN, 5);
                        randItem.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
                        break;
                    case NETHERITE_BOOTS:
                        randItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
                        randItem.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
                        randItem.addUnsafeEnchantment(Enchantment.MENDING, 2);
                        randItem.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 6);
                        randItem.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 5);
                        break;

                }
                //Drops the OP item and bans the player
                player.getWorld().dropItemNaturally(player.getLocation(), randItem);
                Bukkit.getBanList(BanList.Type.NAME).addBan(player.getDisplayName(), "You have lost all of your lives. Thank you for playing on Cat SMP.", null, "Server");
                player.kickPlayer("You have lost all of your lives. Thank you for playing on Cat SMP.");
            }
        } else {
            //If the player wasn't killed by a player then we reset their life count
            player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) - 1);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        //Amplifies the creeper drop rates
        if(e.getEntity() instanceof Creeper) {
            e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), new ItemStack(Material.GUNPOWDER, 5));
        }
    }


    public ShapedRecipe reviveItem() {
        //Crafting recipe for the revive item
        ItemStack reviveItem = getRevivalItem(1);
        NamespacedKey key = new NamespacedKey(this, "player_head");
        ShapedRecipe recipe = new ShapedRecipe(key, reviveItem);
        recipe.shape("TDT", "DND", "TDT");
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        return recipe;
    }

    public ShapedRecipe lifeItem() {
        //Crafting recipe for the life item
        ItemStack life = getLife(1);
        NamespacedKey key = new NamespacedKey(this, "popped_chorus_fruit");
        ShapedRecipe recipe = new ShapedRecipe(key, life);
        recipe.shape("GGG", "GNG", "GGG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('N', Material.NETHERITE_BLOCK);
        return recipe;
    }

    public ShapedRecipe enchantedGoldenApple() {
        //Crafting recipe for the god apple
        ItemStack enchantedApple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        NamespacedKey key = new NamespacedKey(this, "enchanted_golden_apple");
        ShapedRecipe recipe = new ShapedRecipe(key, enchantedApple);
        recipe.shape("NDN", "DAD", "NDN");
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('A', Material.APPLE);
        return recipe;
    }

    public ShapedRecipe saddle() {
        //Crafting recipe for the saddle
        ItemStack craftableSaddle = new ItemStack(Material.SADDLE);
        NamespacedKey key = new NamespacedKey(this, "saddle");
        ShapedRecipe recipe = new ShapedRecipe(key, craftableSaddle);
        recipe.shape("LLL", "LIL", "I I");
        recipe.setIngredient('L', Material.LEATHER);
        recipe.setIngredient('I', Material.IRON_INGOT);
        return recipe;
    }

    public ShapedRecipe speedRecipe() {
        //Crafting recipe for the speed item
        ItemStack speed = getSpeed(1);
        NamespacedKey key = new NamespacedKey(this, "sugar");
        ShapedRecipe recipe = new ShapedRecipe(key, speed);
        recipe.shape("SBS", "BBB", "SBS");
        recipe.setIngredient('S', Material.SUGAR);
        recipe.setIngredient('B', Material.DIAMOND_BLOCK);
        return recipe;
    }

    public ShapelessRecipe netherWart() {
        //Craftable nether wart
        ItemStack netherWart = new ItemStack(Material.NETHER_WART, 9);
        NamespacedKey key = new NamespacedKey(this, "nether_wart");
        ShapelessRecipe recipe = new ShapelessRecipe(key, netherWart);
        recipe.addIngredient(Material.NETHER_WART);
        return recipe;
    }
}
