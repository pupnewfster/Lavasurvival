package com.crossge.necessities;

import com.crossge.necessities.Commands.*;
import com.crossge.necessities.Commands.Economy.CmdBalance;
import com.crossge.necessities.Commands.Economy.CmdBaltop;
import com.crossge.necessities.Commands.Economy.CmdEco;
import com.crossge.necessities.Commands.Economy.CmdPay;
import com.crossge.necessities.Commands.RankManager.*;
import com.crossge.necessities.Commands.WorldManager.*;
import com.crossge.necessities.Economy.Economy;
import com.crossge.necessities.Janet.Janet;
import com.crossge.necessities.Janet.JanetLog;
import com.crossge.necessities.Janet.JanetSlack;
import com.crossge.necessities.Janet.JanetWarn;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.WorldManager.WorldManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_12_R1.*;
import net.nyvaria.googleanalytics.hit.Hit;
import net.nyvaria.openanalytics.bukkit.OpenAnalytics;
import net.nyvaria.openanalytics.bukkit.OpenAnalyticsTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Necessities extends JavaPlugin {
    private static Necessities INSTANCE;
    private OpenAnalyticsTracker googleAnalyticsTracker;
    private final List<DevInfo> devs = new ArrayList<>();
    private PacketPlayOutPlayerInfo janetInfo;
    private CmdCommandSpy spy;
    private WorldManager wm;
    private UserManager um;
    private RankManager rm;
    private ScoreBoards sb;
    private JanetWarn warns;
    private Console console;
    private Variables var;
    private Teleports tps;
    private JanetLog log;
    private CmdHide hide;
    private Janet bot;
    private JanetSlack slack;
    private Announcer announcer;
    private Economy economy;

    File getConfigFile() {
        return new File(getDataFolder(), "config.yml");
    }

    public YamlConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(getConfigFile());
    }

    public static Necessities getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {
        getLogger().info("Enabling Necessities...");
        INSTANCE = this;
        if (!hookGoogle())
            getLogger().warning("Could not hook into Google Analytics!");
        Initialization init = new Initialization();
        init.initiateFiles();
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        GameProfile janetProfile = new GameProfile(UUID.randomUUID(), "Janet");
        janetProfile.getProperties().put("textures", getSkin());
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = server.getWorldServer(0);
        PlayerInteractManager manager = new PlayerInteractManager(world);
        EntityPlayer player = new EntityPlayer(server, world, janetProfile, manager);
        player.listName = formatMessage(ChatColor.translateAlternateColorCodes('&', rm.getRank(rm.getOrder().size() - 1).getTitle() + " ") + "Janet");
        this.janetInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, player);
        getDevInfo();
        getLogger().info("Necessities enabled.");
    }

    private boolean hookGoogle() {
        OpenAnalytics plugin;
        if ((plugin = (OpenAnalytics) getServer().getPluginManager().getPlugin("OpenAnalytics")) == null)
            return false;
        googleAnalyticsTracker = plugin.getTracker();
        return true;
    }

    public static boolean isTracking() {
        return getTracker() != null;
    }

    private static OpenAnalyticsTracker getTracker() {
        return INSTANCE == null ? null : getInstance().googleAnalyticsTracker;
    }

    public boolean isDev(UUID uuid) {
        return this.devs.stream().anyMatch(i -> uuid.equals(i.getMCUUID()));
    }

    public List<DevInfo> getDevs() {
        return Collections.unmodifiableList(this.devs);
    }

    private void getDevInfo() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://galaxygaming.gg/staff.json").openConnection().getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JsonObject json = Jsoner.deserialize(response.toString(), new JsonObject());
            JsonArray ar = (JsonArray) json.get("devs");
            JsonObject ls = (JsonObject) json.get("Lavasurvival");
            JsonArray lsDevs = (JsonArray) ls.get("devs");
            for (int i = 0; i < lsDevs.size(); i++) {
                int devID = lsDevs.getInteger(i);
                JsonObject dev = (JsonObject) ar.stream().filter(a -> devID == ((JsonObject) a).getInteger("devID")).findFirst().orElse(null);
                if (dev != null)
                    this.devs.add(new DevInfo(dev));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DevInfo {
        private final UUID mcUUID;
        private final String slackID;
        private final String name;

        private DevInfo(JsonObject dev) {
            this.mcUUID = UUID.fromString(dev.getString("mcUUID"));
            this.slackID = dev.getString("slackID");
            this.name = dev.getString("name");
        }

        public String getName() {
            return this.name;
        }

        public UUID getMCUUID() {
            return this.mcUUID;
        }
    }

    private IChatBaseComponent formatMessage(String message) {
        return IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
    }

    public void removePlayer(Player p) {
        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) p).getHandle());
        Bukkit.getOnlinePlayers().stream().filter(x -> !x.canSee(p) && !x.equals(p)).forEach(x -> ((CraftPlayer) x).getHandle().playerConnection.sendPacket(info));
    }

    public void addPlayer(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        User u = um.getUser(p.getUniqueId());
        ep.listName = formatMessage(u.getRank() == null ? "" : ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle() + " ") + p.getDisplayName());
        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
        Bukkit.getOnlinePlayers().stream().filter(x -> !x.hasPermission("Necessities.seehidden") && x.canSee(p) && !x.equals(p)).forEach(x -> ((CraftPlayer) x).getHandle().playerConnection.sendPacket(info));
    }

    public void updateName(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        User u = um.getUser(p.getUniqueId());
        ep.listName = formatMessage(u.getRank() == null ? "" : ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle() + " ") + p.getDisplayName());
        PacketPlayOutPlayerInfo tabList = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, ep);
        Bukkit.getOnlinePlayers().forEach(x -> ((CraftPlayer) x).getHandle().playerConnection.sendPacket(tabList));
    }

    public void updateAll(Player x) {
        ArrayList<EntityPlayer> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            EntityPlayer ep = ((CraftPlayer) p).getHandle();
            User u = um.getUser(p.getUniqueId());
            ep.listName = formatMessage(u.getRank() == null ? "" : ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle() + " ") + p.getDisplayName());
            players.add(ep);
        }
        ((CraftPlayer) x).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, players));
    }

    void addJanet(Player p) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(this.janetInfo);
    }

    void addHeader(Player p) {
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field field = packet.getClass().getDeclaredField("a");
            field.setAccessible(true);
            field.set(packet, formatMessage(ChatColor.AQUA + "Galaxy Gaming"));
            Field field2 = packet.getClass().getDeclaredField("b");
            field2.setAccessible(true);
            field2.set(packet, formatMessage(ChatColor.GREEN + "http://galaxygaming.gg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    private Property getSkin() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/136f2ba62be3444ca2968ec597edb57e?unsigned=false").openConnection().getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JsonObject json = Jsoner.deserialize(response.toString(), new JsonObject());
            JsonObject jo = (JsonObject) ((JsonArray) json.get("properties")).get(0);
            String signature = jo.getString("signature"), value = jo.getString("value");
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

    private boolean isEqual(String command, String toCheck) {
        return command.equalsIgnoreCase(toCheck);
    }

    private Cmd getCmd(String name) {
        Cmd com = null;
        if (isEqual(name, "warn"))
            com = new CmdWarn();
        else if (isEqual(name, "ragequit"))
            com = new CmdRagequit();
        else if (isEqual(name, "devs"))
            com = new CmdDevs();
        else if (isEqual(name, "hide"))
            com = getHide();
        else if (isEqual(name, "title"))
            com = new CmdTitle();
        else if (isEqual(name, "bracketcolor"))
            com = new CmdBracketColor();
        else if (isEqual(name, "commandspy"))
            com = getSpy();
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
        else if (isEqual(name, "reloadannouncer"))
            com = new CmdReloadAnnouncer();
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
            //Economy
        else if (isEqual(name, "bal"))
            com = new CmdBalance();
        else if (isEqual(name, "baltop"))
            com = new CmdBaltop();
        else if (isEqual(name, "pay"))
            com = new CmdPay();
        else if (isEqual(name, "eco"))
            com = new CmdEco();
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
        YamlConfiguration config = getConfig();
        if (com instanceof WorldCmd && config.contains("Necessities.WorldManager") && !config.getBoolean("Necessities.WorldManager"))
            com = new DisabledCmd();
        return com == null ? (sender, args) -> false : com;
    }

    @Override
    public void onDisable() {
        this.um.unload();
        this.spy.unload();
        this.hide.unload();
        this.slack.disconnect();
        this.bot.unload();
        this.announcer.exit();
        getLogger().info("Necessities disabled.");
    }

    public static void trackAction(Hit hit) {
        OpenAnalyticsTracker tracker;
        if ((tracker = getTracker()) != null)
            tracker.trackHit(hit);
    }


    public static UserManager getUM() {
        return INSTANCE.um == null ? INSTANCE.um = new UserManager() : INSTANCE.um;
    }

    static CmdCommandSpy getSpy() {
        return INSTANCE.spy == null ? INSTANCE.spy = new CmdCommandSpy() : INSTANCE.spy;
    }

    public static RankManager getRM() {
        return INSTANCE.rm == null ? INSTANCE.rm = new RankManager() : INSTANCE.rm;
    }

    public static JanetSlack getSlack() {
        return INSTANCE.slack == null ? INSTANCE.slack = new JanetSlack() : INSTANCE.slack;
    }

    public static Console getConsole() {
        return INSTANCE.console == null ? INSTANCE.console = new Console() : INSTANCE.console;
    }

    public static Variables getVar() {
        return INSTANCE.var == null ? INSTANCE.var = new Variables() : INSTANCE.var;
    }

    public static Teleports getTPs() {
        return INSTANCE.tps == null ? INSTANCE.tps = new Teleports() : INSTANCE.tps;
    }

    public static JanetWarn getWarns() {
        return INSTANCE.warns == null ? INSTANCE.warns = new JanetWarn() : INSTANCE.warns;
    }

    public static ScoreBoards getSBs() {
        return INSTANCE.sb == null ? INSTANCE.sb = new ScoreBoards() : INSTANCE.sb;
    }

    public static CmdHide getHide() {
        return INSTANCE.hide == null ? INSTANCE.hide = new CmdHide() : INSTANCE.hide;
    }

    static Janet getBot() {
        return INSTANCE.bot == null ? INSTANCE.bot = new Janet() : INSTANCE.bot;
    }

    public static WorldManager getWM() {
        return INSTANCE.wm == null ? INSTANCE.wm = new WorldManager() : INSTANCE.wm;
    }

    public static JanetLog getLog() {
        return INSTANCE.log == null ? INSTANCE.log = new JanetLog() : INSTANCE.log;
    }

    public static Announcer getAnnouncer() {
        return INSTANCE.announcer == null ? INSTANCE.announcer = new Announcer() : INSTANCE.announcer;
    }

    public static Economy getEconomy() {
        return INSTANCE.economy == null ? INSTANCE.economy = new Economy() : INSTANCE.economy;
    }
}