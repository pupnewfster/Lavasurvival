package me.eddiep;

import com.google.gson.Gson;
import me.eddiep.game.Gamemode;
import me.eddiep.game.impl.LavaFlood;
import me.eddiep.game.shop.ShopFactory;
import me.eddiep.system.PlayerListener;
import me.eddiep.system.setup.SetupMap;
import net.milkbowl.vault.economy.Economy;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class Lavasurvival extends JavaPlugin {
    public static final Gson GSON = new Gson();
    public static Lavasurvival INSTANCE;

    private HashMap<UUID, SetupMap> setups = new HashMap<UUID, SetupMap>();
    private Economy econ;
    private boolean running = false;

    private int moneyViewer;

    @Override
    public void onEnable() {
        INSTANCE = this;
        getDataFolder().mkdir();

        log("Attaching to Vault..");
        if (!setupEcon()) {
            log("Disabling, no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        /*log("Making money viewer task..");
        moneyViewer = getServer().getScheduler().scheduleSyncRepeatingTask(this, MONEY_VIEWER, 0, 25);*/

        LavaFlood flood = new LavaFlood();
        flood.prepare();
        flood.start();
        running = true;
    }

    @Override
    public void onDisable() {
        if (running) {
            log("Stopping game..");
            Gamemode.getCurrentGame().end();
            log("Cleaning up..");
            Gamemode.cleanup();
            ShopFactory.cleanup();

            for (UUID uuid : setups.keySet()) {
                setups.get(uuid).end();
            }
            setups.clear();

            getServer().getScheduler().cancelTask(moneyViewer);

            log("Disabled");
        }
        running = false;
    }

    private void setupShops() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "Buy more blocks!");

        ArrayList<String> lore2 = new ArrayList<String>();
        lore2.add(ChatColor.ITALIC + "" + ChatColor.GREEN + "Level up!");

        ShopFactory.createShop(this, "Block Shop", "Block Shop", Material.EMERALD, lore);
        ShopFactory.createShop(this, "Rank Shop", "Block Shop", Material.EXP_BOTTLE, lore2);
    }

    private boolean setupEcon() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    public void removeFromSetup(UUID uuid) {
        setups.remove(uuid);
    }

    public static void log(String message) {
        INSTANCE.getLogger().info(message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setupmap")) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                return true;
            }
            if (sender instanceof Player) {
                Player p = (Player)sender;
                if (setups.containsKey(p.getUniqueId())) {
                    SetupMap s = setups.get(p.getUniqueId());
                    s.sendMessage("Aborted..");
                    s.end();
                    setups.remove(p.getUniqueId());
                } else {
                    SetupMap setup = new SetupMap(p, this);
                    setup.start();
                    setups.put(p.getUniqueId(), setup);
                }
            }
            else {
                sender.sendMessage("This command can only be used in game..");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("join")) {
            if (sender instanceof Player) {
                Player p = (Player)sender;
                if (Gamemode.getCurrentGame().isSpectator(p))
                    Gamemode.getCurrentGame().playerJoin(p);
                else {
                    p.sendMessage(ChatColor.DARK_RED + "You are already playing the current game!");
                }
            }
            else {
                sender.sendMessage("This command can only be used in game..");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("lvote")) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                PlayerListener listener = Gamemode.getPlayerListener();
                if (listener.voted.contains(player)) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You already voted!");
                    return true;
                }
                try {
                    int number = Integer.parseInt(args[0]);
                    number--;
                    if (number >= Gamemode.nextMaps.length) {
                        player.sendMessage(ChatColor.DARK_RED + "Invalid number! Please choose a number between (1 - " + Gamemode.nextMaps.length + ").");
                        return true;
                    }
                    listener.voted.add(player);
                    Gamemode.votes[number]++;
                    player.sendMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + "" + ChatColor.BOLD + "You voted for " + Gamemode.nextMaps[number].getName() + "!");
                    return true;
                } catch (Throwable t) {
                    player.sendMessage(ChatColor.DARK_RED + "Invalid number! Please choose a number between (1 - " + Gamemode.nextMaps.length + ").");
                    return true;
                }
            }
            else {
                sender.sendMessage("This command can only be used in game..");
            }

            return true;
        }

        return false;
    }

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
        }
    };
}
