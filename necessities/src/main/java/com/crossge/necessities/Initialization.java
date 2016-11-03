package com.crossge.necessities;

import com.crossge.necessities.Hats.HatType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;

class Initialization {
    private File configFileWarps = new File("plugins/Necessities/WorldManager", "warps.yml"), configFilePM = new File("plugins/Necessities/WorldManager", "portals.yml"),
            configFileUsers = new File("plugins/Necessities/RankManager", "users.yml"), configFileWM = new File("plugins/Necessities/WorldManager", "worlds.yml"),
            configFileLogOut = new File("plugins/Necessities", "logoutmessages.yml"), configFileLogIn = new File("plugins/Necessities", "loginmessages.yml"),
            configFileCensors = new File("plugins/Necessities", "censors.yml"), configFileSpying = new File("plugins/Necessities", "spying.yml"),
            configFileHiding = new File("plugins/Necessities", "hiding.yml"), configFileTitles = new File("plugins/Necessities", "titles.yml"), configFile = new File("plugins/Necessities", "config.yml");

    void initiateFiles() {
        dirCreate("plugins/Necessities");
        dirCreate("plugins/Necessities/Logs");
        dirCreate("plugins/Necessities/RankManager");
        dirCreate("plugins/Necessities/WorldManager");
        fileCreate("plugins/Necessities/motd.txt");
        fileCreate("plugins/Necessities/rules.txt");
        fileCreate("plugins/Necessities/faq.txt");
        createYaml();
        HatType.mapHats();

        //RankManager
        Necessities.getInstance().getRM().setRanks();
        Necessities.getInstance().getRM().setSubranks();
        Necessities.getInstance().getRM().readRanks();
        Necessities.getInstance().getSBs().createScoreboard();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(this.configFile);
        //WorldManager
        if (config.contains("Necessities.WorldManager") && config.getBoolean("Necessities.WorldManager")) {
            Necessities.getInstance().getWM().initiate();
            Necessities.getInstance().getWarps().initiate();
            Necessities.getInstance().getPM().initiate();
        }

        Necessities.getInstance().getUUID().initiate();
        Necessities.getInstance().getBot().initiate();
        Necessities.getInstance().getSpy().init();
        Necessities.getInstance().getHide().init();
        Necessities.getInstance().getWarns().initiate();
        Necessities.getInstance().getSlack().init();
        Necessities.getInstance().getAI().initiate();
    }

    private void dirCreate(String directory) {
        File d = new File(directory);
        if (!d.exists())
            d.mkdir();
    }

    private void fileCreate(String file) {
        File f = new File(file);
        if (!f.exists())
            try {
                f.createNewFile();
            } catch (Exception ignored) {
            }
    }

    private void addYML(File file) {
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (Exception ignored) {
            }
    }

    private void createYaml() {
        addYML(this.configFileTitles);
        addYML(this.configFileSpying);
        addYML(this.configFileHiding);
        addYML(this.configFileWarps);
        addYML(this.configFileLogOut);
        addYML(this.configFileLogIn);
        addYML(this.configFileUsers);
        addYML(this.configFileWM);
        addYML(this.configFilePM);
        if (!this.configFileCensors.exists())
            try {
                this.configFileCensors.createNewFile();
                YamlConfiguration config = YamlConfiguration.loadConfiguration(this.configFileCensors);
                config.set("badwords", Collections.singletonList(""));
                config.set("goodwords", Collections.singletonList(""));
                config.set("ips", Collections.singletonList(""));
                config.save(this.configFileCensors);
            } catch (Exception ignored) {
            }
        if (!configFile.exists())
            try {
                this.configFile.createNewFile();
                YamlConfiguration config = YamlConfiguration.loadConfiguration(this.configFile);
                config.set("Necessities.WorldManager", true);
                config.set("Necessities.warns", 3);
                config.set("Necessities.caps", true);
                config.set("Necessities.language", true);
                config.set("Necessities.cmdSpam", true);
                config.set("Necessities.chatSpam", true);
                config.set("Necessities.advertise", true);
                config.set("Necessities.AI", false);
                config.set("Necessities.log", false);
                config.set("Necessities.customDeny", false);
                config.set("Necessities.ChatFormat", "{TITLE} {RANK} {NAME}&4:&f {MESSAGE}");
                config.set("Necessities.firstTime", "Welcome {NAME}!");
                config.set("Console.AliveStatus", "Alive");
                config.set("Necessities.DonationPass", "password");
                config.set("Necessities.DonationServer", 9);
                config.set("Necessities.SlackToken", "token");
                config.set("Necessities.WebHook", "webHook");
                config.save(this.configFile);
            } catch (Exception ignored) {
            }
        else {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(this.configFile);
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
            if (!config.contains("Necessities.AI"))
                config.set("Necessities.AI", false);
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
            try {
                config.save(this.configFile);
            } catch (Exception ignored) {
            }
        }
    }
}