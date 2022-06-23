package com.notverygoodatthis;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Scanner;

public class CreditsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Scanner scanner = new Scanner("./src/main/java/com/notverygoodatthis/creditscommand.txt");
        while(scanner.hasNextLine()) {
            sender.sendMessage(scanner.nextLine());
        }
        return false;
    }
}
