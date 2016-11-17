package me.eddiep.minecraft.ls;

import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.GetUUID;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import com.google.gson.Gson;
import me.eddiep.ClassicPhysics;
import me.eddiep.handles.ClassicPhysicsHandler;
import me.eddiep.minecraft.ls.commands.*;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.LavaMap;
import me.eddiep.minecraft.ls.game.impl.Rise;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import me.eddiep.minecraft.ls.game.shop.impl.*;
import me.eddiep.minecraft.ls.ranks.UserManager;
import me.eddiep.minecraft.ls.system.PlayerListener;
import me.eddiep.minecraft.ls.system.setup.SetupMap;
import me.eddiep.ubot.utils.CancelToken;
import net.milkbowl.vault.economy.Economy;
import net.njay.MenuFramework;
import net.njay.MenuRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.boss.CraftBossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Lavasurvival extends JavaPlugin {
    public static final Gson GSON = new Gson();
    public static Lavasurvival INSTANCE;
    public static final BossBar GGBAR = new CraftBossBar(ChatColor.GOLD + "Welcome to " + ChatColor.AQUA + "Galaxy Gaming", BarColor.GREEN, BarStyle.SOLID, new BarFlag[0]);
    public final Runnable MONEY_VIEWER = () -> {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        players.forEach(this::updateMoneyView);
    };

    private Cmd[] commands;
    private HashMap<UUID, SetupMap> setups = new HashMap<>();
    private Economy econ;
    private ClassicPhysics physics;
    private UserManager userManager;
    private boolean running = false;
    private ItemStack rules;
    private CancelToken ubotCancelToken;
    public boolean updating;

    private void updateMoneyView(Player player) {
        Inventory inv = player.getInventory();
        int index = inv.contains(Material.GOLD_INGOT) ? inv.first(Material.GOLD_INGOT) : -1;
        if (index == -1) {
            ItemStack item = new ItemStack(Material.GOLD_INGOT);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Balance");
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.ITALIC + "Current Balance: " + ChatColor.RESET + this.econ.format(this.econ.getBalance(player)));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(inv.firstEmpty(), item);
            return;
        }

        ItemStack item = inv.getItem(index);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Current Balance: " + ChatColor.RESET + this.econ.format(this.econ.getBalance(player)));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void withdrawAndUpdate(Player player, double price) {
        this.econ.withdrawPlayer(player, price);
        updateMoneyView(player);
        if (Necessities.isTracking()) {
            Necessities.trackActionWithValue(player, "Economy", -price, -price);
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

        log("Attaching to Vault..");
        if (!setupEcon()) {
            log("Disabling, no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        log("Attaching to ClassicPhysics..");
        if (!setupPhysics()) {
            log("Disabling, no ClassicPhysics found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Setup the shops after connecting to vault
        setupShops();
        setRules();
        if (LavaMap.getPossibleMaps().length > 0) {//Should we make it random here which gamemode we start with and make it obey the allowed maps for that gamemode
            Rise rise = new Rise();
            rise.prepare();
            rise.start();
            this.running = true;
        } else //Only schedule a listener if no maps. If maps then it already is initialized through Gamemode.prepare()
            getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    private boolean setupPhysics() {
        return (this.physics = (ClassicPhysics) Bukkit.getPluginManager().getPlugin("ClassicPhysics")) != null;
    }

    @Override
    public void onDisable() {
        if (this.running) {
            log("Stopping game..");
            Gamemode.getCurrentGame().forceEnd();
            log("Cleaning up..");
            //ubotCancelToken.cancel();
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
        MenuFramework.enable(new MenuRegistry(this, RankShop.class, ItemShop.class, BlockShopCategory.class, BasicBlockShop.class, AdvancedBlockShop.class,
                SurvivorBlockShop.class, TrustedBlockShop.class, ElderBlockShop.class, DonatorBlockShop.class));

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "" + ChatColor.ITALIC + "Buy more blocks!");

        ArrayList<String> lore2 = new ArrayList<>();
        lore2.add(ChatColor.GREEN + "" + ChatColor.ITALIC + "Level up!");

        ArrayList<String> lore3 = new ArrayList<>();
        lore3.add(ChatColor.RED + "" + ChatColor.ITALIC + "Buy powerups!");

        ArrayList<String> lore4 = new ArrayList<>();
        lore4.add(ChatColor.AQUA + "" + ChatColor.ITALIC + "Store unused blocks!");

        ShopFactory.createShop(this, "Block Shop", new MenuShopManager(BlockShopCategory.class), Material.EMERALD, lore, true);
        ShopFactory.createShop(this, "Rank Shop", new MenuShopManager(RankShop.class), Material.EXP_BOTTLE, lore2, false);
        ShopFactory.createShop(this, "Item Shop", new MenuShopManager(ItemShop.class), Material.CLAY_BALL, lore3, true);
        ShopFactory.createShop(this, "Bank", new BankShopManager(), Material.CHEST, lore4, false);
    }

    private void init() {
        this.commands = new Cmd[]{
                new CmdEndGame(),
                new CmdLVote(),
                new CmdRules(),
                new CmdSetupMap(),
                new CmdSpawn(),
                new CmdAirc()
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
        this.userManager.readUsers();

        //log("Starting UBot");
        //UBot ubot = new UBot(new File("/root/ubot/ls1/Lavasurvival"), new Updater(), new UBotLogger());
        //ubotCancelToken = ubot.startAsync();
    }

    private boolean setupEcon() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        this.econ = rsp.getProvider();
        return (this.econ = rsp.getProvider()) != null;
    }

    public ItemStack getRules() {
        return this.rules;
    }

    public Economy getEconomy() {
        return this.econ;
    }

    public GetUUID getUUIDs() {
        return Necessities.getInstance().getUUID();
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public CmdHide getHide() {
        return Necessities.getInstance().getHide();
    }

    public com.crossge.necessities.RankManager.UserManager getNecessitiesUserManager() {
        return Necessities.getInstance().getUM();
    }

    public RankManager getRankManager() {
        return Necessities.getInstance().getRM();
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
        for (Cmd possible : this.commands) {
            if (possible.getName().equalsIgnoreCase(name))
                return possible;
        }
        return null;
    }

    public ClassicPhysics getPhysics() {
        return this.physics;
    }

    public ClassicPhysicsHandler getPhysicsHandler() {
        return this.physics.getPhysicsHandler();
    }

    public static void warn(String s) {
        INSTANCE.getLogger().warning(s);
    }

    public void changeServer(Player p, String serverName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF("Connect");
        dos.writeUTF(serverName);
        p.sendPluginMessage(this, "BungeeCord", baos.toByteArray());
        baos.close();
        dos.close();
    }

    public void stopUbot() {
        this.ubotCancelToken.cancel();
    }

    public void depositPlayer(Player player, double reward) {
        econ.depositPlayer(player, reward);
        if (Necessities.isTracking()) {
            Necessities.trackActionWithValue(player, "Economy", reward, reward);
        }
    }
}