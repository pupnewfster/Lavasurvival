package me.eddiep.ranks;

import me.eddiep.Lavasurvival;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RankManager {
    private static HashMap<String, Rank> ranks = new HashMap<String, Rank>();
    private static ArrayList<Rank> order = new ArrayList<Rank>();
    private File configFileRanks = new File(Lavasurvival.INSTANCE.getDataFolder(), "ranks.yml");

    public void readRanks() {
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        ArrayList<String> names = new ArrayList<String>();
        for (String rank : configRanks.getKeys(false)) {
            if (configRanks.contains(rank + ".previousRank")) {
                if (names.contains(configRanks.getString(rank + ".previousRank")))
                    names.add(names.indexOf(configRanks.getString(rank + ".previousRank")) + 1, rank);
                else {
                    String previous = configRanks.getString(rank + ".previousRank");
                    while (configRanks.contains(previous + ".previousRank")) {
                        previous = configRanks.getString(previous + ".previousRank");
                        if (names.contains(previous)) {
                            names.add(names.indexOf(previous) + 1, rank);
                            break;
                        }
                    }
                }
            } else if (!names.contains(rank))
                names.add(0, rank);
        }
        for (String name : names) {
            ranks.put(name, new Rank(name));
            order.add(ranks.get(name));
        }
        UserManager um = Lavasurvival.INSTANCE.getUserManager();
        um.readUsers();
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
            @Override
            public void run() {
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Retrieving all permissions.");
                updatePerms();
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "All permissions retrieved.");
            }
        });
    }

    private void updatePerms() {
        ArrayList<String> p = new ArrayList<String>();
        for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
            for (String t : perm.getChildren().keySet())
                if (!p.contains(t))
                    p.add(t);
            if (!p.contains(perm.getName())) {
                perm.addParent("*", true);
                p.add(perm.getName());
            }
        }
    }

    public ArrayList<Rank> getOrder() {
        return order;
    }

    public Rank getRank(String name) {
        return ranks.get(name);
    }

    public boolean hasRank(Rank rank, Rank check) {//Useful to check if player already bought a rank
        return !(rank == null || check == null || !order.contains(check) || !order.contains(rank)) && order.indexOf(rank) - order.indexOf(check) >= 0;
    }

    public void setRanks() {
        if (!configFileRanks.exists())
            try {
                configFileRanks.createNewFile();
                YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
                configRanks.set("New.permissions", Arrays.asList(""));
                configRanks.set("New.rankTitle", "&f[&7New&f]");
                configRanks.set("Basic.permissions", Arrays.asList(""));
                configRanks.set("Basic.rankTitle", "&f[&bBasic&f]");
                configRanks.set("Basic.previousRank", "New");
                configRanks.set("Advanced.permissions", Arrays.asList(""));
                configRanks.set("Advanced.rankTitle", "&f[&1Advanced&f]");
                configRanks.set("Advanced.previousRank", "Basic");
                configRanks.set("Survivor.permissions", Arrays.asList(""));
                configRanks.set("Survivor.rankTitle", "&f[&4Survivor&f]");
                configRanks.set("Survivor.previousRank", "Advanced");
                configRanks.set("Trusted.permissions", Arrays.asList(""));
                configRanks.set("Trusted.rankTitle", "&8[&fTrusted&8]&f");
                configRanks.set("Trusted.previousRank", "Survivor");
                configRanks.set("Elder.permissions", Arrays.asList(""));
                configRanks.set("Elder.rankTitle", "&f[&0Elder&f]");
                configRanks.set("Elder.previousRank", "Trusted");
                configRanks.set("Op.permissions", Arrays.asList("lavasurvival.setup", "lavasurvival.promote", "lavasurvival.demote", "lavasurvival.setrank",
                        "lavasurvival.voteSpeak"));
                configRanks.set("Op.rankTitle", "&f[&6Op&f]");
                configRanks.set("Op.previousRank", "Elder");
                configRanks.set("Admin.permissions", Arrays.asList(""));
                configRanks.set("Admin.rankTitle", "&f[&cAdmin&f]");
                configRanks.set("Admin.previousRank", "Op");
                configRanks.save(configFileRanks);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}