package com.notverygoodatthis;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            //Resets a player's lives
            Player playerSender = (Player) sender;
            if(args.length != 0) {
                //Makes sure that the command can only be used by operators
                if(playerSender.isOp()) {
                    //Gets the target player based on the command input
                    Player target = Bukkit.getPlayer(args[0]);
                    //Sets the target player's deaths to 0, effectively resetting their lives
                    target.setStatistic(Statistic.DEATHS, 0);
                    return true;
                }
                else {
                    //If someone tried to run this command but isn't operator, they get a message and nothing happens
                    playerSender.sendMessage("This is an operator-only command");
                }
            }
        }
        return false;
    }
}
