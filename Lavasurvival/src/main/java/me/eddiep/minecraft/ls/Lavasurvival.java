package me.eddiep.minecraft.ls;

import com.crossge.necessities.GetUUID;
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
import net.milkbowl.vault.economy.Economy;
import net.njay.MenuFramework;
import net.njay.MenuRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class Lavasurvival extends JavaPlugin {
    public static final Gson GSON = new Gson();
    public static Lavasurvival INSTANCE;
    public final Runnable MONEY_VIEWER = new Runnable() {
        @Override
        public void run() {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            for (Player player : players) {
                ShopFactory.validateInventory(player.getInventory());
                updateMoneyView(player);
            }
        }
    };

    private Cmd[] commands;
    private HashMap<UUID, SetupMap> setups = new HashMap<>();
    private Economy econ;
    private ClassicPhysics physics;
    private GetUUID uuiDs;
    private UserManager userManager;
    private com.crossge.necessities.RankManager.UserManager um;
    private RankManager rm;
    private boolean running = false;
    private int moneyViewer;
    private ItemStack rules;

    public void updateMoneyView(Player player) {
        Inventory inv = player.getInventory();

        int index = inv.contains(Material.GOLD_INGOT) ? inv.first(Material.GOLD_INGOT) : -1;

        if (index == -1) {
            ItemStack item = new ItemStack(Material.GOLD_INGOT);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Balance");
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.ITALIC + "Current Balance: " + ChatColor.RESET + econ.format(econ.getBalance(player)));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(inv.firstEmpty(), item);
            return;
        }

        ItemStack item = inv.getItem(index);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Current Balance: " + ChatColor.RESET + econ.format(econ.getBalance(player)));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void withdrawAndUpdate(Player player, double price) {
        econ.withdrawPlayer(player, price);
        updateMoneyView(player);
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
        /*log("Making money viewer task..");
        moneyViewer = getServer().getScheduler().scheduleSyncRepeatingTask(this, MONEY_VIEWER, 0, 25);*/
        if (LavaMap.getPossibleMaps().length > 0) {
            Rise flood = new Rise();
            flood.prepare();
            flood.start();
            running = true;
        } else //Only schedule a listener if no maps. If maps then it already is initialized through Gamemode.prepare()
            getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    private boolean setupPhysics() {
        physics = (ClassicPhysics) Bukkit.getPluginManager().getPlugin("ClassicPhysics");

        return physics != null;
    }

    @Override
    public void onDisable() {
        if (running) {
            log("Stopping game..");
            Gamemode.getCurrentGame().forceEnd();
            log("Cleaning up..");
            Gamemode.cleanup();
            ShopFactory.cleanup();

            for (UUID uuid : setups.keySet())
                setups.get(uuid).end();
            setups.clear();

            getServer().getScheduler().cancelTask(moneyViewer);

            log("Disabled");
        }
        running = false;
    }

    private void setRules() {
        rules = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) rules.getItemMeta();
        meta.setTitle("Rules");
        meta.setAuthor("GamezGalaxy");
        meta.addPage("1. No Griefing." + "\n" +
                "2. Any form of hacked client is forbidden." + "\n" +
                "3. No cursing or offensive language." + "\n" +
                "4. No block spamming." + "\n" +
                "5. No damming the lava" + "\n" +
                "6. No leaving the map" + "\n" +
                "7. No blocking spawn" + "\n" +
                "8. Please ask before entering someone else's shelter" + "\n");
        rules.setItemMeta(meta);
    }

    private void setupShops() {
        MenuFramework.enable(new MenuRegistry(this, RankShop.class, BlockShopCatagory.class, BasicBlockShop.class,
                AdvancedBlockShop.class, SurvivorBlockShop.class, TrustedBlockShop.class, ElderBlockShop.class));

        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GOLD + "" + ChatColor.ITALIC + "Buy more blocks!");

        ArrayList<String> lore2 = new ArrayList<String>();
        lore2.add(ChatColor.GREEN + "" + ChatColor.ITALIC + "Level up!");

        ShopFactory.createShop(this, "Block Shop", BlockShopCatagory.class, Material.EMERALD, lore);
        ShopFactory.createShop(this, "Rank Shop", RankShop.class, Material.EXP_BOTTLE, lore2);
    }

    private void init() {
        commands = new Cmd[] {
                new CmdEndGame(),
                new CmdLVote(),
                new CmdRules(),
                new CmdSetupMap()
        };

        getDataFolder().mkdir();
        new File(getDataFolder() + "/maps").mkdir();
        File configFileUsers = new File(getDataFolder(), "userinfo.yml");
        if (!configFileUsers.exists())
            try {
                configFileUsers.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        userManager = new UserManager();
        um = new com.crossge.necessities.RankManager.UserManager();
        rm = new RankManager();
        uuiDs = new GetUUID();
    }

    private boolean setupEcon() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public ItemStack getRules() {
        return rules;
    }

    public Economy getEconomy() {
        return econ;
    }

    public GetUUID getUUIDs() {
        return uuiDs;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public com.crossge.necessities.RankManager.UserManager getNecessitiesUserManager() {
        return um;
    }

    public RankManager getRankManager() {
        return rm;
    }

    public void removeFromSetup(UUID uuid) {
        setups.remove(uuid);
    }

    public void addToSetup(UUID uuid, SetupMap setup) {
        setups.put(uuid, setup);
    }

    public HashMap<UUID, SetupMap> getSetups() {
        return setups;
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
        for (Cmd possible : commands) {
            if (possible.getName().equals(name))
                return possible;
        }
        return null;
    }

    public ClassicPhysics getPhysics() {
        return physics;
    }

    public ClassicPhysicsHandler getPhysicsHandler() {
        return physics.getPhysicsHandler();
    }
}