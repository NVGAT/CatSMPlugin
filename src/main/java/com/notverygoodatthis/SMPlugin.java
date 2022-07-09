package com.notverygoodatthis;

              //*************************************\\
             //***********Omega SMP plugin************\\
            //*******Made by NotVeryGoodAtThis*********\\
           //**Give credit if you use my code anywhere**\\
          //***See credits.txt to see the tools I used***\\
         //The comments should make the code easy to read.\\
        //***Have fun! Tweak the code however you'd like***\\
       //Once you're happy, run the project in IntelliJ IDEA\\
      //It will create a JAR file in the folder named target!\\
     //*******************************************************\\
    //*****I'll try to make all of my plugins open-source.*****\\
   //****I just like the idea of people taking a deeper look****\\
  //*************************************************************\\
 //*****I hope you have a lot of fun working with the code :)*****\\
//*****************************************************************\\


import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.List;
public final class SMPlugin extends JavaPlugin implements Listener {
    //Important constants used in other classes
    public static int MAX_LIVES = 5;
    public static String LIFE_ITEM_NAME = "§a§lLife";
    public static String SPEED_ITEM_NAME = "§f§lSpeed";
    public static String REVIVAL_ITEM_NAME = "§4§oRevive item";
    public static List<String> creditsMessage = new ArrayList<String>();
    List<String> items = new ArrayList<String>();

    //Getter for the speed item, used for efficiency and clean code
    private ItemStack getSpeed(int amount) {
        ItemStack speed = new ItemStack(Material.SUGAR, amount);
        ItemMeta meta = speed.getItemMeta();
        meta.setDisplayName(SPEED_ITEM_NAME);
        speed.setItemMeta(meta);
        return speed;
    }

    //Getter for the revival item
    private static ItemStack getRevivalItem(int amount) {
        //Uses the SkullCreator library to get a cool looking skull from base64
        ItemStack revivalItem = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ2MDdhZThhNmY5Mzc0MmU4ZWIxNmEwZjg2MjY1OWUzMDg3NjEwMTlhMzk3NzIyYzFhZmU4NGIxNzlkMWZhMiJ9fX0=");
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

    //Getter for the ressurection fragment
    public static ItemStack getRessurectionItem(int amount) {
        ItemStack ressurectionItem = new ItemStack(Material.COCOA_BEANS, amount);
        ItemMeta meta = ressurectionItem.getItemMeta();;
        meta.setDisplayName("§b§lRessurection fragment");
        ressurectionItem.setItemMeta(meta);
        return ressurectionItem;
    }

    @Override
    //The onEnable function fires when the plugin is first enabled in the Spigot server
    public void onEnable() {
        //Registers the event listeners
        Bukkit.getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();

        //Loads the available-items and credits-message lists from the configuration file
        FileConfiguration config = this.getConfig();
        items = (List<String>) config.getList("available-items");
        creditsMessage = (List<String>) config.getList("credits-message");

        //Registers the recipes and the commands
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
        this.getCommand("catcredits").setExecutor(new CreditsCommand());
    }

    @EventHandler
    //PlayerInteractEvent is used to determine players clicking on anything
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
                //Drops a ressurection fragment on the ground
                player.getWorld().dropItemNaturally(player.getLocation(), getRessurectionItem(1));
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
        //Amplifies the creeper drop rates. If you don't want amplified drop rates, you can just remove this method
        if(e.getEntity() instanceof Creeper) {
            e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), new ItemStack(Material.GUNPOWDER, 5));
        }
    }

    @EventHandler
    public void craftingEvent(PrepareItemCraftEvent e) {
        CraftingInventory ci = e.getInventory();
        try {
            //We check if the recipe result has the revival item name
            if(ci.getRecipe().getResult().getItemMeta().getDisplayName().equals(REVIVAL_ITEM_NAME)) {
                //If the middle slot isn't a ressurection item, we set the result to air, or effectively nothing.
                if(!ci.getMatrix()[4].getItemMeta().getDisplayName().equals("§b§lRessurection fragment")) {
                    ci.setResult(new ItemStack(Material.AIR));
                }
            }
        } catch(NullPointerException exception) {
            //nothing here, just wanted to get rid of the errors
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
        recipe.setIngredient('N', Material.COCOA_BEANS);
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
