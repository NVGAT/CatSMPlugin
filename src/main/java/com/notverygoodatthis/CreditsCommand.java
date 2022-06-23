package com.notverygoodatthis;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreditsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            player.playSound(player.getLocation(), Sound.MUSIC_CREDITS, 1, 1);
            for(String item : SMPlugin.creditsMessage) {
                player.sendMessage(item);
            }
            player.stopAllSounds();
        }
        return false;
    }
}
