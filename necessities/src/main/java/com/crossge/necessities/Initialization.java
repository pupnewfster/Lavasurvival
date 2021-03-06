package com.crossge.necessities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collections;
import java.util.Map;

class Initialization {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void initiateFiles() {
        dirCreate("plugins/Necessities");
        dirCreate("plugins/Necessities/Logs");
        dirCreate("plugins/Necessities/RankManager");
        dirCreate("plugins/Necessities/WorldManager");
        fileCreate("plugins/Necessities/motd.txt");
        fileCreate("plugins/Necessities/rules.txt");
        fileCreate("plugins/Necessities/faq.txt");
        fileCreate("plugins/Necessities/announcements.txt");
        createYaml();

        YamlConfiguration config = Necessities.getInstance().getConfig();
        if (config.contains("Necessities.customDeny") && config.getBoolean("Necessities.customDeny")) //At the moment only is checked on startup
            Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> {
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Setting custom deny messages.");
                String msg = Necessities.getVar().getEr() + "Error: " + Necessities.getVar().getErMsg() + "You do not have permission to perform this command.";
                for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                    try {
                        Map<String, Map<String, Object>> cmds = p.getDescription().getCommands();
                        if (cmds != null)
                            for (String k : cmds.keySet()) {
                                PluginCommand pc = Bukkit.getPluginCommand(p.getName() + ":" + k);
                                if (pc != null)
                                    pc.setPermissionMessage(msg);
                            }
                    } catch (Exception ignored) {
                    }
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Custom deny messages set.");
            });

        //RankManager
        Necessities.getRM().setRanks();
        Necessities.getRM().setSubranks();
        Necessities.getRM().readRanks();
        Necessities.getSBs().createScoreboard();

        //WorldManager
        if (config.contains("Necessities.WorldManager") && config.getBoolean("Necessities.WorldManager"))
            Necessities.getWM().initiate();

        Necessities.getBot().initiate();
        Necessities.getSpy().init();
        Necessities.getHide().init();
        Necessities.getWarns().initiate();
        Necessities.getSlack().init();
        Necessities.getAnnouncer().init();
        Necessities.getEconomy().init();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void dirCreate(String directory) {
        File d = new File(directory);
        if (!d.exists())
            d.mkdir();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void fileCreate(String file) {
        File f = new File(file);
        if (!f.exists())
            try {
                f.createNewFile();
            } catch (Exception ignored) {
            }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void addYML(File file) {
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (Exception ignored) {
            }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createYaml() {
        addYML(new File("plugins/Necessities", "titles.yml"));
        addYML(new File("plugins/Necessities", "spying.yml"));
        addYML(new File("plugins/Necessities", "hiding.yml"));
        addYML(new File("plugins/Necessities", "loginmessages.yml"));
        addYML(new File("plugins/Necessities", "logoutmessages.yml"));
        addYML(new File("plugins/Necessities/RankManager", "users.yml"));
        addYML(new File("plugins/Necessities/WorldManager", "worlds.yml"));
        File configFileCensors = new File("plugins/Necessities", "censors.yml");
        if (!configFileCensors.exists())
            try {
                configFileCensors.createNewFile();
                YamlConfiguration config = YamlConfiguration.loadConfiguration(configFileCensors);
                config.set("badwords", Collections.singletonList(""));
                config.set("goodwords", Collections.singletonList(""));
                config.set("ips", Collections.singletonList(""));
                config.save(configFileCensors);
            } catch (Exception ignored) {
            }
        if (!Necessities.getInstance().getConfigFile().exists())
            try {
                Necessities.getInstance().getConfigFile().createNewFile();
                YamlConfiguration config = Necessities.getInstance().getConfig();
                config.set("Necessities.WorldManager", true);
                config.set("Necessities.warns", 3);
                config.set("Necessities.caps", true);
                config.set("Necessities.language", true);
                config.set("Necessities.cmdSpam", true);
                config.set("Necessities.chatSpam", true);
                config.set("Necessities.advertise", true);
                config.set("Necessities.log", false);
                config.set("Necessities.customDeny", false);
                config.set("Necessities.ChatFormat", "{TITLE} {RANK} {NAME}&4:&f {MESSAGE}");
                config.set("Necessities.firstTime", "Welcome {NAME}!");
                config.set("Console.AliveStatus", "Alive");
                config.set("Necessities.DonationPass", "password");
                config.set("Necessities.DonationServer", 9);
                config.set("Necessities.SlackToken", "token");
                config.set("Necessities.WebHook", "webHook");
                config.set("Lavasurvival.DBHost", "127.0.0.1:3306");
                config.set("Lavasurvival.DBTable", "lavasurvival");
                config.set("Lavasurvival.DBUser", "lsuser");
                config.set("Lavasurvival.DBPassword", "password");
                config.set("Announcements.frequency", 5);
                config.save(Necessities.getInstance().getConfigFile());
            } catch (Exception ignored) {
            }
        else {
            YamlConfiguration config = Necessities.getInstance().getConfig();
            if (!config.contains("Necessities.warns"))
                config.set("Necessities.warns", 3);
            if (!config.contains("Necessities.caps"))
                config.set("Necessities.caps", true);
            if (!config.contains("Necessities.language"))
                config.set("Necessities.language", true);
            if (!config.contains("Necessities.cmdSpam"))
                config.set("Necessities.cmdSpam", true);
            if (!config.contains("Necessities.chatSpam"))
                config.set("Necessities.chatSpam", true);
            if (!config.contains("Necessities.advertise"))
                config.set("Necessities.advertise", true);
            if (!config.contains("Necessities.ChatFormat"))
                config.set("Necessities.ChatFormat", "{TITLE} {RANK} {NAME}: {MESSAGE}");
            if (!config.contains("Necessities.firstTime"))
                config.set("Necessities.firstTime", "Welcome {NAME}!");
            if (!config.contains("Console.AliveStatus"))
                config.set("Console.AliveStatus", "Alive");
            if (!config.contains("Necessities.WorldManager"))
                config.set("Necessities.WorldManager", true);
            if (!config.contains("Necessities.log"))
                config.set("Necessities.log", false);
            if (!config.contains("Necessities.customDeny"))
                config.set("Necessities.customDeny", false);
            if (!config.contains("Necessities.DonationPass"))
                config.set("Necessities.DonationPass", "password");
            if (!config.contains("Necessities.DonationServer"))
                config.set("Necessities.DonationServer", 9);
            if (!config.contains("Necessities.SlackToken"))
                config.set("Necessities.SlackToken", "token");
            if (!config.contains("Necessities.WebHook"))
                config.set("Necessities.WebHook", "webHook");
            if (!config.contains("Lavasurvival.DBHost"))
                config.set("Lavasurvival.DBHost", "127.0.0.1:3306");
            if (!config.contains("Lavasurvival.DBTable"))
                config.set("Lavasurvival.DBTable", "lavasurvival");
            if (!config.contains("Lavasurvival.DBUser"))
                config.set("Lavasurvival.DBUser", "lsuser");
            if (!config.contains("Lavasurvival.DBPassword"))
                config.set("Lavasurvival.DBPassword", "password");
            if (!config.contains("Announcements.frequency"))
                config.set("Announcements.frequency", 5);
            try {
                config.save(Necessities.getInstance().getConfigFile());
            } catch (Exception ignored) {
            }
        }
    }
}