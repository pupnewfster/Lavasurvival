package me.eddiep.minecraft.ls;

import com.crossge.necessities.Necessities;
import com.google.gson.Gson;
import me.eddiep.minecraft.ls.commands.*;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.LavaMap;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import me.eddiep.minecraft.ls.game.shop.impl.*;
import me.eddiep.minecraft.ls.ranks.UserManager;
import me.eddiep.minecraft.ls.system.PlayerListener;
import me.eddiep.minecraft.ls.system.setup.SetupMap;
import me.eddiep.minecraft.ls.system.ubot.UBotLogger;
import me.eddiep.minecraft.ls.system.ubot.Updater;
import me.eddiep.ubot.UBot;
import me.eddiep.ubot.utils.CancelToken;
import net.njay.MenuFramework;
import net.njay.MenuRegistry;
import net.nyvaria.googleanalytics.hit.EventHit;
import net.nyvaria.openanalytics.bukkit.client.Client;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.boss.CraftBossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Lavasurvival extends JavaPlugin {
    public static final Gson GSON = new Gson();
    public static Lavasurvival INSTANCE;
    public static final BossBar GGBAR = new CraftBossBar(ChatColor.GOLD + "Welcome to " + ChatColor.AQUA + "Galaxy Gaming", BarColor.GREEN, BarStyle.SOLID);
    public final Runnable MONEY_VIEWER = () -> Bukkit.getOnlinePlayers().forEach(this::updateMoneyView);

    private Cmd[] commands;
    private final HashMap<UUID, SetupMap> setups = new HashMap<>();
    private UserManager userManager;
    private boolean running;
    private ItemStack rules, tutorial;
    private String dbURL;
    private Properties properties;
    @SuppressWarnings("CanBeFinal")
    private CancelToken ubotCancelToken;
    public boolean updating;

    private void updateMoneyView(Player player) {
        Inventory inv = player.getInventory();
        int index = inv.first(Material.GOLD_INGOT);
        if (index == -1) {
            ItemStack item = new ItemStack(Material.GOLD_INGOT);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Balance");
            meta.setLore(Collections.singletonList(ChatColor.GREEN + "" + ChatColor.ITALIC + "Current Balance: " + ChatColor.RESET + Necessities.getEconomy().format(Necessities.getEconomy().getBalance(player.getUniqueId()))));
            item.setItemMeta(meta);
            inv.setItem(inv.firstEmpty(), item);
            return;
        }
        ItemStack item = inv.getItem(index);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Collections.singletonList(ChatColor.GREEN + "" + ChatColor.ITALIC + "Current Balance: " + ChatColor.RESET + Necessities.getEconomy().format(Necessities.getEconomy().getBalance(player.getUniqueId()))));
        item.setItemMeta(meta);
    }

    public void withdrawAndUpdate(Player player, double price) {
        Necessities.getEconomy().withdraw(player.getUniqueId(), price);
        updateMoneyView(player);
        if (Necessities.isTracking()) {
            EventHit hit = new EventHit(new Client(player), "economy", "economy");
            hit.event_value = (int) -price;

            Necessities.trackAction(hit);
        }
    }

    public static void log(String message) {
        INSTANCE.getLogger().info(message);
    }

    public static void globalMessage(String message) {
        if (Gamemode.getCurrentGame() != null)
            Gamemode.getCurrentGame().globalMessage(message);
        else //Sends to everyone if no game to send to
            Bukkit.broadcastMessage(message);
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        init();

        log("Attaching to Necessities..");
        if (!Bukkit.getPluginManager().isPluginEnabled("Necessities")) {
            log("Disabling, Necessities not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        log("Attaching to ClassicPhysics..");
        if (!Bukkit.getPluginManager().isPluginEnabled("ClassicPhysics")) {
            log("Disabling, no ClassicPhysics found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupShops();
        setRules();
        setTutorial();

        if (LavaMap.getPossibleMaps().length > 0 && Gamemode.runFirstGamemode())
            this.running = true;
        else //Only schedule a listener if no maps. If maps then it already is initialized through Gamemode.prepare()
            getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        //Why do we bother scheduling one if no maps
        loadAdvancements();
    }

    @Override
    public void onDisable() {
        if (this.running) {
            log("Stopping game..");
            Gamemode.getCurrentGame().forceEnd();
            log("Cleaning up..");
            try {
                ubotCancelToken.cancel();
            } catch (Exception ignored) { //If ubot was never initialized don't show an error message
            }
            Gamemode.cleanup();
            ShopFactory.cleanup();
            this.setups.keySet().forEach(uuid -> this.setups.get(uuid).end());
            this.setups.clear();
            this.userManager.saveAll();
            log("Disabled");
        }
        GGBAR.removeAll();
        this.running = false;
    }

    private void setTutorial() {
        tutorial = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) tutorial.getItemMeta();
        meta.setTitle("Tutorial");
        meta.setAuthor(ChatColor.GREEN + "GalaxyGaming");
        meta.addPage("The round begins with the preparation phase; during this time, build your shelter as large as you can, with thick enough walls to withstand the lava.");
        meta.addPage("select blocks on your hotbar to view melt times against each type of liquid. \n" +
                "Once the preparation timer runs to zero, the lava begins rising to flood the map. You may continue to build after the lava has poured, however leaving your shelter becomes a far greater");
        meta.addPage("challenge. Once it comes in contact with your shelter, your blocks will slowly degrade before lava comes rushing through. \n" +
                "The round ends a few minutes after the lava has finished flooding the map. The larger the interior of your shelter is at this time, the more cash you");
        meta.addPage("earn. (Also called GGs).");
        this.tutorial.setItemMeta(meta);
    }

    private void setRules() {
        rules = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) rules.getItemMeta();
        meta.setTitle("Rules");
        meta.setAuthor(ChatColor.GREEN + "GalaxyGaming");
        meta.addPage("1. No griefing." + "\n" +
                "2. Any form of hacked client is forbidden." + "\n" +
                "3. No cursing or offensive language." + "\n" +
                "4. No block spamming." + "\n" +
                "5. No damming the lava." + "\n" +
                "6. No leaving the map." + "\n" +
                "7. No blocking spawn." + "\n" +
                "8. Please ask before entering someone else's shelter." + "\n");
        this.rules.setItemMeta(meta);
    }

    private void setupShops() {
        MenuFramework.enable(new MenuRegistry(this, RankShop.class, ItemShop.class, BlockShopCategory.class, BasicBlockShop.class, AdvancedBlockShop.class, SurvivorBlockShop.class, TrustedBlockShop.class,
                ElderBlockShop.class, DonatorBlockShop.class));

        ShopFactory.createShop(this, "Block Shop", new MenuShopManager(BlockShopCategory.class), Material.EMERALD, Collections.singletonList(ChatColor.GOLD + "" + ChatColor.ITALIC + "Buy more blocks!"), true);
        ShopFactory.createShop(this, "Rank Shop", new MenuShopManager(RankShop.class), Material.EXP_BOTTLE, Collections.singletonList(ChatColor.GREEN + "" + ChatColor.ITALIC + "Level up!"), false);
        ShopFactory.createShop(this, "Item Shop", new MenuShopManager(ItemShop.class), Material.CLAY_BALL, Collections.singletonList(ChatColor.RED + "" + ChatColor.ITALIC + "Buy powerups!"), true);
        ShopFactory.createShop(this, "Bank", new BankShopManager(), Material.CHEST, Collections.singletonList(ChatColor.AQUA + "" + ChatColor.ITALIC + "Store unused blocks!"), false);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void init() {
        this.commands = new Cmd[]{
                new CmdEndGame(),
                new CmdLVote(),
                new CmdRules(),
                new CmdSetupMap(),
                new CmdSpawn(),
                new CmdAirc(),
                new CmdCalculateCPMap()
        };

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getDataFolder().mkdir();
        new File(getDataFolder() + "/maps").mkdir();
        File configFileUsers = new File(getDataFolder(), "userinfo.yml");
        if (!configFileUsers.exists())
            try {
                configFileUsers.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        this.userManager = new UserManager();

        log("Starting UBot");
        try {
            UBot ubot = new UBot(new File("/home/minecraft/ubot/Lavasurvival"), new Updater(), new UBotLogger());
            ubotCancelToken = ubot.startAsync();
        } catch (Exception e) {
            log("Failed to start UBot");
        }

        YamlConfiguration config = Necessities.getInstance().getConfig();
        this.dbURL = "jdbc:mysql://" + config.getString("Lavasurvival.DBHost") + "/" + config.getString("Lavasurvival.DBTable");
        this.properties = new Properties();
        this.properties.setProperty("user", config.getString("Lavasurvival.DBUser"));
        this.properties.setProperty("password", config.getString("Lavasurvival.DBPassword"));
        this.properties.setProperty("useSSL", "false");
        this.properties.setProperty("autoReconnect", "true");
        this.properties.setProperty("useLegacyDatetimeCode", "false");
        this.properties.setProperty("serverTimezone", "EST");
    }

    private void loadAdvancements() {
        try {
            JarFile jar = new JarFile(getFile());
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName(), line;
                if (name.startsWith("resources/") && name.endsWith(".json")) {
                    InputStream input = jar.getInputStream(entry);
                    InputStreamReader isr = new InputStreamReader(input);
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuilder text = new StringBuilder();
                    while ((line = reader.readLine()) != null)
                        text.append(line);
                    reader.close();
                    isr.close();
                    input.close();
                    Bukkit.getUnsafe().loadAdvancement(new NamespacedKey(this, name.substring(name.indexOf("_") + 1, name.length() - 5)), text.toString());
                }
            }
            ((CraftServer) Bukkit.getServer()).getServer().getAdvancementData().reload();//Fix the advancements overlapping in the menu
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ItemStack getRules() {
        return this.rules;
    }

    public ItemStack getTutorial() {
        return this.tutorial;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public String getDBURL() {
        return this.dbURL;
    }

    public Properties getDBProperties() {
        return this.properties;
    }

    public void removeFromSetup(UUID uuid) {
        this.setups.remove(uuid);
    }

    public void addToSetup(UUID uuid, SetupMap setup) {
        this.setups.put(uuid, setup);
    }

    public HashMap<UUID, SetupMap> getSetups() {
        return this.setups;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        Cmd command = getCmd(cmd.getName());
        return command != null && command.commandUse(sender, args);
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd == null)
            return null;
        Cmd command = getCmd(cmd.getName());
        return (sender == null || command == null) ? null : command.tabComplete(sender, args);
    }

    private Cmd getCmd(String name) {
        return Arrays.stream(this.commands).filter(possible -> possible.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static void warn(String s) {
        INSTANCE.getLogger().warning(s);
    }

    public void changeServer(Player p, String serverName) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bs);
        dos.writeUTF("Connect");
        dos.writeUTF(serverName);
        p.sendPluginMessage(this, "BungeeCord", bs.toByteArray());
        bs.close();
        dos.close();
    }

    public void stopUbot() {
        this.ubotCancelToken.cancel();
    }

    public void depositPlayer(Player player, double reward) {
        Necessities.getEconomy().deposit(player.getUniqueId(), reward);
        if (Necessities.isTracking()) {
            EventHit hit = new EventHit(new Client(player), "economy", "economy");
            hit.event_value = (int) reward;
            Necessities.trackAction(hit);
        }
    }
}