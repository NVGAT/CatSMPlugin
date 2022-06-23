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
                //We get the player whose name we mentioned in the command
                Player target = Bukkit.getPlayerExact(args[0]);
                //Then we get the amount of lives they have, based on the max lives subtracted by the amount of deaths the player had
                int livesLeft = SMPlugin.MAX_LIVES - target.getStatistic(Statistic.DEATHS);
                //Then we send a message to the command sender about how many lives the mentioned player has
                playerSender.sendMessage(target.getDisplayName() + " has " + livesLeft + " lives left.");
                return true;
            } else {
                //If the command sender didn't specify a name, we alert him about it and return false
                playerSender.sendMessage("Enter an username to get life number from");
            }
        }
        return false;
    }
}
