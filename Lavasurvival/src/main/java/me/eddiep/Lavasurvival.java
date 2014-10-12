package me.eddiep;

import com.google.gson.Gson;
import me.eddiep.commands.*;
import me.eddiep.game.Gamemode;
import me.eddiep.game.LavaMap;
import me.eddiep.game.impl.LavaFlood;
import me.eddiep.game.shop.ShopFactory;
import me.eddiep.game.shop.impl.*;
import me.eddiep.ranks.RankManager;
import me.eddiep.ranks.UUIDs;
import me.eddiep.ranks.UserManager;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class Lavasurvival extends JavaPlugin {
    public static final Gson GSON = new Gson();
    public static Lavasurvival INSTANCE;
    private final Runnable MONEY_VIEWER = new Runnable() {
        @Override
        public void run() {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            for (Player player : players) {
                Inventory inv = player.getInventory();

                ShopFactory.validateInventory(inv);

                int index = inv.first(Material.GOLD_INGOT);

                if (index == -1) {
                    ItemStack item = new ItemStack(Material.GOLD_INGOT);

                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.GOLD + "Balance");

                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add(ChatColor.ITALIC + "Current Balance: " + ChatColor.RESET + econ.format(econ.getBalance(player)));

                    meta.setLore(lore);

                    item.setItemMeta(meta);

                    inv.setItem(inv.firstEmpty(), item);
                    continue;
                }

                ItemStack item = inv.getItem(index);

                ItemMeta meta = item.getItemMeta();
                ArrayList<String> lore = new ArrayList<String>();
                lore.add(ChatColor.ITALIC + "Current Balance: " + ChatColor.RESET + econ.format(econ.getBalance(player)));

                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    private HashMap<UUID, SetupMap> setups = new HashMap<UUID, SetupMap>();
    private Economy econ;
    private RankManager rankManager;
    private UUIDs uuiDs;
    private UserManager userManager;
    private boolean running = false;
    private int moneyViewer;

    public static void log(String message) {
        INSTANCE.getLogger().info(message);
    }

    public static void globalMessage(String message) {
        if (Gamemode.getCurrentGame() != null)
            Gamemode.getCurrentGame().globalMessage(message);
        else //Sends to everyone
            Bukkit.broadcastMessage(message);
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        getDataFolder().mkdir();
        init();

        log("Attaching to Vault..");
        if (!setupEcon()) {
            log("Disabling, no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //Setup the shops after connecting to vault
        setupShops();

        /*log("Making money viewer task..");
        moneyViewer = getServer().getScheduler().scheduleSyncRepeatingTask(this, MONEY_VIEWER, 0, 25);*/

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        LavaFlood flood = new LavaFlood();
        if (LavaMap.getPossibleMaps().length > 0) {
            flood.prepare();
            flood.start();
            running = true;
        }
    }

    @Override
    public void onDisable() {
        UserManager um = new UserManager();
        um.unload();
        if (running) {
            log("Stopping game..");
            Gamemode.getCurrentGame().end();
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
        rankManager.setRanks();
        rankManager.readRanks();
        uuiDs.initiate();
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

    public void removeFromSetup(UUID uuid) {
        setups.remove(uuid);
    }

    public void addToSetup(UUID uuid, SetupMap setup) {
        setups.put(uuid, setup);
    }

    public HashMap<UUID, SetupMap> getSetups() {
        return setups;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Cmd com = new Cmd();
        if (cmd.getName().equalsIgnoreCase("promote"))
            com = new CmdPromote();
        else if (cmd.getName().equalsIgnoreCase("demote"))
            com = new CmdDemote();
        else if (cmd.getName().equalsIgnoreCase("setrank"))
            com = new CmdSetrank();
        else if (cmd.getName().equalsIgnoreCase("join"))
            com = new CmdJoin();
        else if (cmd.getName().equalsIgnoreCase("lvote"))
            com = new CmdLVote();
        else if (cmd.getName().equalsIgnoreCase("setupmap"))
            com = new CmdSetupMap();
        return com.commandUse(sender, args);
    }
}
