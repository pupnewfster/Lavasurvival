package me.eddiep.minecraft.ls.ranks;

import me.eddiep.minecraft.ls.Lavasurvival;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    private static HashMap<UUID, UserInfo> players = new HashMap<>();
    private File configFileUsers = new File(Lavasurvival.INSTANCE.getDataFolder(), "userinfo.yml");

    public void readUsers() {
        for (Player p : Bukkit.getOnlinePlayers())
            parseUser(p);
    }

    public void parseUser(final Player p) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
            @Override
            public void run() {
                if (players.containsKey(p.getUniqueId()))
                    players.get(p.getUniqueId()).setPlayer(p);
                else
                    players.put(p.getUniqueId(), new UserInfo(p));
            }
        });
    }

    public HashMap<UUID, UserInfo> getUsers() {
        return players;
    }

    public UserInfo getUser(UUID uuid) {
        if (!players.containsKey(uuid))
            return new UserInfo(uuid);
        return players.get(uuid);
    }

    public void addUser(Player player) {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        configUsers.set(player.getUniqueId().toString() + ".lastName", player.getName());
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}