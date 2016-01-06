package com.crossge.necessities.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class CmdLogOutMessage extends Cmd {
    private File configFileLogOut = new File("plugins/Necessities", "logoutmessages.yml");

    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String logoutmessage = "{RANK} {NAME}&r Disconnected.";
            if (args.length != 0) {
                logoutmessage = "";
                for (String arg : args) logoutmessage = logoutmessage + arg + " ";
                if (!logoutmessage.contains("{NAME}"))
                    logoutmessage = "{RANK} {NAME}&r " + logoutmessage;
                logoutmessage = logoutmessage.trim();
            }
            YamlConfiguration configLogOut = YamlConfiguration.loadConfiguration(configFileLogOut);
            configLogOut.set(p.getUniqueId().toString(), logoutmessage);
            try {
                configLogOut.save(configFileLogOut);
            } catch (Exception e) {
            }
            p.sendMessage("Logout message set to: " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                    logoutmessage.replaceAll("\\{NAME\\}", p.getDisplayName()).replaceAll("\\{RANK\\}",
                            um.getUser(p.getUniqueId()).getRank().getTitle())).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console does not have a logout message.");
        return true;
    }
}