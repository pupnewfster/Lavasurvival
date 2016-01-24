package com.crossge.necessities;

import com.TentacleLabs.GoogleAnalyticsPlugin.GoogleAnalyticsPlugin;
import com.TentacleLabs.GoogleAnalyticsPlugin.Tracker;
import com.crossge.necessities.Commands.*;
import com.crossge.necessities.Commands.RankManager.*;
import com.crossge.necessities.Commands.WorldManager.*;
import com.crossge.necessities.Janet.Janet;
import com.crossge.necessities.Janet.JanetSlack;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Necessities extends JavaPlugin {
    private static Necessities INSTANCE;
    private final List<String> devs = Arrays.asList("pupnewfster", "Mod_Chris", "hypereddie10");
    private File configFile = new File("plugins/Necessities", "config.yml");
    private Tracker googleAnalyticsTracker;
    private Property skin;
    private UUID janetID;
    DonationReader dr = new DonationReader();
    UserManager um = new UserManager();
    RankManager rm = new RankManager();

    public static Necessities getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabling Necessities...");
        INSTANCE = this;
        if (!hookGoogle())
            getLogger().warning("Could not hook into Google Analytics!");
        janetID = UUID.randomUUID();
        Initialization init = new Initialization();
        init.initiateFiles();
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        dr.init();
        getLogger().info("Necessities enabled.");
    }

    private boolean hookGoogle() {
        GoogleAnalyticsPlugin plugin;
        if ((plugin = (GoogleAnalyticsPlugin) getServer().getPluginManager().getPlugin("GoogleAnalyticsPlugin")) == null)
            return false;
        googleAnalyticsTracker = plugin.getTracker();
        return true;
    }

    public static boolean isTracking() {
        return getTracker() != null;
    }

    public static Tracker getTracker() {
        return getInstance().googleAnalyticsTracker;
    }

    public boolean isDev(String name) {
        return devs.contains(name);
    }

    public List<String> getDevs() {
        return this.devs;
    }

    private IChatBaseComponent formatMessage(String message) {
        return IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
    }

    public void removePlayer(Player p) {
        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) p).getHandle());
        for (Player x : Bukkit.getOnlinePlayers())
            if (!x.canSee(p) && !x.equals(p))
                ((CraftPlayer) x).getHandle().playerConnection.sendPacket(info);
    }

    public void addPlayer(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        User u = um.getUser(p.getUniqueId());
        ep.listName = formatMessage(u.getRank() == null ? "" : ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle() + " ") + p.getDisplayName());
        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
        for (Player x : Bukkit.getOnlinePlayers())
            if (!x.hasPermission("Necessities.seehidden") && x.canSee(p) && !x.equals(p))
                ((CraftPlayer) x).getHandle().playerConnection.sendPacket(info);
    }

    public void updateName(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        User u = um.getUser(p.getUniqueId());
        ep.listName = formatMessage(u.getRank() == null ? "" : ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle() + " ") + p.getDisplayName());
        PacketPlayOutPlayerInfo tabList = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, ep);
        for (Player x : Bukkit.getOnlinePlayers())
            ((CraftPlayer)x).getHandle().playerConnection.sendPacket(tabList);
    }

    public void updateAll(Player x) {
        ArrayList<EntityPlayer> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            EntityPlayer ep = ((CraftPlayer) p).getHandle();
            User u = um.getUser(p.getUniqueId());
            ep.listName = formatMessage(u.getRank() == null ? "" : ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle() + " ") + p.getDisplayName());
            players.add(ep);
        }
        ((CraftPlayer)x).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, players));
    }

    public void addJanet(Player p) {
        GameProfile janetProfile = new GameProfile(janetID, "Janet");
        if (this.skin == null)
            this.skin = getSkin();
        if (this.skin != null)
            janetProfile.getProperties().put("textures", this.skin);
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer world = server.getWorldServer(0);
        PlayerInteractManager manager = new PlayerInteractManager(world);
        EntityPlayer player = new EntityPlayer(server, world, janetProfile, manager);
        player.listName = formatMessage(ChatColor.translateAlternateColorCodes('&', rm.getRank(rm.getOrder().size() - 1).getTitle() + " ") + "Janet");
        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, player);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(info);
    }

    public void addHeader(Player p) {
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(formatMessage(ChatColor.GREEN + "GamezGalaxy"));
        try {
            Field field = packet.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(packet, formatMessage(ChatColor.BLUE + "http://gamezgalaxy.com"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
    }

    private Property getSkin() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/136f2ba62be3444ca2968ec597edb57e?unsigned=false").openConnection().getInputStream()));
            String value = "", signature = "";
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
            return new Property("textures", value, signature);
        } catch (Exception ignored) {
        }
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
        else if (isEqual(name, "tpa"))
            com = new CmdTpa();
        else if (isEqual(name, "tpdeny"))
            com = new CmdTpdeny();
        else if (isEqual(name, "tpaccept"))
            com = new CmdTpaccept();
        else if (isEqual(name, "tpahere"))
            com = new CmdTpahere();
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
        else if (isEqual(name, "tps"))
            com = new CmdTps();
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
        else if (isEqual(name, "slack"))
            com = new CmdSlack();
        else if (isEqual(name, "requestmod"))
            com = new CmdRequestMod();
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
        JanetSlack slack = new JanetSlack();
        CmdHide hide = new CmdHide();
        Janet bot = new Janet();
        um.unload();
        cs.unload();
        hide.unload();
        slack.disconnect();
        bot.unload();
        dr.disconnect();
        getLogger().info("Necessities disabled.");
    }

    public static void trackAction(String clientId, String action, Object label) {
        String clientVersion = Bukkit.getVersion().substring("git-Bukkit".length());
        String clientName = "Minecraft " + clientVersion.substring(0, clientVersion.indexOf("-"));
        getTracker().TrackAction(clientName, clientId, "127.0.0.1", clientId, action, label.toString());
    }

    public static void trackActionWithValue(String clientId, String action, Object label, Object value) {
        String clientVersion = Bukkit.getVersion().substring("git-Bukkit".length());
        String clientName = "Minecraft " + clientVersion.substring(0, clientVersion.indexOf("-"));
        getTracker().TrackActionWithValue(clientName, clientId, "127.0.0.1", clientId, action, label.toString(), value.toString());
    }

    public static void trackAction(UUID uuid, String action, Object label) {
        boolean usesPluginChannel = false;
        String clientId, ip;
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) {
            clientId = Bukkit.getOfflinePlayer(uuid).getName();
            ip = "0.0.0.0";
        } else {
            clientId = p.getName();
            ip = (p.getAddress() != null ? p.getAddress().toString().substring(1) : "0.0.0.0");
            usesPluginChannel = p.getListeningPluginChannels().size() != 0;
        }
        String clientVersion = Bukkit.getVersion().substring("git-Bukkit".length());
        String clientName = "Minecraft " + clientVersion.substring(0, clientVersion.indexOf("-")) + (usesPluginChannel ? " [Supports Plugin Channels]" : "");
        getTracker().TrackAction(clientName, clientId, ip, clientId, action, label.toString());
    }

    public static void trackAction(Player p, String action, Object label) {
        String clientId = p.getName(), ip = (p.getAddress() != null ? p.getAddress().toString().substring(1) : "0.0.0.0");
        boolean usesPluginChannel = p.getListeningPluginChannels().size() != 0;
        String clientVersion = Bukkit.getVersion().substring("git-Bukkit".length());
        String clientName = "Minecraft " + clientVersion.substring(0, clientVersion.indexOf("-")) + (usesPluginChannel ? " [Supports Plugin Channels]" : "");
        getTracker().TrackAction(clientName, clientId, ip, clientId, action, label.toString());
    }

    public static void trackActionWithValue(UUID uuid, String action, Object label, Object value) {
        boolean usesPluginChannel = false;
        String clientId, ip;
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) {
            clientId = Bukkit.getOfflinePlayer(uuid).getName();
            ip = "0.0.0.0";
        } else {
            clientId = p.getName();
            ip = (p.getAddress() != null ? p.getAddress().toString().substring(1) : "0.0.0.0");
            usesPluginChannel = p.getListeningPluginChannels().size() != 0;
        }
        String clientVersion = Bukkit.getVersion().substring("git-Bukkit".length());
        String clientName = "Minecraft " + clientVersion.substring(0, clientVersion.indexOf("-")) + (usesPluginChannel ? " [Supports Plugin Channels]" : "");
        getTracker().TrackActionWithValue(clientName, clientId, ip, clientId, action, label.toString(), value.toString());
    }

    public static void trackActionWithValue(Player p, String action, Object label, Object value) {
        String clientId = p.getName(), ip = (p.getAddress() != null ? p.getAddress().toString().substring(1) : "0.0.0.0");
        boolean usesPluginChannel = p.getListeningPluginChannels().size() != 0;
        String clientVersion = Bukkit.getVersion().substring("git-Bukkit".length());
        String clientName = "Minecraft " + clientVersion.substring(0, clientVersion.indexOf("-")) + (usesPluginChannel ? " [Supports Plugin Channels]" : "");
        getTracker().TrackActionWithValue(clientName, clientId, ip, clientId, action, label.toString(), value.toString());
    }
}