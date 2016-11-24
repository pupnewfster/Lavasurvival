package com.crossge.necessities;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class Console {
    private static boolean togglechat = false;
    private static UUID lastContact = null;
    private final File configFile = new File("plugins/Necessities", "config.yml");

    public String getName() {
        Variables var = new Variables();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        return var.getMessages() + "Console [" + ChatColor.GREEN + (config.contains("Console.AliveStatus") ? ChatColor.translateAlternateColorCodes('&', config.getString("Console.AliveStatus")) : "Alive") +
                var.getMessages() + "]:" + ChatColor.RESET;
    }

    public void chatToggle() {
        togglechat = !togglechat;
    }

    public boolean chatToggled() {
        return togglechat;
    }

    public UUID getLastContact() {
        return lastContact;
    }

    public void setLastContact(UUID uuid) {
        lastContact = uuid;
    }
}