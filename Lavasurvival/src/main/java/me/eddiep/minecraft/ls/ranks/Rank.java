package me.eddiep.minecraft.ls.ranks;

import me.eddiep.minecraft.ls.Lavasurvival;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class Rank {
    private File configFileRanks = new File(Lavasurvival.INSTANCE.getDataFolder(), "ranks.yml");
    private ArrayList<String> permissions = new ArrayList<String>();
    private Rank previous;
    private Rank next;
    private String title = "";
    private String name = "";

    public Rank(String name) {
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        this.name = name;
        if (configRanks.contains(getName() + ".rankTitle"))
            this.title = configRanks.getString(getName() + ".rankTitle");
        if (configRanks.contains(getName() + ".previousRank")) {
            this.previous = Lavasurvival.INSTANCE.getRankManager().getRank(configRanks.getString(getName() + ".previousRank"));
            getPrevious().setNext(this);
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
            for (String node : this.previous.getNodes())
                this.permissions.add(node);
        YamlConfiguration configRanks = YamlConfiguration.loadConfiguration(configFileRanks);
        for (String node : configRanks.getStringList(getName() + ".permissions"))
            this.permissions.add(node);
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