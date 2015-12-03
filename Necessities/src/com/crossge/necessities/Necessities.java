package com.crossge.necessities;

import com.TentacleLabs.GoogleAnalyticsPlugin.GoogleAnalyticsPlugin;
import com.TentacleLabs.GoogleAnalyticsPlugin.Tracker;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.*;
import com.crossge.necessities.Commands.*;
import com.crossge.necessities.Commands.RankManager.*;
import com.crossge.necessities.Commands.WorldManager.*;
import com.crossge.necessities.Janet.Janet;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class Necessities extends JavaPlugin {
    private ProtocolManager protocolManager;
    private Tracker googleAnalyticsTracker;
    private WrappedSignedProperty skin;
    private static Necessities instance;
    private UUID janetID;
    private File configFile = new File("plugins/Necessities", "config.yml");
    UserManager um = new UserManager();
    RankManager rm = new RankManager();
    DonationReader dr = new DonationReader();

    public static Necessities getInstance() {
        return instance;
    }

    public boolean isProtocolLibLoaded() {
        return this.protocolManager != null;
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabling Necessities...");
        instance = this;

        if (!hookGoogle())
            getLogger().warning("Could not hook into Google Analytics!");

        janetID = UUID.randomUUID();
        try {
            this.protocolManager = ProtocolLibrary.getProtocolManager();
        } catch (Exception e) {}//Not using protocollib
        Initialization init = new Initialization();
        init.initiateFiles();
        getServer().getPluginManager().registerEvents(new Listeners(), this);

        dr.init();
        getLogger().info("Necessities enabled.");
    }

    private boolean hookGoogle() {
        GoogleAnalyticsPlugin plugin;
        if ((plugin = (GoogleAnalyticsPlugin)getServer().getPluginManager().getPlugin("GoogleAnalyticsPlugin")) == null)
            return false;
        googleAnalyticsTracker = plugin.getTracker();
        return true;
    }

    public static boolean isTracking() {
        return getInstance().googleAnalyticsTracker != null;
    }

    public static Tracker getTracker() {
        return getInstance().googleAnalyticsTracker;
    }

    public void removePlayer(Player p) {
        if (this.protocolManager != null)
            try {
                PacketContainer tabList = this.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO, true);
                StructureModifier<List<PlayerInfoData>> infoData = tabList.getPlayerInfoDataLists();
                StructureModifier<EnumWrappers.PlayerInfoAction> infoAction = tabList.getPlayerInfoAction();
                List<PlayerInfoData> playerInfo = infoData.read(0);
                playerInfo.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(p), 0, EnumWrappers.NativeGameMode.fromBukkit(p.getGameMode()), WrappedChatComponent.fromText(p.getName())));
                infoData.write(0, playerInfo);
                infoAction.write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                for (Player x : Bukkit.getOnlinePlayers())
                    if (!x.canSee(p) && !x.equals(p))
                        this.protocolManager.sendServerPacket(x, tabList);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void addPlayer(Player p) {
        if (this.protocolManager != null)
            try {
                PacketContainer tabList = this.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO, true);
                StructureModifier<List<PlayerInfoData>> infoData = tabList.getPlayerInfoDataLists();
                StructureModifier<EnumWrappers.PlayerInfoAction> infoAction = tabList.getPlayerInfoAction();
                List<PlayerInfoData> playerInfo = infoData.read(0);
                User u = um.getUser(p.getUniqueId());
                playerInfo.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(p), 0, EnumWrappers.NativeGameMode.fromBukkit(p.getGameMode()),
                        WrappedChatComponent.fromText((u.getRank() == null ? "" : ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle() + " ")) + p.getDisplayName())));

                infoData.write(0, playerInfo);
                infoAction.write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                for (Player x : Bukkit.getOnlinePlayers())
                    if (!x.hasPermission("Necessities.seehidden") && x.canSee(p) && !x.equals(p))
                        this.protocolManager.sendServerPacket(x, tabList);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void updateName(Player p) {
        if (this.protocolManager != null)
            try {
                PacketContainer tabList = this.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO, true);
                StructureModifier<List<PlayerInfoData>> infoData = tabList.getPlayerInfoDataLists();
                StructureModifier<EnumWrappers.PlayerInfoAction> infoAction = tabList.getPlayerInfoAction();
                List<PlayerInfoData> playerInfo = infoData.read(0);
                User u = um.getUser(p.getUniqueId());
                playerInfo.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(p), 0, EnumWrappers.NativeGameMode.fromBukkit(p.getGameMode()),
                        WrappedChatComponent.fromText((u.getRank() == null ? "" : ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle() + " ")) + p.getDisplayName())));
                infoData.write(0, playerInfo);
                infoAction.write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
                for (Player x : Bukkit.getOnlinePlayers())
                    this.protocolManager.sendServerPacket(x, tabList);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void updateAll(Player x) {
        if (this.protocolManager != null)
            try {
                PacketContainer tabList = this.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO, true);
                StructureModifier<List<PlayerInfoData>> infoData = tabList.getPlayerInfoDataLists();
                StructureModifier<EnumWrappers.PlayerInfoAction> infoAction = tabList.getPlayerInfoAction();
                List<PlayerInfoData> playerInfo = infoData.read(0);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    User u = um.getUser(p.getUniqueId());
                    playerInfo.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(p), 0, EnumWrappers.NativeGameMode.fromBukkit(p.getGameMode()),
                            WrappedChatComponent.fromText((u.getRank() == null ? "" : ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle() + " ")) + p.getDisplayName())));
                }
                infoData.write(0, playerInfo);
                infoAction.write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
                this.protocolManager.sendServerPacket(x, tabList);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void addJanet(Player p) {
        if (this.protocolManager != null)
            try {
                PacketContainer tabList = this.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO, true);
                StructureModifier<List<PlayerInfoData>> infoData = tabList.getPlayerInfoDataLists();
                StructureModifier<EnumWrappers.PlayerInfoAction> infoAction = tabList.getPlayerInfoAction();
                List<PlayerInfoData> playerInfo = infoData.read(0);
                WrappedGameProfile janetProfile = new WrappedGameProfile(janetID, "Janet");
                if (this.skin == null)
                    this.skin = getSkin();
                if (this.skin != null)
                    janetProfile.getProperties().put("textures", this.skin);
                playerInfo.add(new PlayerInfoData(janetProfile, 0, EnumWrappers.NativeGameMode.CREATIVE,
                        WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', rm.getRank(rm.getOrder().size() - 1).getTitle() + " ") + "Janet")));
                infoData.write(0, playerInfo);
                infoAction.write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                this.protocolManager.sendServerPacket(p, tabList);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void refreshJanet(Player p) {
        if (this.protocolManager != null)
            try {
                PacketContainer tabList = this.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO, true);
                StructureModifier<List<PlayerInfoData>> infoData = tabList.getPlayerInfoDataLists();
                StructureModifier<EnumWrappers.PlayerInfoAction> infoAction = tabList.getPlayerInfoAction();
                List<PlayerInfoData> playerInfo = infoData.read(0);
                WrappedGameProfile janetProfile = new WrappedGameProfile(janetID, "Janet");
                if (this.skin == null)
                    this.skin = getSkin();
                if (this.skin != null)
                    janetProfile.getProperties().put("textures", this.skin);
                playerInfo.add(new PlayerInfoData(janetProfile, 0, EnumWrappers.NativeGameMode.CREATIVE,
                        WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', rm.getRank(rm.getOrder().size() - 1).getTitle() + " ") + "Janet")));
                infoData.write(0, playerInfo);
                infoAction.write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
                this.protocolManager.sendServerPacket(p, tabList);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void addHeader(Player p) {
        if (this.protocolManager != null)
            try {
                PacketContainer tabList = this.protocolManager.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
                StructureModifier<WrappedChatComponent> chatStuff = tabList.getChatComponents();
                chatStuff.write(0, WrappedChatComponent.fromText(ChatColor.GREEN + "GamezGalaxy"));
                chatStuff.write(1, WrappedChatComponent.fromText(ChatColor.BLUE + "http://gamezgalaxy.com"));
                this.protocolManager.sendServerPacket(p, tabList);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private WrappedSignedProperty getSkin() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" +
                    UUID.fromString("136f2ba6-2be3-444c-a296-8ec597edb57e").toString().replaceAll("-", "") + "?unsigned=false").openConnection().getInputStream()));
            String value = "";
            String signature = "";
            int count = 0;
            for (char c : in.readLine().toCharArray()) {
                if (c == '"')
                    count++;
                else if (count == 17)
                    value += c;
                else if (count == 21)
                    signature += c;
                if (count > 21)
                    break;
            }
            in.close();
            return new WrappedSignedProperty("textures", value, signature);
        } catch (Exception ignored) { }
        return null;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        return getCmd(cmd.getName()).commandUse(sender, args);
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (sender == null || cmd == null)
            return null;
        List<String> tab = getCmd(cmd.getName()).tabComplete(sender, args);
        if (tab == null || tab.isEmpty())
            return null;
        return tab;
    }

    private boolean isEqual(String command, String tocheck) {
        return command.equalsIgnoreCase(tocheck);
    }

    private Cmd getCmd(String name) {
        Cmd com = new Cmd();
        if (isEqual(name, "slap"))
            com = new CmdSlap();
        else if (isEqual(name, "warn"))
            com = new CmdWarn();
        else if (isEqual(name, "ragequit"))
            com = new CmdRagequit();
        else if (isEqual(name, "devs"))
            com = new CmdDevs();
        else if (isEqual(name, "loginmessage"))
            com = new CmdLogInMessage();
        else if (isEqual(name, "logoutmessage"))
            com = new CmdLogOutMessage();
        else if (isEqual(name, "hat"))
            com = new CmdHat();
        else if (isEqual(name, "hide"))
            com = new CmdHide();
        else if (isEqual(name, "title"))
            com = new CmdTitle();
        else if (isEqual(name, "bracketcolor"))
            com = new CmdBracketColor();
        else if (isEqual(name, "commandspy"))
            com = new CmdCommandSpy();
        else if (isEqual(name, "gamemode"))
            com = new CmdGamemode();
        else if (isEqual(name, "fly"))
            com = new CmdFly();
        else if (isEqual(name, "who"))
            com = new CmdWho();
        else if (isEqual(name, "me"))
            com = new CmdMe();
        else if (isEqual(name, "nick"))
            com = new CmdNick();
        else if (isEqual(name, "kick"))
            com = new CmdKick();
        else if (isEqual(name, "top"))
            com = new CmdTop();
        else if (isEqual(name, "suicide"))
            com = new CmdSuicide();
        else if (isEqual(name, "mute"))
            com = new CmdMute();
        else if (isEqual(name, "motd"))
            com = new CmdMotd();
        else if (isEqual(name, "skull"))
            com = new CmdSkull();
        else if (isEqual(name, "help"))
            com = new CmdHelp();
        else if (isEqual(name, "ignore"))
            com = new CmdIgnore();
        else if (isEqual(name, "ignore"))
            com = new CmdIgnore();
        else if (isEqual(name, "msg"))
            com = new CmdMsg();
        else if (isEqual(name, "reply"))
            com = new CmdReply();
        else if (isEqual(name, "say"))
            com = new CmdSay();
        else if (isEqual(name, "togglechat"))
            com = new CmdToggleChat();
        else if (isEqual(name, "ban"))
            com = new CmdBan();
        else if (isEqual(name, "tempban"))
            com = new CmdTempban();
        else if (isEqual(name, "unban"))
            com = new CmdUnban();
        else if (isEqual(name, "banip"))
            com = new CmdBanIP();
        else if (isEqual(name, "unbanip"))
            com = new CmdUnbanIP();
        else if (isEqual(name, "tp"))
            com = new CmdTp();
        else if (isEqual(name, "tphere"))
            com = new CmdTphere();
        else if (isEqual(name, "tppos"))
            com = new CmdTppos();
        else if (isEqual(name, "faq"))
            com = new CmdFaq();
        else if (isEqual(name, "opbroadcast"))
            com = new CmdOpChat();
            //RankManager
        else if (isEqual(name, "promote"))
            com = new CmdPromote();
        else if (isEqual(name, "demote"))
            com = new CmdDemote();
        else if (isEqual(name, "setrank"))
            com = new CmdSetrank();
        else if (isEqual(name, "addpermission"))
            com = new CmdAddPermission();
        else if (isEqual(name, "delpermission"))
            com = new CmdDelPermission();
        else if (isEqual(name, "addpermsubrank"))
            com = new CmdAddPermSubrank();
        else if (isEqual(name, "delpermsubrank"))
            com = new CmdDelPermSubrank();
        else if (isEqual(name, "addpermissionuser"))
            com = new CmdAddPermissionUser();
        else if (isEqual(name, "delpermissionuser"))
            com = new CmdDelPermissionUser();
        else if (isEqual(name, "addsubrank"))
            com = new CmdAddSubrank();
        else if (isEqual(name, "delsubrank"))
            com = new CmdDelSubrank();
        else if (isEqual(name, "addsubrankuser"))
            com = new CmdAddSubrankUser();
        else if (isEqual(name, "delsubrankuser"))
            com = new CmdDelSubrankUser();
        else if (isEqual(name, "createsubrank"))
            com = new CmdCreateSubrank();
        else if (isEqual(name, "removesubrank"))
            com = new CmdRemoveSubrank();
        else if (isEqual(name, "createrank"))
            com = new CmdCreateRank();
        else if (isEqual(name, "removerank"))
            com = new CmdRemoveRank();
        else if (isEqual(name, "whois"))
            com = new CmdWhois();
        else if (isEqual(name, "ranks"))
            com = new CmdRanks();
        else if (isEqual(name, "subranks"))
            com = new CmdSubranks();
        else if (isEqual(name, "rankcmds"))
            com = new CmdRankCmds();
        else if (isEqual(name, "reloadpermissions"))
            com = new CmdReloadPermissions();
            //WorldManager
        else if (isEqual(name, "createworld"))
            com = new CmdCreateWorld();
        else if (isEqual(name, "worldspawn"))
            com = new CmdWorldSpawn();
        else if (isEqual(name, "loadworld"))
            com = new CmdLoadWorld();
        else if (isEqual(name, "unloadworld"))
            com = new CmdUnloadWorld();
        else if (isEqual(name, "removeworld"))
            com = new CmdRemoveWorld();
        else if (isEqual(name, "worlds"))
            com = new CmdWorlds();
        else if (isEqual(name, "world"))
            com = new CmdWorld();
        else if (isEqual(name, "setworldspawn"))
            com = new CmdSetWorldSpawn();
        else if (isEqual(name, "modifyworld"))
            com = new CmdModifyWorld();
        else if (isEqual(name, "createportal"))
            com = new CmdCreatePortal();
        else if (isEqual(name, "removeportal"))
            com = new CmdRemovePortal();
        else if (isEqual(name, "warps"))
            com = new CmdWarps();
        else if (isEqual(name, "warp"))
            com = new CmdWarp();
        else if (isEqual(name, "createwarp"))
            com = new CmdCreateWarp();
        else if (isEqual(name, "removewarp"))
            com = new CmdRemoveWarp();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (com instanceof WorldCmd && config.contains("Necessities.WorldManager") && !config.getBoolean("Necessities.WorldManager"))
            com = new Cmd();
        return com;
    }

    @Override
    public void onDisable() {
        CmdCommandSpy cs = new CmdCommandSpy();
        CmdHide hide = new CmdHide();
        Janet bot = new Janet();
        um.unload();
        cs.unload();
        hide.unload();
        bot.unload();
        dr.disconnect();
        getLogger().info("Necessities disabled.");
    }

    public static void trackAction(UUID uuid, String action, Object label) {
        String clientId;
        String ip;
        boolean usesPluginChannel = false;

        Player p = instance.getServer().getPlayer(uuid);
        if (p == null) {
            OfflinePlayer offlinep = instance.getServer().getOfflinePlayer(uuid);
            clientId = offlinep.getName();
            ip = "0.0.0.0";
        } else {
            clientId = p.getName();
            if (p.getAddress() != null) {
                ip = p.getAddress().toString().substring(1);
            } else {
                ip = "0.0.0.0";
            }

            usesPluginChannel = p.getListeningPluginChannels().size() != 0;
        }

        String clientVersion = instance.getServer().getVersion().substring("git-Bukkit".length());
        String clientName = "Minecraft " + clientVersion.substring(0, clientVersion.indexOf("-")) + (usesPluginChannel ? " [Supports Plugin Channels]" : "");

        getInstance().googleAnalyticsTracker.TrackAction(clientName, clientId, ip, clientId, action, label.toString());
    }

    public static void trackAction(Player p, String action, Object label) {
        String clientId;
        String ip;
        boolean usesPluginChannel;

        clientId = p.getName();
        if (p.getAddress() != null) {
            ip = p.getAddress().toString().substring(1);
        } else {
            ip = "0.0.0.0";
        }

        usesPluginChannel = p.getListeningPluginChannels().size() != 0;
        String clientVersion = instance.getServer().getVersion().substring("git-Bukkit".length());
        String clientName = "Minecraft " + clientVersion.substring(0, clientVersion.indexOf("-")) + (usesPluginChannel ? " [Supports Plugin Channels]" : "");

        getInstance().googleAnalyticsTracker.TrackAction(clientName, clientId, ip, clientId, action, label.toString());
    }

    public static void trackActionWithValue(UUID uuid, String action, Object label, Object value) {
        String clientId;
        String ip;
        boolean usesPluginChannel = false;

        Player p = instance.getServer().getPlayer(uuid);
        if (p == null) {
            OfflinePlayer offlinep = instance.getServer().getOfflinePlayer(uuid);
            clientId = offlinep.getName();
            ip = "0.0.0.0";
        } else {
            clientId = p.getName();
            if (p.getAddress() != null) {
                ip = p.getAddress().toString().substring(1);
            } else {
                ip = "0.0.0.0";
            }

            usesPluginChannel = p.getListeningPluginChannels().size() != 0;
        }

        String clientVersion = instance.getServer().getVersion().substring("git-Bukkit".length());
        String clientName = "Minecraft " + clientVersion.substring(0, clientVersion.indexOf("-")) + (usesPluginChannel ? " [Supports Plugin Channels]" : "");

        getInstance().googleAnalyticsTracker.TrackActionWithValue(clientName, clientId, ip, clientId, action, label.toString(), value.toString());
    }

    public static void trackActionWithValue(Player p, String action, Object label, Object value) {
        String clientId;
        String ip;
        boolean usesPluginChannel;

        clientId = p.getName();
        if (p.getAddress() != null) {
            ip = p.getAddress().toString().substring(1);
        } else {
            ip = "0.0.0.0";
        }

        usesPluginChannel = p.getListeningPluginChannels().size() != 0;
        String clientVersion = instance.getServer().getVersion().substring("git-Bukkit".length());
        String clientName = "Minecraft " + clientVersion.substring(0, clientVersion.indexOf("-")) + (usesPluginChannel ? " [Supports Plugin Channels]" : "");

        getInstance().googleAnalyticsTracker.TrackActionWithValue(clientName, clientId, ip, clientId, action, label.toString(), value.toString());
    }
}