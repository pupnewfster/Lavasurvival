package me.eddiep;

import com.google.gson.Gson;
import me.eddiep.commands.*;
import me.eddiep.game.Gamemode;
import me.eddiep.game.LavaMap;
import me.eddiep.game.impl.*;
import me.eddiep.game.shop.ShopFactory;
import me.eddiep.game.shop.impl.*;
import me.eddiep.ggbot.*;
import me.eddiep.ranks.*;
import me.eddiep.system.PlayerListener;
import me.eddiep.system.setup.SetupMap;
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
    private HashMap<UUID, SetupMap> setups = new HashMap<UUID, SetupMap>();
    private Economy econ;
    private RankManager rankManager;
    private GGBot ggbot;
    private GGBotModeration ggbotModeration;
    private GGBotWarn ggbotWarn;
    private UUIDs uuiDs;
    private UserManager userManager;
    private CmdHide hide;
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
            ArrayList<String> lore = new ArrayList<String>();
            lore.add(ChatColor.ITALIC + "Current Balance: " + ChatColor.RESET + econ.format(econ.getBalance(player)));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(inv.firstEmpty(), item);
            return;
        }

        ItemStack item = inv.getItem(index);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>();
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

    @Override
    public void onDisable() {
        UserManager um = new UserManager();
        um.unload();
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
        rankManager = new RankManager();
        uuiDs = new UUIDs();
        ggbotWarn = new GGBotWarn();
        ggbotModeration = new GGBotModeration();
        ggbot = new GGBot();
        hide = new CmdHide();
        rankManager.setRanks();
        rankManager.readRanks();
        uuiDs.initiate();
        ggbotModeration.initiate();
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

    public CmdHide getHide() {
        return hide;
    }

    public ItemStack getRules() {
        return rules;
    }

    public Economy getEconomy() {
        return econ;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public UUIDs getUUIDs() {
        return uuiDs;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public GGBot getGGBot() {
        return ggbot;
    }

    public GGBotWarn getGGBotWarn() {
        return ggbotWarn;
    }

    public GGBotModeration getGGBotModeration() {
        return ggbotModeration;
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

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return getCmd(cmd.getName()).commandUse(sender, args);
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender == null || cmd == null)
            return null;
        List<String> tab = getCmd(cmd.getName()).tabComplete(sender, args);
        return (tab == null || tab.isEmpty()) ? null : tab;
    }

    private Cmd getCmd(String name) {
        Cmd com = new Cmd();
        if (name.equalsIgnoreCase("promote"))
            com = new CmdPromote();
        else if (name.equalsIgnoreCase("demote"))
            com = new CmdDemote();
        else if (name.equalsIgnoreCase("setrank"))
            com = new CmdSetrank();
        else if (name.equalsIgnoreCase("join"))
            com = new CmdJoin();
        else if (name.equalsIgnoreCase("lvote"))
            com = new CmdLVote();
        else if (name.equalsIgnoreCase("setupmap"))
            com = new CmdSetupMap();
        else if (name.equalsIgnoreCase("warn"))
            com = new CmdWarn();
        else if (name.equalsIgnoreCase("hide"))
            com = new CmdHide();
        else if (name.equalsIgnoreCase("opchat"))
            com = new CmdOpchat();
        else if (name.equalsIgnoreCase("kick"))
            com = new CmdKick();
        else if (name.equalsIgnoreCase("ban"))
            com = new CmdBan();
        else if (name.equalsIgnoreCase("endgame"))
            com = new CmdEndGame();
        else if (name.equalsIgnoreCase("rules"))
            com = new CmdRules();
        return com;
    }
}