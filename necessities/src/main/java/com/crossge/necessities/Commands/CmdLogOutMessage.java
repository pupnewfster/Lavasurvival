package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

@SuppressWarnings("unused")
public class CmdLogOutMessage implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String logoutMessage = "{RANK} {NAME}&r Disconnected.";
            if (args.length != 0) {
                logoutMessage = "";
                for (String arg : args)
                    logoutMessage = logoutMessage + arg + " ";
                if (!logoutMessage.contains("{NAME}"))
                    logoutMessage = "{RANK} {NAME}&r " + logoutMessage;
                logoutMessage = logoutMessage.trim();
            }
            File configFileLogOut = new File(Necessities.getInstance().getDataFolder(), "logoutmessages.yml");
            YamlConfiguration configLogOut = YamlConfiguration.loadConfiguration(configFileLogOut);
            configLogOut.set(p.getUniqueId().toString(), logoutMessage);
            try {
                configLogOut.save(configFileLogOut);
            } catch (Exception ignored) {
            }
            p.sendMessage("Logout message set to: " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                    logoutMessage.replaceAll("\\{NAME}", p.getDisplayName()).replaceAll("\\{RANK}",
                            Necessities.getUM().getUser(p.getUniqueId()).getRank().getTitle())).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console does not have a logout message.");
        return true;
    }
}
