package com.notverygoodatthis;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player playerSender = (Player) sender;
            if(args.length != 0) {
                if(playerSender.isOp()) {
                    try {
                        Player target = Bukkit.getPlayerExact(args[0]);
                        int newLives = Integer.parseInt(args[1]);
                        target.setStatistic(Statistic.DEATHS, SMPlugin.MAX_LIVES - newLives);
                        if(target.getStatistic(Statistic.DEATHS) < 5) {
                            Bukkit.getBanList(BanList.Type.NAME).pardon(target.getDisplayName());
                        }
                        return true;
                    }
                    catch(NumberFormatException e) {
                        playerSender.sendMessage("You need to enter a valid number as the second argument");
                    }
                    catch(NullPointerException e) {
                        playerSender.sendMessage("Specify the player name");
                    }
                    catch(IllegalArgumentException e) {
                        playerSender.sendMessage("Enter a valid number in the second argument");
                    }
                }
                else {
                    playerSender.sendMessage("This is an operator-only command.");
                }
            }
        }
        return false;
    }
}
