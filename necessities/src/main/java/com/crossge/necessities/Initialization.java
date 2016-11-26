package com.crossge.necessities;

import com.crossge.necessities.Hats.HatType;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;

class Initialization {
    private final File configFileWarps = new File("plugins/Necessities/WorldManager", "warps.yml");
    private final File configFilePM = new File("plugins/Necessities/WorldManager", "portals.yml");
    private final File configFileUsers = new File("plugins/Necessities/RankManager", "users.yml");
    private final File configFileWM = new File("plugins/Necessities/WorldManager", "worlds.yml");
    private final File configFileLogOut = new File("plugins/Necessities", "logoutmessages.yml");
    private final File configFileLogIn = new File("plugins/Necessities", "loginmessages.yml");
    private final File configFileCensors = new File("plugins/Necessities", "censors.yml");
    private final File configFileSpying = new File("plugins/Necessities", "spying.yml");
    private final File configFileHiding = new File("plugins/Necessities", "hiding.yml");
    private final File configFileTitles = new File("plugins/Necessities", "titles.yml");

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
        File cwords = new File("plugins/Necessities", "customWords.txt");
        if (!cwords.exists())
            try {
                cwords.createNewFile();
                FileUtils.copyURLToFile(getClass().getResource("/customWords.txt"), cwords);
            } catch (Exception ignored) {
            }
        createYaml();
        HatType.mapHats();

        //RankManager
        Necessities.getInstance().getRM().setRanks();
        Necessities.getInstance().getRM().setSubranks();
        Necessities.getInstance().getRM().readRanks();
        Necessities.getInstance().getSBs().createScoreboard();

        YamlConfiguration config = Necessities.getInstance().getConfig();
        //WorldManager
        if (config.contains("Necessities.WorldManager") && config.getBoolean("Necessities.WorldManager")) {
            Necessities.getInstance().getWM().initiate();
            Necessities.getInstance().getWarps().initiate();
            Necessities.getInstance().getPM().initiate();
        }

        Necessities.getInstance().getNet().readCustom();
        Necessities.getInstance().getUUID().initiate();
        Necessities.getInstance().getBot().initiate();
        Necessities.getInstance().getSpy().init();
        Necessities.getInstance().getHide().init();
        Necessities.getInstance().getWarns().initiate();
        Necessities.getInstance().getSlack().init();
        Necessities.getInstance().getAI().initiate();
        Necessities.getInstance().getAnnouncer().init();
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