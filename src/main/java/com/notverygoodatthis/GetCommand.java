package com.notverygoodatthis;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            //Simple command that gets the amount of lives a certain player has
            Player playerSender = (Player) sender;
            if(args.length != 0) {
                Player target = Bukkit.getPlayerExact(args[0]);
                int livesLeft = SMPlugin.MAX_LIVES - target.getStatistic(Statistic.DEATHS);
                playerSender.sendMessage(target.getDisplayName() + " has " + livesLeft + " lives left.");
                return true;
            } else {
                playerSender.sendMessage("Enter an username to get life number from");
            }
        }
        return false;
    }
}
