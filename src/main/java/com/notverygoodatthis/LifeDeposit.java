package com.notverygoodatthis;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LifeDeposit implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player playerSender = (Player) sender;
            if(label.length() != 0) {
                int lifeAmount;
                try {
                    lifeAmount = Integer.parseInt(args[0]);
                    if(lifeAmount < SMPlugin.MAX_LIVES - playerSender.getStatistic(Statistic.DEATHS)) {
                        ItemStack life = new ItemStack(Material.FIREWORK_STAR, lifeAmount);
                        ItemMeta meta = life.getItemMeta();
                        meta.setDisplayName(SMPlugin.LIFE_ITEM_NAME);
                        life.setItemMeta(meta);
                        playerSender.getWorld().dropItemNaturally(playerSender.getLocation(), life);
                        playerSender.setStatistic(Statistic.DEATHS, playerSender.getStatistic(Statistic.DEATHS) + lifeAmount);
                        if(playerSender.getStatistic(Statistic.DEATHS) > SMPlugin.MAX_LIVES) {
                            Bukkit.getBanList(BanList.Type.NAME).addBan(playerSender.getDisplayName(), "You have lost all of your lives. Thank you for playing on Cat SMP", null, "Server");
                            playerSender.kickPlayer("You have lost all of your lives. Thank you for playing on Cat SMP.");
                            Bukkit.broadcastMessage(playerSender.getDisplayName() + " has lost all of their lives. They will be stuck in spectator until someone revives them.");
                        }
                        playerSender.sendMessage("You have successfully deposited " + lifeAmount + " lives!");
                    } else {
                        playerSender.sendMessage("You cannot deposit more lives than you already have!");
                    }
                } catch(NumberFormatException e) {
                    playerSender.sendMessage("Please enter how many lives you wish to deposit");
                    return false;
                }
            }
        }
        return true;
    }
}
