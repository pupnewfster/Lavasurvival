package com.crossge.necessities.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CmdMotd extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        File f = new File("plugins/Necessities/motd.txt");
        if (!f.exists()) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The file does not exist somehow...");
            return true;
        }
        try {
            BufferedReader read = new BufferedReader(new FileReader(f));
            String line;
            boolean hasText = false;
            while ((line = read.readLine()) != null)
                if (!line.equals("")) {
                    hasText = true;
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                }
            if (!hasText)
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "No server motd set.");
            read.close();
        } catch (Exception e) {
        }
        return true;
    }
}