package me.eddiep.minecraft.ls.ranks;

import me.eddiep.minecraft.ls.Lavasurvival;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    private final HashMap<UUID, UserInfo> players = new HashMap<>();

    public UserManager() {
        Bukkit.getOnlinePlayers().forEach(this::parseUser);
    }

    private void parseUser(final Player p) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> {
            if (players.containsKey(p.getUniqueId())) //This should never be true
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

    public UserInfo getUser(UUID uuid) {
        return !players.containsKey(uuid) ? new UserInfo(uuid) : players.get(uuid);
    }

    public void saveAll() {
        players.values().forEach(UserInfo::saveBank);
    }

    public void addUser(Player p) {
        final UUID uuid = p.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Class.forName("org.mariadb.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(Lavasurvival.INSTANCE.getDBURL(), Lavasurvival.INSTANCE.getDBUser(), Lavasurvival.INSTANCE.getDBPass());
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE uuid = \"" + uuid + "\"");
                    if (!rs.next())
                        stmt.execute("INSERT INTO users (uuid, matches, ownedBlocks, bank, addToBank) VALUES (\"" + uuid + "\", \"\", \"\", \"\", \"\")");
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (Exception ignored) {
                }
            }
        }.runTaskAsynchronously(Lavasurvival.INSTANCE);
    }
}