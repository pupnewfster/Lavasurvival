package me.eddiep.ranks;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import me.eddiep.Lavasurvival;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class UserManager {
    private File configFileUsers = new File(Lavasurvival.INSTANCE.getDataFolder(), "userinfo.yml");
    private static HashMap<UUID, UserInfo> players = new HashMap<UUID, UserInfo>();

    public void readUsers() {
        for(Player p : Bukkit.getOnlinePlayers())
            parseUser(p);
    }

    public void parseUser(final Player p) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
            @Override
            public void run() {
                if(players.containsKey(p.getUniqueId()))
                    players.get(p.getUniqueId()).setPlayer(p);
                else
                    players.put(p.getUniqueId(), new UserInfo(p));
                players.get(p.getUniqueId()).givePerms();
            }
        });
    }

    public HashMap<UUID, UserInfo> getUsers() {
        return players;
    }

    public UserInfo getUser(UUID uuid) {
        if(!players.containsKey(uuid))
            return new UserInfo(uuid);
        return players.get(uuid);
    }

    public void unload() {
        for(UUID uuid : players.keySet())
            players.get(uuid).removePerms();
    }

    public void addUser(Player player) {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        if(configUsers.contains(player.getUniqueId().toString()))
            return;
        configUsers.set(player.getUniqueId().toString() + ".rank", "New");
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserRank(UserInfo u, Rank r) {
        if(r == null)
            return;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        configUsers.set(u.getUUID().toString() + ".rank", r.getName());
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        u.updateRank(r);
    }
}