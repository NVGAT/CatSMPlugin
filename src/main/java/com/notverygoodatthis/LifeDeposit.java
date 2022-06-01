package com.notverygoodatthis;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LifeDeposit implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            //Command that deposits some of the player's lives in an item form
            Player playerSender = (Player) sender;
            if(label.length() != 0) {
                int lifeAmount;
                try {
                    lifeAmount = Integer.parseInt(args[0]);
                    if(lifeAmount < SMPlugin.MAX_LIVES - playerSender.getStatistic(Statistic.DEATHS)) {
                        //If the amount of lives is less than what the player has, we drop the life items on the ground
                        playerSender.getWorld().dropItemNaturally(playerSender.getLocation(), SMPlugin.getLife(lifeAmount));
                        playerSender.setStatistic(Statistic.DEATHS, playerSender.getStatistic(Statistic.DEATHS) + lifeAmount);
                        //If the player lost all of their lives to this, we ban him for not having any lives left
                        if(playerSender.getStatistic(Statistic.DEATHS) > SMPlugin.MAX_LIVES) {
                            Bukkit.getBanList(BanList.Type.NAME).addBan(playerSender.getDisplayName(), "You have lost all of your lives. Thank you for playing on Cat SMP", null, "Server");
                            playerSender.kickPlayer("You have lost all of your lives. Thank you for playing on Cat SMP.");
                            Bukkit.broadcastMessage(playerSender.getDisplayName() + " has lost all of their lives. They will be stuck in spectator until someone revives them.");
                        }
                        //After all of that we notify the player that they've successfully deposited their lives
                        playerSender.sendMessage("You have successfully deposited " + lifeAmount + " lives!");
                    } else {
                        //If the player doesn't have as many lives as they've specified, then we tell them that they can't deposit more lives than they already have
                        playerSender.sendMessage("You cannot deposit more lives than you already have!");
                    }
                } catch(NumberFormatException e) {
                    //If we get a NumberFormatException that means that the player hasn't specified a valid number, or hasn't specified a number at all.
                    //Either way, we notify them about it and don't do anything with the command
                    playerSender.sendMessage("Please enter how many lives you wish to deposit");
                    return false;
                }
            }
        }
        return true;
    }
}
