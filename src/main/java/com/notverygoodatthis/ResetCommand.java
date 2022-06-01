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
                if(playerSender.isOp()) {
                    Player target = Bukkit.getPlayer(args[0]);
                    target.setStatistic(Statistic.DEATHS, 0);
                    return true;
                }
                else {
                    playerSender.sendMessage("You need to be an operator to issue this command");
                }
            }
        }
        return false;
    }
}
