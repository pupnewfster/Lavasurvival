package com.crossge.necessities;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class Console {
    private static String aliveStatus = "Alive";
    private static UUID lastContact = null;
    private static boolean togglechat = false;
    private File configFile = new File("plugins/Necessities", "config.yml");

    public void initiate() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        aliveStatus = ChatColor.translateAlternateColorCodes('&', config.getString("Console.AliveStatus"));
    }

    public String getName() {
        Variables var = new Variables();
        return var.getMessages() + "Console [" + ChatColor.GREEN + aliveStatus + var.getMessages() + "]:";
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