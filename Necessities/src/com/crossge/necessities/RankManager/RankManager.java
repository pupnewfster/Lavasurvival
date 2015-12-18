package com.crossge.necessities.RankManager;

import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.*;

public class RankManager {
    private File configFileRanks = new File("plugins/Necessities/RankManager", "ranks.yml"), configFileSubranks = new File("plugins/Necessities/RankManager", "subranks.yml");
    private static HashMap<String, String> subranks = new HashMap<>();
    private static HashMap<String, Rank> ranks = new HashMap<>();
    private static ArrayList<Rank> order = new ArrayList<>();
    private static ArrayList<String> names = new ArrayList<>();

    public void readRanks() {
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks), configSubranks = YamlConfiguration.loadConfiguration(configFileSubranks);
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
        for (String subrank : configSubranks.getKeys(true))
            if (!subrank.equals("") && !configSubranks.getStringList(subrank).isEmpty())//If is an actual subrank not just base node in tree of a subrank
                subranks.put(subrank.toLowerCase(), subrank);
        UserManager um = new UserManager();
        um.readUsers();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Retrieving all permissions.");
                updatePerms();
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "All permissions retrieved.");
            }
        });
    }

    private void updatePerms() {
        ArrayList<String> p = new ArrayList<>();
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

    public void reloadPermissions() {
        UserManager um = new UserManager();
        for (Rank r : getOrder()) {
            r.refreshPerms();
            um.refreshRankPerm(r);
        }
    }

    public boolean validSubrank(String subrank) {
        return !subranks.containsKey(subrank.toLowerCase());
    }

    public String getSub(String subrank) {
        return subranks.get(subrank.toLowerCase());
    }

    public ArrayList<Rank> getOrder() {
        return order;
    }

    public Collection<String> getSubranks() {
        return subranks.values();
    }

    public Rank getRank(int index) {
        return order.size() - 1 < index ? null : order.get(index);
    }

    public Rank getRank(String name) {
        return ranks.get(name);
    }

    public boolean hasRank(Rank rank, Rank check) {
        return !(!order.contains(check) || !order.contains(rank)) && order.indexOf(rank) - order.indexOf(check) >= 0;
    }

    public void updateRankPerms(Rank r, String permission, boolean remove) {
        if (permission.equals(""))
            return;
        UserManager um = new UserManager();
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        List<String> perms = configRanks.getStringList(r.getName() + ".permissions");
        if (perms.contains(""))
            perms.remove("");
        if (remove) {
            perms.remove(permission);
            if (perms.isEmpty())
                perms.add("");
            configRanks.set(r.getName() + ".permissions", perms);
            r.removePerm(permission);
            um.delRankPerm(r, permission);

        } else {
            perms.add(permission);
            configRanks.set(r.getName() + ".permissions", perms);
            r.addPerm(permission);
            um.addRankPerm(r, permission);
        }
        try {
            configRanks.save(configFileRanks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSubPerms(String subrank, String permission, boolean remove) {
        if (permission.equals(""))
            return;
        YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(configFileSubranks);
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        UserManager um = new UserManager();
        List<String> perms = configSubranks.getStringList(subrank);
        if (perms.contains(""))
            perms.remove("");
        if (remove) {
            perms.remove(permission);
            if (perms.isEmpty())
                perms.add("");
            configSubranks.set(subrank, perms);
        } else {
            perms.add(permission);
            configSubranks.set(subrank, perms);
        }
        try {
            configSubranks.save(configFileSubranks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Rank r : order)
            if (configRanks.contains(r.getName()) && configRanks.getStringList(r.getName() + ".subranks").contains(subrank)) {
                r.refreshPerms();
                um.refreshRankPerm(r);
            }
    }

    public void updateRankSubrank(Rank r, String name, boolean remove) {
        if (name.equals(""))
            return;
        UserManager um = new UserManager();
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        List<String> subranks = configRanks.getStringList(r.getName() + ".subranks");
        if (subranks.contains(""))
            subranks.remove("");
        if (remove)
            subranks.remove(name);
        else
            subranks.add(name);
        if (subranks.isEmpty())
            subranks.add("");
        configRanks.set(r.getName() + ".subranks", subranks);
        try {
            configRanks.save(configFileRanks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        r.refreshPerms();
        um.refreshRankPerm(r);
    }

    public void addRank(String name, Rank previous, Rank next) {
        if (name.equals(""))
            return;
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        configRanks.set(name, Arrays.asList(""));
        configRanks.set(name + ".permissions", Arrays.asList(""));
        configRanks.set(name + ".subranks", Arrays.asList(""));
        configRanks.set(name + ".rankTitle", "[" + name + "]");
        if (previous != null)
            configRanks.set(name + ".previousRank", previous.getName());
        try {
            configRanks.save(configFileRanks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (previous == null) {
            ranks.put(name, new Rank(name));
            if (next != null)
                next.setPrevious(ranks.get(name));
            order.add(0, ranks.get(name));
        } else {
            ranks.put(name, new Rank(name));
            previous.setNext(ranks.get(name));
            if (next != null)
                next.setPrevious(ranks.get(name));
            order.add(order.indexOf(previous) + 1, ranks.get(name));
        }
    }

    public void removeRank(Rank rank) {
        UserManager um = new UserManager();
        Rank previous = rank.getPrevious();
        Rank next = rank.getNext();
        for (User u : um.getUsers().values())
            if (u.getRank().equals(rank)) {
                if (next != null)
                    u.setRank(next);
                else if (previous != null)
                    u.setRank(previous);
            }
        order.remove(rank);
        ranks.remove(rank.getName());
        if (previous != null && next != null) {
            next.setPrevious(previous);
            previous.setNext(next);
        } else if (previous == null && next != null)
            next.setPrevious(null);
        else if (previous != null)
            previous.setNext(null);
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        configRanks.set(rank.getName(), null);
        try {
            configRanks.save(configFileRanks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (next != null)
            um.refreshRankPerm(next);
        else if (previous != null)
            um.refreshRankPerm(previous);
    }

    public void addSubrank(String name) {
        if (name.equals(""))
            return;
        YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(configFileSubranks);
        configSubranks.set(name, Arrays.asList(""));
        subranks.put(name.toLowerCase(), name);
        try {
            configSubranks.save(configFileSubranks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeSubrank(String name) {
        if (name.equals(""))
            return;
        UserManager um = new UserManager();
        for (User u : um.getUsers().values())
            um.updateUserSubrank(u.getUUID(), name, true);
        for (Rank r : order)
            updateRankSubrank(r, name, true);
        YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(configFileSubranks);
        configSubranks.set(name, null);
        subranks.remove(name.toLowerCase());
        try {
            configSubranks.save(configFileSubranks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSubranks() {//TODO: Change these to something for LS or just use the normal rank permissions spot
        if (!configFileSubranks.exists())
            try {
                configFileSubranks.createNewFile();
                YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(configFileSubranks);
                configSubranks.set("Necessities.Donator", Arrays.asList("Necessities.colorchat", "lavasurvival.donator"));;
                configSubranks.save(configFileSubranks);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void setRanks() {
        if (!configFileRanks.exists())
            try {
                configFileRanks.createNewFile();
                YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
                configRanks.set("New.permissions", Arrays.asList(""));
                configRanks.set("New.subranks", Arrays.asList(""));
                configRanks.set("New.rankTitle", "&4[&7New&4]&7");
                configRanks.set("Basic.permissions", Arrays.asList(""));
                configRanks.set("Basic.subranks", Arrays.asList(""));
                configRanks.set("Basic.rankTitle", "&4[&bBasic&4]&1");
                configRanks.set("Basic.previousRank", "New");
                configRanks.set("Advanced.permissions", Arrays.asList(""));
                configRanks.set("Advanced.subranks", Arrays.asList(""));
                configRanks.set("Advanced.rankTitle", "&4[&3Advanced&4]&9");
                configRanks.set("Advanced.previousRank", "Basic");
                configRanks.set("Survivor.permissions", Arrays.asList(""));
                configRanks.set("Survivor.subranks", Arrays.asList(""));
                configRanks.set("Survivor.rankTitle", "&4[&2Survivor&4]&2");
                configRanks.set("Survivor.previousRank", "Advanced");
                configRanks.set("Trusted.permissions", Arrays.asList(""));
                configRanks.set("Trusted.subranks", Arrays.asList(""));
                configRanks.set("Trusted.rankTitle", "&4[&l&fTrusted&r&4]&f");
                configRanks.set("Trusted.previousRank", "Survivor");
                configRanks.set("Elder.permissions", Arrays.asList(""));
                configRanks.set("Elder.subranks", Arrays.asList(""));
                configRanks.set("Elder.rankTitle", "&4[&l&fElder&4]&d");
                configRanks.set("Elder.previousRank", "Trusted");
                configRanks.set("Moderator.permissions", Arrays.asList("lavasurvival.setup", "lavasurvival.voteSpeak", "Necessities.promote", "Necessities.demote", "Necessities.setrank", "Necessities.warn",
                        "Necessities.hide", "Necessities.opchat", "Necessities.kick", "Necessities.gamemode", "Necessities.teleport"));
                configRanks.set("Moderator.subranks", Arrays.asList(""));
                configRanks.set("Moderator.rankTitle", "&4[&2Mod&4]&a");
                configRanks.set("Moderator.previousRank", "Elder");
                configRanks.set("Admin.permissions", Arrays.asList("Necessities.ban", "Necessities.tempban", "Necessities.unban", "Necessities.unbanip", "Necessities.banip", "lavasurvival.endGame"));
                configRanks.set("Admin.subranks", Arrays.asList(""));
                configRanks.set("Admin.rankTitle", "&4[&1Admin&4]&b");
                configRanks.set("Admin.previousRank", "Moderator");
                configRanks.set("Manager.permissions", Arrays.asList("*", "-Necessities.rankmanager.setranksame", "minecraft.command.*", "bukkit.broadcast.*", "-minecraft.command.deop",
                        "bukkit.command.whitelist.*", "bukkit.command.gamerule", "-minecraft.command.op", "-bukkit.command.op"));
                configRanks.set("Manager.subranks", Arrays.asList("*"));
                configRanks.set("Manager.rankTitle", "&4[&1Manager&4]&6b");
                configRanks.set("Manager.previousRank", "Admin");
                configRanks.set("Director.permissions", Arrays.asList("Necessities.rankmanager.setranksame", "minecraft.command.gamerule", "minecraft.command.deop", "minecraft.command.op",
                        "bukkit.command.op"));
                configRanks.set("Director.subranks", Arrays.asList(""));
                configRanks.set("Director.rankTitle", "&4[&bDirector&4]&6");
                configRanks.set("Director.previousRank", "Manager");
                configRanks.save(configFileRanks);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}