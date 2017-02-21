package com.crossge.necessities.RankManager;

import com.crossge.necessities.Hats.Hat;
import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class UserManager {
    private final File configFileUsers = new File("plugins/Necessities/RankManager", "users.yml");
    private final HashMap<UUID, User> players = new HashMap<>();

    void readUsers() {
        Bukkit.getOnlinePlayers().forEach(this::parseUser);
    }

    private void parseUser(final Player p) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> forceParseUser(p));
    }

    public void forceParseUser(Player p) {
        this.players.put(p.getUniqueId(), new User(p));
        this.players.get(p.getUniqueId()).givePerms();
    }

    public HashMap<UUID, User> getUsers() {
        return this.players;
    }

    public void removeUser(UUID uuid) {
        this.players.remove(uuid);
    }

    public User getUser(UUID uuid) {
        return !this.players.containsKey(uuid) ? new User(uuid) : this.players.get(uuid);
    }

    public void unload() {
        for (Map.Entry<UUID, User> entry : this.players.entrySet()) {
            User u = this.players.get(entry.getKey());
            Hat h = u.getHat();
            if (h != null)
                h.despawn();
            u.updateTimePlayed();
            u.removePerms();
        }
    }

    void addRankPerm(Rank r, String node) {
        this.players.keySet().stream().filter(uuid -> Necessities.getRM().hasRank(this.players.get(uuid).getRank(), r)).forEach(uuid -> this.players.get(uuid).addPerm(node));
    }

    void delRankPerm(Rank r, String node) {
        this.players.keySet().stream().filter(uuid -> Necessities.getRM().hasRank(this.players.get(uuid).getRank(), r)).forEach(uuid -> this.players.get(uuid).removePerm(node));
    }

    void refreshRankPerm(Rank r) {
        this.players.keySet().stream().filter(uuid -> Necessities.getRM().hasRank(this.players.get(uuid).getRank(), r)).forEach(uuid -> this.players.get(uuid).refreshPerms());
    }

    public void addUser(Player player) {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(this.configFileUsers);
        if (configUsers.contains(player.getUniqueId().toString()))
            return;
        configUsers.set(player.getUniqueId().toString() + ".rank", Necessities.getRM().getRank(0).getName());
        configUsers.set(player.getUniqueId().toString() + ".permissions", Collections.singletonList(""));
        configUsers.set(player.getUniqueId().toString() + ".subranks", Collections.singletonList(""));
        try {
            configUsers.save(this.configFileUsers);
        } catch (Exception ignored) {
        }
    }

    public void updateUserRank(User u, UUID uuid, Rank r) {
        if (r == null)
            return;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(this.configFileUsers);
        configUsers.set(uuid.toString() + ".rank", r.getName());
        try {
            configUsers.save(this.configFileUsers);
        } catch (Exception ignored) {
        }
        u.updateRank(r);
    }

    public void updateUserPerms(UUID uuid, String permission, boolean remove) {
        if (permission.equals(""))
            return;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(this.configFileUsers);
        List<String> perms = configUsers.getStringList(uuid.toString() + ".permissions");
        if (perms.contains(""))
            perms.remove("");
        if (remove) {
            perms.remove(permission);
            if (perms.isEmpty())
                perms.add("");
            configUsers.set(uuid.toString() + ".permissions", perms);
            if (this.players.containsKey(uuid))
                getUser(uuid).removePerm(permission);

        } else {
            perms.add(permission);
            configUsers.set(uuid.toString() + ".permissions", perms);
            if (this.players.containsKey(uuid))
                getUser(uuid).addPerm(permission);
        }
        try {
            configUsers.save(this.configFileUsers);
        } catch (Exception ignored) {
        }
    }

    public void updateUserSubrank(UUID uuid, String name, boolean remove) {
        if (name.equals(""))
            return;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(this.configFileUsers);
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
            configUsers.save(this.configFileUsers);
        } catch (Exception ignored) {
        }
        if (this.players.containsKey(uuid))
            getUser(uuid).refreshPerms();
    }
}