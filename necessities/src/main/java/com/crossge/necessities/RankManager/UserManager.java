package com.crossge.necessities.RankManager;

import com.crossge.necessities.Hats.Hat;
import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserManager {
    private File configFileUsers = new File("plugins/Necessities/RankManager", "users.yml");
    private static HashMap<UUID, User> players = new HashMap<>();
    RankManager rm = new RankManager();

    public void readUsers() {
        Bukkit.getOnlinePlayers().forEach(this::parseUser);
    }

    public void parseUser(final Player p) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> forceParseUser(p));
    }

    public void forceParseUser(Player p) {
        players.put(p.getUniqueId(), new User(p));
        players.get(p.getUniqueId()).givePerms();
    }

    public HashMap<UUID, User> getUsers() {
        return players;
    }

    public void removeUser(UUID uuid) {
        players.remove(uuid);
    }

    public User getUser(UUID uuid) {
        return !players.containsKey(uuid) ? new User(uuid) : players.get(uuid);
    }

    public void unload() {
        for (UUID uuid : players.keySet()) {
            User u = players.get(uuid);
            Hat h = u.getHat();
            if (h != null)
                h.despawn();
            u.updateTimePlayed();
            u.removePerms();
        }
    }

    public void addRankPerm(Rank r, String node) {
        if (players == null)
            return;
        players.keySet().stream().filter(uuid -> rm.hasRank(players.get(uuid).getRank(), r)).forEach(uuid -> players.get(uuid).addPerm(node));
    }

    public void delRankPerm(Rank r, String node) {
        if (players == null)
            return;
        players.keySet().stream().filter(uuid -> rm.hasRank(players.get(uuid).getRank(), r)).forEach(uuid -> players.get(uuid).removePerm(node));
    }

    public void refreshRankPerm(Rank r) {
        if (players == null)
            return;
        for (UUID uuid : players.keySet())
            if (rm.hasRank(players.get(uuid).getRank(), r))
                players.get(uuid).refreshPerms();
    }

    public void addUser(Player player) {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        if (configUsers.contains(player.getUniqueId().toString()))
            return;
        configUsers.set(player.getUniqueId().toString() + ".rank", rm.getRank(0).getName());
        configUsers.set(player.getUniqueId().toString() + ".permissions", Arrays.asList(""));
        configUsers.set(player.getUniqueId().toString() + ".subranks", Arrays.asList(""));
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
        }
    }

    public void updateUserRank(User u, UUID uuid, Rank r) {
        if (r == null)
            return;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        configUsers.set(uuid.toString() + ".rank", r.getName());
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
        }
        u.updateRank(r);
    }

    public void updateUserPerms(UUID uuid, String permission, boolean remove) {
        if (permission.equals(""))
            return;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        List<String> perms = configUsers.getStringList(uuid.toString() + ".permissions");
        if (perms.contains(""))
            perms.remove("");
        if (remove) {
            perms.remove(permission);
            if (perms.isEmpty())
                perms.add("");
            configUsers.set(uuid.toString() + ".permissions", perms);
            if (players.containsKey(uuid))
                getUser(uuid).removePerm(permission);

        } else {
            perms.add(permission);
            configUsers.set(uuid.toString() + ".permissions", perms);
            if (players.containsKey(uuid))
                getUser(uuid).addPerm(permission);
        }
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
        }
    }

    public void updateUserSubrank(UUID uuid, String name, boolean remove) {
        if (name.equals(""))
            return;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        List<String> subranks = configUsers.getStringList(uuid.toString() + ".subranks");
        if (subranks.contains(""))
            subranks.remove("");
        if (remove)
            subranks.remove(name);
        else
            subranks.add(name);
        if (subranks.isEmpty())
            subranks.add("");
        configUsers.set(uuid.toString() + ".subranks", subranks);
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
        }
        if (players.containsKey(uuid))
            getUser(uuid).refreshPerms();
    }
}