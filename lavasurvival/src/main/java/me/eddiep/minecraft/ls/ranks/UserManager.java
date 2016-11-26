package me.eddiep.minecraft.ls.ranks;

import me.eddiep.minecraft.ls.Lavasurvival;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    private static final HashMap<UUID, UserInfo> players = new HashMap<>();
    private final File configFileUsers = new File(Lavasurvival.INSTANCE.getDataFolder(), "userinfo.yml");

    public void readUsers() {
        Bukkit.getOnlinePlayers().forEach(this::parseUser);
    }

    private void parseUser(final Player p) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> {
            if (players.containsKey(p.getUniqueId()))
                players.get(p.getUniqueId()).setPlayer(p);
            else
                players.put(p.getUniqueId(), new UserInfo(p));
        });
    }

    public void forceParseUser(final Player p) {
        if (players.containsKey(p.getUniqueId()))
            players.get(p.getUniqueId()).setPlayer(p);
        else
            players.put(p.getUniqueId(), new UserInfo(p));
    }

    public void saveAll() {
        players.keySet().forEach(uuid -> players.get(uuid).save());
    }

    @SuppressWarnings("unused")
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