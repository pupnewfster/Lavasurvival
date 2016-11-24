package com.crossge.necessities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

class Announcer { //TODO: add a command to reload the list of messages
    private BukkitRunnable announcerTask;
    private final ArrayList<String> messages = new ArrayList<>();
    private final Random r = new Random();

    void init() {
        try (BufferedReader read = new BufferedReader(new FileReader(new File("plugins/Necessities/announcements.txt")))) {
            String line;
            while ((line = read.readLine()) != null)
                this.messages.add(ChatColor.translateAlternateColorCodes('&', line));
        } catch (Exception ignored) {
        }
        if (this.messages.isEmpty())
            return;
        this.announcerTask = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(messages.get(r.nextInt(messages.size())));
            }
        };
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Necessities", "config.yml"));
        this.announcerTask.runTaskTimerAsynchronously(Necessities.getInstance(), 0, 20 * 60 * config.getInt("Announcements.frequency"));
    }

    void exit() {
        this.announcerTask.cancel();
    }
}