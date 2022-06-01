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
            //Sets the player's lives to a provided number
            Player playerSender = (Player) sender;
            if(args.length != 0) {
                if(playerSender.isOp()) {
                    try {
                        //If the player is an operator and all the arguments are valid, we set the player's lives to the provided number
                        Player target = Bukkit.getPlayerExact(args[0]);
                        int newLives = Integer.parseInt(args[1]);
                        target.setStatistic(Statistic.DEATHS, SMPlugin.MAX_LIVES - newLives);
                        if(target.getStatistic(Statistic.DEATHS) < 5) {
                            Bukkit.getBanList(BanList.Type.NAME).pardon(target.getDisplayName());
                        }
                        return true;
                    }
                    catch(NumberFormatException e) {
                        //If we get a NumberFormatException that means that the user didn't enter a valid number in the command, and we inform them about it.
                        playerSender.sendMessage("You need to enter a valid number as the second argument");
                    }
                    catch(NullPointerException e) {
                        //If we get a NullPointerException that means that the Bukkit.getPlayerExact() getter didn't manage to return a player name, and we inform the user about it
                        playerSender.sendMessage("Specify the player name");
                    }
                    catch(IllegalArgumentException e) {
                        //If we get an IllegalArgumentException that means that the Integer.parseInt() method didn't manage to convert the number string into an integer. As always, we inform the user about this
                        playerSender.sendMessage("Enter a valid number in the second argument");
                    }
                }
                else {
                    //If a non-operator player tries to run this command, they're locked out of it
                    playerSender.sendMessage("This is an operator-only command.");
                }
            }
        }
        return false;
    }
}
