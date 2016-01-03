package com.crossge.necessities;

import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class DonationReader {
    RankManager rm = new RankManager();
    UserManager um = new UserManager();
    Variables var = new Variables();
    GetUUID get = new GetUUID();
    private String pass;
    private BukkitRunnable current;

    private void check() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://egservers.net:3306/donation", "donation", this.pass);
            Statement stmt = conn.createStatement();
            Statement stmt2 = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM actions");
            ResultSet rs2 = stmt2.executeQuery("SELECT * FROM players");
            while (rs.next()) {
                if (Integer.parseInt(rs.getString("server").replaceAll("[^0-9]", "")) == 9 && rs.getInt("delivered") == 0) {
                    long steamID = rs.getLong("uid");
                    UUID uuid = null;
                    while (rs2.next()) {
                        if (rs2.getLong("uid") == steamID) {
                            uuid = UUID.fromString(rs2.getString("uuid").replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                            break;
                        }
                    }
                    if (uuid != null && get.hasJoined(uuid)) {
                        User u = um.getUser(uuid);
                        String subrank = "Necessities.Donator" + rs.getInt("package");
                        if (rm.validSubrank(subrank.toLowerCase()))
                            continue;
                        um.updateUserSubrank(uuid, rm.getSub(subrank), false);
                        Bukkit.broadcastMessage(u.getDispName() + var.getMessages() + " just donated.");
                        PreparedStatement stmt3 = conn.prepareStatement("UPDATE actions SET delivered =" + 1 + " WHERE id = " + rs.getInt("id"));
                        stmt3.executeUpdate();
                        stmt3.close();
                    }
                }
            }
            rs.close();
            rs2.close();
            stmt.close();
            stmt2.close();
            conn.close();
        } catch (Exception e) { }
    }

    public void init() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Connecting to Donator Database.");
        this.current = new BukkitRunnable() {
            @Override
            public void run() {
                check();
            }
        };
        File configFile = new File("plugins/Necessities", "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        this.pass = config.getString("Necessities.DonationPass");
        this.current.runTaskTimerAsynchronously(Necessities.getInstance(), 0, 20 * 60);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Connected to Donator Database.");
    }

    public void disconnect() {
        if (this.current != null)
            this.current.cancel();
    }
}