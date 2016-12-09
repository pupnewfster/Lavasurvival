package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

@SuppressWarnings("unused")
public class CmdLogInMessage implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String loginMessage = "{RANK} {NAME}&r joined the game.";
            if (args.length != 0) {
                loginMessage = "";
                for (String arg : args)
                    loginMessage = loginMessage + arg + " ";
                if (!loginMessage.contains("{NAME}"))
                    loginMessage = "{RANK} {NAME}&r " + loginMessage;
                loginMessage = loginMessage.trim();
            }
            File configFileLogIn = new File(Necessities.getInstance().getDataFolder(), "loginmessages.yml");
            YamlConfiguration configLogIn = YamlConfiguration.loadConfiguration(configFileLogIn);
            configLogIn.set(p.getUniqueId().toString(), loginMessage);
            try {
                configLogIn.save(configFileLogIn);
            } catch (Exception ignored) {
            }
            p.sendMessage("Login message set to: " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                    loginMessage.replaceAll("\\{NAME}", p.getDisplayName()).replaceAll("\\{RANK}",
                            Necessities.getUM().getUser(p.getUniqueId()).getRank().getTitle())).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console does not have a login message.");
        return true;
    }
}