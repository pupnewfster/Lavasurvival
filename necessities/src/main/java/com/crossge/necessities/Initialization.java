package com.crossge.necessities;

import com.crossge.necessities.Commands.CmdCommandSpy;
import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.Hats.HatType;
import com.crossge.necessities.Janet.Janet;
import com.crossge.necessities.Janet.JanetAI;
import com.crossge.necessities.Janet.JanetSlack;
import com.crossge.necessities.Janet.JanetWarn;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.WorldManager.PortalManager;
import com.crossge.necessities.WorldManager.WarpManager;
import com.crossge.necessities.WorldManager.WorldManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;

public class Initialization {
    private File configFileWarps = new File("plugins/Necessities/WorldManager", "warps.yml"), configFilePM = new File("plugins/Necessities/WorldManager", "portals.yml"),
            configFileUsers = new File("plugins/Necessities/RankManager", "users.yml"), configFileWM = new File("plugins/Necessities/WorldManager", "worlds.yml"),
            configFileLogOut = new File("plugins/Necessities", "logoutmessages.yml"), configFileLogIn = new File("plugins/Necessities", "loginmessages.yml"),
            configFileCensors = new File("plugins/Necessities", "censors.yml"), configFileSpying = new File("plugins/Necessities", "spying.yml"),
            configFileHiding = new File("plugins/Necessities", "hiding.yml"), configFileTitles = new File("plugins/Necessities", "titles.yml"), configFile = new File("plugins/Necessities", "config.yml");
    CmdCommandSpy cs = new CmdCommandSpy();
    PortalManager pm = new PortalManager();
    WarpManager warps = new WarpManager();
    WorldManager wm = new WorldManager();
    JanetSlack slack = new JanetSlack();
    ScoreBoards sb = new ScoreBoards();
    RankManager rm = new RankManager();
    JanetWarn warns = new JanetWarn();
    Console console = new Console();
    CmdHide hide = new CmdHide();
    GetUUID get = new GetUUID();
    JanetAI ai = new JanetAI();
    Janet bot = new Janet();

    public void initiateFiles() {
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
        rm.setRanks();
        rm.setSubranks();
        rm.readRanks();
        sb.createScoreboard();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        //WorldManager
        if (config.contains("Necessities.WorldManager") && config.getBoolean("Necessities.WorldManager")) {
            wm.initiate();
            warps.initiate();
            pm.initiate();
        }

        get.initiate();
        bot.initiate();
        cs.init();
        hide.init();
        warns.initiate();
        slack.init();
        ai.initiate();
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
            } catch (Exception e) {
            }
    }

    private void addYML(File file) {
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (Exception e) {
            }
    }

    private void createYaml() {
        addYML(configFileTitles);
        addYML(configFileSpying);
        addYML(configFileHiding);
        addYML(configFileWarps);
        addYML(configFileLogOut);
        addYML(configFileLogIn);
        addYML(configFileUsers);
        addYML(configFileWM);
        addYML(configFilePM);
        if (!configFileCensors.exists())
            try {
                configFileCensors.createNewFile();
                YamlConfiguration config = YamlConfiguration.loadConfiguration(configFileCensors);
                config.set("badwords", Arrays.asList(""));
                config.set("goodwords", Arrays.asList(""));
                config.set("ips", Arrays.asList(""));
                config.save(configFileCensors);
            } catch (Exception e) {
            }
        if (!configFile.exists())
            try {
                configFile.createNewFile();
                YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
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
                config.set("Necessities.SlackChanel", "channel");
                config.set("Necessities.ChannelID", "channelID");
                config.set("Necessities.WebHook", "webHook");
                config.save(configFile);
            } catch (Exception e) {
            }
        else {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
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
            if (!config.contains("Necessities.SlackChanel"))
                config.set("Necessities.SlackChanel", "channel");
            if (!config.contains("Necessities.ChannelID"))
                config.set("Necessities.ChannelID", "channelID");
            if (!config.contains("Necessities.WebHook"))
                config.set("Necessities.WebHook", "webHook");
            try {
                config.save(configFile);
            } catch (Exception e) {
            }
        }
    }
}