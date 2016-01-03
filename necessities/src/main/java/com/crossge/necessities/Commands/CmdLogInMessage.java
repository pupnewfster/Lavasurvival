package com.crossge.necessities.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class CmdLogInMessage extends Cmd {
    private File configFileLogIn = new File("plugins/Necessities", "loginmessages.yml");

    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String loginmessage = "{RANK} {NAME}&r joined the game.";
            if (args.length != 0) {
                loginmessage = "";
                for (String arg : args)
                    loginmessage = loginmessage + arg + " ";
                if (!loginmessage.contains("\\{NAME\\}"))
                    loginmessage = "{RANK} {NAME}&r " + loginmessage;
                loginmessage = loginmessage.trim();
            }
            YamlConfiguration configLogIn = YamlConfiguration.loadConfiguration(configFileLogIn);
            configLogIn.set(p.getUniqueId().toString(), loginmessage);
            try {
                configLogIn.save(configFileLogIn);
            } catch (Exception e) {
                e.printStackTrace();
            }
            p.sendMessage("Login message set to: " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                    loginmessage.replaceAll("\\{NAME\\}", p.getDisplayName()).replaceAll("\\{RANK\\}",
                            um.getUser(p.getUniqueId()).getRank().getTitle())).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console does not have a login message.");
        return true;
    }
}