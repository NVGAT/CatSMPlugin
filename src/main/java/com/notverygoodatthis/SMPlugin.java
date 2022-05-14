package com.notverygoodatthis;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.naming.Name;
import java.awt.*;
import java.util.*;
import java.util.List;

public final class SMPlugin extends JavaPlugin implements Listener {
    public static int MAX_LIVES = 5;
    public static String LIFE_ITEM_NAME = "§a§lLife";
    List<String> items = new ArrayList<String>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        items = (List<String>) config.getList("available-items");
        registerRecipes();
        registerCommands();
    }

    void registerRecipes() {
        Bukkit.addRecipe(reviveItem());
        Bukkit.addRecipe(enchantedGoldenApple());
        Bukkit.addRecipe(saddle());
    }

    void registerCommands() {
        this.getCommand("catrevive").setExecutor(new CommandKit());
        this.getCommand("deposit").setExecutor(new LifeDeposit());
        this.getCommand("reset").setExecutor(new ResetCommand());
        this.getCommand("catlives").setExecutor(new GetCommand());
        this.getCommand("catset").setExecutor(new SetCommand());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if(itemInHand.getType() == Material.FIREWORK_STAR && itemInHand.getItemMeta().getDisplayName().equals(LIFE_ITEM_NAME)) {
                player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) - 1);
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Entity killer = player.getKiller();
        if(killer instanceof Player) {
            getLogger().info(String.valueOf(player.getStatistic(Statistic.DEATHS)));
            if(player.getStatistic(Statistic.DEATHS) > MAX_LIVES - 1) {
                Bukkit.broadcastMessage(player.getDisplayName() + " has lost all of their lives. They will be banned until someone revives them.");
                Random random = new Random();
                int randInt = random.nextInt(items.size());
                ItemStack randMat = new ItemStack(Material.matchMaterial(items.get(randInt)));
                ItemMeta meta = randMat.getItemMeta();
                switch (randMat.getType()) {
                    case NETHERITE_CHESTPLATE:
                        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
                        meta.addEnchant(Enchantment.DURABILITY, 3, true);
                        randMat.setItemMeta(meta);
                        break;
                }
                Bukkit.getBanList(BanList.Type.NAME).addBan(player.getDisplayName(), "You have lost all of your lives. Thank you for playing on Cat SMP.", null, "Server");
                player.kickPlayer("You have lost all of your lives. Thank you for playing on Cat SMP.");
            }
        } else {
            player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) - 1);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if(player.getStatistic(Statistic.DEATHS) < MAX_LIVES - 1 && Bukkit.getBanList(BanList.Type.NAME).isBanned(player.getName())) {
            Bukkit.getBanList(BanList.Type.NAME).pardon(player.getName());
            player.sendMessage("You got revived by someone. You now have 5 lives. Take better care of them this time.");
        }
    }

    public ShapedRecipe reviveItem() {
        ItemStack reviveItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = reviveItem.getItemMeta();
        meta.setDisplayName("§f§4§oRevive item");
        reviveItem.setItemMeta(meta);
        NamespacedKey key = new NamespacedKey(this, "player_head");
        ShapedRecipe recipe = new ShapedRecipe(key, reviveItem);
        recipe.shape("GGG", "GDG", "GGG");
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        return recipe;
    }

    public ShapedRecipe enchantedGoldenApple() {
        ItemStack enchantedApple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        NamespacedKey key = new NamespacedKey(this, "enchanted_golden_apple");
        ShapedRecipe recipe = new ShapedRecipe(key, enchantedApple);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('A', Material.APPLE);
        return recipe;
    }

    public ShapedRecipe saddle() {
        ItemStack craftableSaddle = new ItemStack(Material.SADDLE);
        NamespacedKey key = new NamespacedKey(this, "saddle");
        ShapedRecipe recipe = new ShapedRecipe(key, craftableSaddle);
        recipe.shape("LLL", "LIL", "I I");
        recipe.setIngredient('L', Material.LEATHER);
        recipe.setIngredient('I', Material.IRON_INGOT);
        return recipe;
    }

    public ShapelessRecipe netherWart() {
        ItemStack netherWart = new ItemStack(Material.NETHER_WART, 9);
        NamespacedKey key = new NamespacedKey(this, "nether_wart");
        ShapelessRecipe recipe = new ShapelessRecipe(key, netherWart);
        recipe.addIngredient(Material.NETHER_WART);
        return recipe;
    }
}
