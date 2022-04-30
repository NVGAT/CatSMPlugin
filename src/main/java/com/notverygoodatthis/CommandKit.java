package com.notverygoodatthis;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKit implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player playerSender = (Player) sender;
            if(label.length() != 0) {
                try {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if(target != null && playerSender.getStatistic(Statistic.DEATHS) < SMPlugin.MAX_LIVES - 1) {
                        Bukkit.getBanList(BanList.Type.NAME).pardon(target.getDisplayName());
                        target.setStatistic(Statistic.DEATHS, 0);
                        playerSender.sendMessage(target.getDisplayName() + " has been revived.");
                    }
                } catch (NullPointerException e) {
                    OfflinePlayer target = Bukkit.getPlayerExact(args[0]);
                    if(target == null) {
                        playerSender.sendMessage("That player has never player on this server");
                    } else if(target != null && playerSender.getStatistic(Statistic.DEATHS) < SMPlugin.MAX_LIVES - 1) {
                        Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());
                        target.setStatistic(Statistic.DEATHS, 0);
                        playerSender.sendMessage("That player has been revived");
                    }
                    else {
                        playerSender.sendMessage("An error ocurred while processing your request. You either typed the name wrong or the player does not need a revival");
                    }
                }

            }
        }
        return true;
    }
}
