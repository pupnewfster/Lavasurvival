package com.crossge.necessities.RankManager;

import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.*;

public class RankManager {
    private final HashMap<String, String> subranks = new HashMap<>();
    private final HashMap<String, Rank> ranks = new HashMap<>();
    private final ArrayList<String> names = new ArrayList<>();
    private final ArrayList<Rank> order = new ArrayList<>();
    private final File configFileRanks = new File("plugins/Necessities/RankManager", "ranks.yml");
    private final File configFileSubranks = new File("plugins/Necessities/RankManager", "subranks.yml");

    public void readRanks() {
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(this.configFileRanks), configSubranks = YamlConfiguration.loadConfiguration(this.configFileSubranks);
        for (String rank : configRanks.getKeys(false)) {
            if (configRanks.contains(rank + ".previousRank")) {
                if (this.names.contains(configRanks.getString(rank + ".previousRank")))
                    this.names.add(this.names.indexOf(configRanks.getString(rank + ".previousRank")) + 1, rank);
                else {
                    String previous = configRanks.getString(rank + ".previousRank");
                    while (configRanks.contains(previous + ".previousRank")) {
                        previous = configRanks.getString(previous + ".previousRank");
                        if (this.names.contains(previous)) {
                            this.names.add(this.names.indexOf(previous) + 1, rank);
                            break;
                        }
                    }
                }
            } else if (!this.names.contains(rank))
                this.names.add(0, rank);
        }
        for (String name : this.names) {
            this.ranks.put(name, new Rank(name));
            this.order.add(this.ranks.get(name));
        }
        //If is an actual subrank not just base node in tree of a subrank
        configSubranks.getKeys(true).stream().filter(subrank -> !subrank.equals("") && !configSubranks.getStringList(subrank).isEmpty()).forEach(subrank -> this.subranks.put(subrank.toLowerCase(), subrank));
        Necessities.getInstance().getUM().readUsers();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Retrieving all permissions.");
            updatePerms();
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "All permissions retrieved.");
        });
    }

    private void updatePerms() {
        ArrayList<String> p = new ArrayList<>();
        for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
            perm.getChildren().keySet().stream().filter(t -> !p.contains(t)).forEach(p::add);
            if (!p.contains(perm.getName())) {
                perm.addParent("*", true);
                p.add(perm.getName());
            }
        }
    }

    public void reloadPermissions() {
        for (Rank r : getOrder()) {
            r.refreshPerms();
            Necessities.getInstance().getUM().refreshRankPerm(r);
        }
    }

    public boolean validSubrank(String subrank) {
        return !this.subranks.containsKey(subrank.toLowerCase());
    }

    public String getSub(String subrank) {
        return this.subranks.get(subrank.toLowerCase());
    }

    public ArrayList<Rank> getOrder() {
        return this.order;
    }

    public Collection<String> getSubranks() {
        return this.subranks.values();
    }

    public Rank getRank(int index) {
        return this.order.size() - 1 < index ? null : this.order.get(index);
    }

    public Rank getRank(String name) {
        return this.ranks.get(name);
    }

    public boolean hasRank(Rank rank, Rank check) {
        return !(!this.order.contains(check) || !this.order.contains(rank)) && this.order.indexOf(rank) - this.order.indexOf(check) >= 0;
    }

    public void updateRankPerms(Rank r, String permission, boolean remove) {
        if (permission.equals(""))
            return;
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(this.configFileRanks);
        List<String> perms = configRanks.getStringList(r.getName() + ".permissions");
        if (perms.contains(""))
            perms.remove("");
        if (remove) {
            perms.remove(permission);
            if (perms.isEmpty())
                perms.add("");
            configRanks.set(r.getName() + ".permissions", perms);
            r.removePerm(permission);
            Necessities.getInstance().getUM().delRankPerm(r, permission);

        } else {
            perms.add(permission);
            configRanks.set(r.getName() + ".permissions", perms);
            r.addPerm(permission);
            Necessities.getInstance().getUM().addRankPerm(r, permission);
        }
        try {
            configRanks.save(this.configFileRanks);
        } catch (Exception ignored) {
        }
    }

    public void updateSubPerms(String subrank, String permission, boolean remove) {
        if (permission.equals(""))
            return;
        YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(this.configFileSubranks);
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(this.configFileRanks);
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
            configSubranks.save(this.configFileSubranks);
        } catch (Exception ignored) {
        }
        this.order.stream().filter(r -> configRanks.contains(r.getName()) && configRanks.getStringList(r.getName() + ".subranks").contains(subrank)).forEach(r -> {
            r.refreshPerms();
            Necessities.getInstance().getUM().refreshRankPerm(r);
        });
    }

    public void updateRankSubrank(Rank r, String name, boolean remove) {
        if (name.equals(""))
            return;
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(this.configFileRanks);
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
            configRanks.save(this.configFileRanks);
        } catch (Exception ignored) {
        }
        r.refreshPerms();
        Necessities.getInstance().getUM().refreshRankPerm(r);
    }

    public void addRank(String name, Rank previous, Rank next) {
        if (name.equals(""))
            return;
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(this.configFileRanks);
        configRanks.set(name, Collections.singletonList(""));
        configRanks.set(name + ".permissions", Collections.singletonList(""));
        configRanks.set(name + ".subranks", Collections.singletonList(""));
        configRanks.set(name + ".rankTitle", "[" + name + "]");
        if (previous != null)
            configRanks.set(name + ".previousRank", previous.getName());
        try {
            configRanks.save(this.configFileRanks);
        } catch (Exception ignored) {
        }
        if (previous == null) {
            this.ranks.put(name, new Rank(name));
            if (next != null)
                next.setPrevious(this.ranks.get(name));
            this.order.add(0, this.ranks.get(name));
        } else {
            this.ranks.put(name, new Rank(name));
            previous.setNext(this.ranks.get(name));
            if (next != null)
                next.setPrevious(this.ranks.get(name));
            this.order.add(this.order.indexOf(previous) + 1, this.ranks.get(name));
        }
    }

    public void removeRank(Rank rank) {
        UserManager um = Necessities.getInstance().getUM();
        Rank previous = rank.getPrevious();
        Rank next = rank.getNext();
        um.getUsers().values().stream().filter(u -> u.getRank().equals(rank)).forEach(u -> {
            if (next != null)
                u.setRank(next);
            else if (previous != null)
                u.setRank(previous);
        });
        this.order.remove(rank);
        this.ranks.remove(rank.getName());
        if (previous != null && next != null) {
            next.setPrevious(previous);
            previous.setNext(next);
        } else if (previous == null && next != null)
            next.setPrevious(null);
        else if (previous != null)
            previous.setNext(null);
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(this.configFileRanks);
        configRanks.set(rank.getName(), null);
        try {
            configRanks.save(this.configFileRanks);
        } catch (Exception ignored) {
        }
        if (next != null)
            um.refreshRankPerm(next);
        else if (previous != null)
            um.refreshRankPerm(previous);
    }

    public void addSubrank(String name) {
        if (name.equals(""))
            return;
        YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(this.configFileSubranks);
        configSubranks.set(name, Collections.singletonList(""));
        this.subranks.put(name.toLowerCase(), name);
        try {
            configSubranks.save(this.configFileSubranks);
        } catch (Exception ignored) {
        }
    }

    public void removeSubrank(String name) {
        if (name.equals(""))
            return;
        UserManager um = Necessities.getInstance().getUM();
        um.getUsers().values().forEach(u -> um.updateUserSubrank(u.getUUID(), name, true));
        this.order.forEach(r -> updateRankSubrank(r, name, true));
        YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(this.configFileSubranks);
        configSubranks.set(name, null);
        this.subranks.remove(name.toLowerCase());
        try {
            configSubranks.save(this.configFileSubranks);
        } catch (Exception ignored) {
        }
    }

    public void setSubranks() {//TODO: Change these to something for LS or just use the normal rank permissions spot
        if (!this.configFileSubranks.exists())
            try {
                //noinspection ResultOfMethodCallIgnored
                this.configFileSubranks.createNewFile();
                YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(this.configFileSubranks);
                configSubranks.set("Necessities.Donator", Arrays.asList("Necessities.colorchat", "lavasurvival.donator"));
                configSubranks.save(this.configFileSubranks);
            } catch (Exception ignored) {
            }
    }

    public void setRanks() {
        if (!this.configFileRanks.exists())
            try {
                //noinspection ResultOfMethodCallIgnored
                this.configFileRanks.createNewFile();
                YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(this.configFileRanks);
                configRanks.set("New.permissions", Arrays.asList("bukkit.broadcast.user", "bukkit.command.plugins", "Necessities.me", "-bukkit.command.list", "-minecraft.command.list"));
                configRanks.set("New.subranks", Collections.singletonList(""));
                configRanks.set("New.rankTitle", "&4[&7New&4]&7");
                configRanks.set("Basic.permissions", Collections.singletonList(""));
                configRanks.set("Basic.subranks", Collections.singletonList(""));
                configRanks.set("Basic.rankTitle", "&4[&bBasic&4]&1");
                configRanks.set("Basic.previousRank", "New");
                configRanks.set("Advanced.permissions", Arrays.asList("Necessities.title", "Necessities.bracket"));
                configRanks.set("Advanced.subranks", Collections.singletonList(""));
                configRanks.set("Advanced.rankTitle", "&4[&3Advanced&4]&9");
                configRanks.set("Advanced.previousRank", "Basic");
                configRanks.set("Survivor.permissions", Collections.singletonList("Necessities.nick"));
                configRanks.set("Survivor.subranks", Collections.singletonList(""));
                configRanks.set("Survivor.rankTitle", "&4[&2Survivor&4]&2");
                configRanks.set("Survivor.previousRank", "Advanced");
                configRanks.set("Trusted.permissions", Collections.singletonList(""));
                configRanks.set("Trusted.subranks", Collections.singletonList(""));
                configRanks.set("Trusted.rankTitle", "&4[&l&fTrusted&r&4]&f");
                configRanks.set("Trusted.previousRank", "Survivor");
                configRanks.set("Elder.permissions", Collections.singletonList(""));
                configRanks.set("Elder.subranks", Collections.singletonList(""));
                configRanks.set("Elder.rankTitle", "&4[&l&fElder&4]&d");
                configRanks.set("Elder.previousRank", "Trusted");
                configRanks.set("Moderator.permissions", Arrays.asList("lavasurvival.setup", "lavasurvival.voteSpeak", "Necessities.warn", "Necessities.hide", "Necessities.opchat", "Necessities.kick",
                        "Necessities.gamemode", "Necessities.teleport", "Necessities.slack"));
                configRanks.set("Moderator.subranks", Collections.singletonList(""));
                configRanks.set("Moderator.rankTitle", "&4[&2Mod&4]&a");
                configRanks.set("Moderator.previousRank", "Elder");
                configRanks.set("Admin.permissions", Arrays.asList("Necessities.ban", "Necessities.tempban", "Necessities.unban", "Necessities.unbanip", "Necessities.banip", "lavasurvival.endGame",
                        "Necessities.promote", "Necessities.demote", "Necessities.setrank"));
                configRanks.set("Admin.subranks", Collections.singletonList(""));
                configRanks.set("Admin.rankTitle", "&4[&1Admin&4]&b");
                configRanks.set("Admin.previousRank", "Moderator");
                configRanks.set("Manager.permissions", Arrays.asList("*", "-Necessities.rankmanager.setranksame", "minecraft.command.*", "bukkit.broadcast.*", "-minecraft.command.deop",
                        "bukkit.command.whitelist.*", "bukkit.command.gamerule", "-minecraft.command.op", "-bukkit.command.op"));
                configRanks.set("Manager.subranks", Collections.singletonList("*"));
                configRanks.set("Manager.rankTitle", "&4[&1Manager&4]&6b");
                configRanks.set("Manager.previousRank", "Admin");
                configRanks.set("Director.permissions", Arrays.asList("Necessities.rankmanager.setranksame", "minecraft.command.gamerule", "minecraft.command.deop", "minecraft.command.op",
                        "bukkit.command.op"));
                configRanks.set("Director.subranks", Collections.singletonList(""));
                configRanks.set("Director.rankTitle", "&4[&bDirector&4]&6");
                configRanks.set("Director.previousRank", "Manager");
                configRanks.save(this.configFileRanks);
            } catch (Exception ignored) {
            }
    }
}