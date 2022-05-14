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
                String revivalName = args[0];
                Bukkit.getLogger().info("the name of the provided player is " + revivalName);
                try {
                    Bukkit.getBanList(BanList.Type.NAME).pardon(revivalName);
                    playerSender.getInventory().getItemInMainHand().setAmount(playerSender.getInventory().getItemInMainHand().getAmount() - 1);
                    OfflinePlayer revivalPlayer = Bukkit.getOfflinePlayer(revivalName);
                    revivalPlayer.setStatistic(Statistic.DEATHS, 0);
                    playerSender.sendMessage(revivalName + " has been successfully revived. If there is a bug or you made a typo contact NotVeryGoodAtThis#8575 on Discord.");
                } catch(NullPointerException e) {
                    playerSender.sendMessage("That player either hasn't been online on this server at all or they don't need a revival.");
                }
            }
        }
        return true;
    }
}
