package me.eddiep.ranks;

import java.io.File;
import java.util.ArrayList;

import me.eddiep.Lavasurvival;
import org.bukkit.configuration.file.YamlConfiguration;

public class Rank {
    private File configFileRanks = new File(Lavasurvival.INSTANCE.getDataFolder(), "ranks.yml");
    private ArrayList<String> permissions = new ArrayList<String>();
    private Rank previous;
    private Rank next;
    private String title = "";
    private String name = "";

    public Rank(String name) {
        this.name = name;
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        if(configRanks.contains(getName() + ".rankTitle"))
            this.title = configRanks.getString(getName() + ".rankTitle");
        if(configRanks.contains(getName() + ".previousRank")) {
            this.previous = Lavasurvival.INSTANCE.getRankManager().getRank(configRanks.getString(getName() + ".previousRank"));
            this.previous.setNext(this);
        }
        setPerms();
    }

    public void setNext(Rank r) {
        this.next = r;
    }

    public void setPrevious(Rank r) {
        this.previous = r;
    }

    public Rank getNext() {
        return this.next;
    }

    private void setPerms() {
        if(this.previous != null)
            for(String node : this.previous.getNodes())
                this.permissions.add(node);
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        for(String node : configRanks.getStringList(getName() + ".permissions"))
            this.permissions.add(node);
    }

    public void refreshPerms() {
        this.permissions.clear();
        setPerms();
        if(this.next != null)
            this.next.refreshPerms();
    }

    public void addPerm(String permission) {
        this.permissions.add(permission);
        refreshPerms();
    }

    public void removePerm(String permission) {
        this.permissions.remove(permission);
        refreshPerms();
    }

    public ArrayList<String> getNodes() {
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
}