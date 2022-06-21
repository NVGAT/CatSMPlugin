package com.notverygoodatthis;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getLogger;

public class CommandKit implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            //Revival command
            Player playerSender = (Player) sender;
            if(label.length() != 0) {
                String revivalName = args[0];
                //Logs the revived player's name
                getLogger().info("the name of the provided player is " + revivalName);
                getLogger().info("Held item name: " + playerSender.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
                getLogger().info("Revival item name: " + SMPlugin.REVIVAL_ITEM_NAME);
                try {
                    //Checks if the currently held item is the revival item
                    ItemStack heldItem = playerSender.getInventory().getItemInMainHand();
                    if(heldItem.getType() == Material.PLAYER_HEAD && heldItem.getItemMeta().getDisplayName().equals(SMPlugin.REVIVAL_ITEM_NAME)) {
                        //If it checks out, we unban the desired player
                        Bukkit.getBanList(BanList.Type.NAME).pardon(revivalName);
                        //We remove the used revival item
                        playerSender.getInventory().getItemInMainHand().setAmount(playerSender.getInventory().getItemInMainHand().getAmount() - 1);
                        //We reset the revived player's death count
                        OfflinePlayer revivalPlayer = Bukkit.getOfflinePlayer(revivalName);
                        revivalPlayer.setStatistic(Statistic.DEATHS, 0);
                        //Then we notify the command executor that the player has been successfully revived, and that if there are any issues to contact me on Discord
                        playerSender.sendMessage(revivalName + " has been successfully revived. If there is a bug or you made a typo contact NotVeryGoodAtThis#8575 on Discord.");
                    } else {
                        //If the player isn't holding a revival head while reviving someone then we notify them about it
                        playerSender.sendMessage("Hold a revival item in your hand to revive someone");
                    }
                } catch(NullPointerException e) {
                    //If we get a NullPointerException that means that the Bukkit.getOfflinePlayer() function didn't return a player name.
                    //That could either mean that the player hasn't been online at all or isn't banned. Either way, they don't need a revival
                    playerSender.sendMessage("That player either hasn't been online on this server at all or they don't need a revival.");
                }
            }
        }
        return true;
    }
}
