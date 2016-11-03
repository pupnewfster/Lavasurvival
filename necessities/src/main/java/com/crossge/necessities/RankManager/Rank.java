package com.crossge.necessities.RankManager;

import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.ArrayList;

public class Rank {
    private File configFileRanks = new File("plugins/Necessities/RankManager", "ranks.yml"), configFileSubranks = new File("plugins/Necessities/RankManager", "subranks.yml");
    private ArrayList<String> permissions = new ArrayList<>();
    private String title = "", name = "";
    private Rank previous, next;

    public Rank(String name) {
        this.name = name;
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        if (configRanks.contains(getName() + ".rankTitle"))
            this.title = configRanks.getString(getName() + ".rankTitle");
        if (configRanks.contains(getName() + ".previousRank")) {
            RankManager rm = Necessities.getInstance().getRM();
            this.previous = rm.getRank(configRanks.getString(getName() + ".previousRank"));
            this.previous.setNext(this);
        }
        setPerms();
    }

    public Rank getNext() {
        return this.next;
    }

    public void setNext(Rank r) {
        this.next = r;
    }

    private void setPerms() {
        if (this.previous != null)
            this.permissions.addAll(this.previous.getNodes());
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(this.configFileRanks);
        YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(this.configFileSubranks);
        configRanks.getStringList(getName() + ".subranks").stream().filter(subrank -> !subrank.equals("") && configSubranks.contains(subrank)).forEach(subrank -> this.permissions.addAll(configSubranks.getStringList(subrank)));
        this.permissions.addAll(configRanks.getStringList(getName() + ".permissions"));
    }

    void refreshPerms() {
        this.permissions.clear();
        setPerms();
        if (this.next != null)
            this.next.refreshPerms();
    }

    void addPerm(String permission) {
        this.permissions.add(permission);
        refreshPerms();
    }

    void removePerm(String permission) {
        this.permissions.remove(permission);
        refreshPerms();
    }

    ArrayList<String> getNodes() {
        return this.permissions;
    }

    public String getName() {
        return this.name;
    }

    public String getTitle() {
        return this.title;
    }

    public Rank getPrevious() {
        return this.previous;
    }

    void setPrevious(Rank r) {
        this.previous = r;
    }

    public String getColor() {
        return this.title.split("\\]").length == 1 ? ChatColor.RESET + "" : ChatColor.translateAlternateColorCodes('&', this.title.split("\\]")[1]).trim();
    }

    public String getCommands() {
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(this.configFileRanks);
        YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(this.configFileSubranks);
        String commands = "";
        for (String subrank : configRanks.getStringList(getName() + ".subranks"))
            if (!subrank.equals("") && configSubranks.contains(subrank))
                for (String node : configSubranks.getStringList(subrank))
                    commands += cmdName(node) + ", ";
        for (String node : configRanks.getStringList(getName() + ".permissions"))
            commands += cmdName(node) + ", ";
        return commands.equals("") ? "" : commands.trim().substring(0, commands.length() - 2);
    }

    private String cmdName(String node) {//TODO: finish
        Permission perm = Bukkit.getPluginManager().getPermission(node);
        return perm == null ? node : perm.getName();
    }
}