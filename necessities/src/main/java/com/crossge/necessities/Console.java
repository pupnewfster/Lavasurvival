package com.crossge.necessities;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

public class Console {
    private boolean togglechat;
    private UUID lastContact;

    public String getName() {
        Variables var = new Variables();
        YamlConfiguration config = Necessities.getInstance().getConfig();
        return var.getMessages() + "Console [" + ChatColor.GREEN + (config.contains("Console.AliveStatus") ? ChatColor.translateAlternateColorCodes('&', config.getString("Console.AliveStatus")) : "Alive") +
                var.getMessages() + "]:" + ChatColor.RESET;
    }

    public void chatToggle() {
        this.togglechat = !this.togglechat;
    }

    public boolean chatToggled() {
        return this.togglechat;
    }

    public UUID getLastContact() {
        return this.lastContact;
    }

    public void setLastContact(UUID uuid) {
        this.lastContact = uuid;
    }
}