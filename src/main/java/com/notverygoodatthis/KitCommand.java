package com.notverygoodatthis;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Gives the OP kit to an operator who executed this command
        Player player = (Player) sender;
        Inventory inv = player.getInventory();
        if(player.isOp()) {
            ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
            helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
            helmet.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
            helmet.addUnsafeEnchantment(Enchantment.MENDING, 2);
            helmet.addUnsafeEnchantment(Enchantment.OXYGEN, 5);
            helmet.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
            inv.addItem(helmet);
            
            ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
            chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
            chestplate.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
            chestplate.addUnsafeEnchantment(Enchantment.MENDING, 2);
            inv.addItem(chestplate);
            
            ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
            leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
            leggings.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
            leggings.addUnsafeEnchantment(Enchantment.MENDING, 2);
            inv.addItem(leggings);
            
            ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
            boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
            boots.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
            boots.addUnsafeEnchantment(Enchantment.MENDING, 2);
            boots.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 6);
            boots.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 5);
            inv.addItem(boots);
            
            ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
            sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
            sword.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 5);
            sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 5);
            sword.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
            sword.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, 4);
            sword.addUnsafeEnchantment(Enchantment.MENDING, 2);
            inv.addItem(sword);
            
            ItemStack axe = new ItemStack(Material.NETHERITE_AXE);
            axe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
            axe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
            axe.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 5);
            axe.addUnsafeEnchantment(Enchantment.MENDING, 2);
            inv.addItem(axe);

            player.sendMessage("You are all kitted out!");
        } else {
            player.sendMessage("This is an operator only command");
        }
        return false;
    }
}
