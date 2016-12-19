package com.crossge.necessities.Economy;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

public class Economy { //TODO config option for format money as well as refresh baltop after every so many updates happen so that it can be async and then quicker
    private final HashMap<UUID, Double> loadedBals = new HashMap<>();
    private ArrayList<String> balTop = new ArrayList<>();
    private Properties properties;
    private boolean regenBalTop = true;
    private String dbURL;

    public void init() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Loading Economy...");
        YamlConfiguration config = Necessities.getInstance().getConfig();
        this.dbURL = "jdbc:mysql://" + config.getString("Lavasurvival.DBHost") + "/" + config.getString("Lavasurvival.DBTable");
        this.properties = new Properties();
        this.properties.setProperty("user", config.getString("Lavasurvival.DBUser"));
        this.properties.setProperty("password", config.getString("Lavasurvival.DBPassword"));
        this.properties.setProperty("useSSL", "false");
        this.properties.setProperty("autoReconnect", "true");
        Bukkit.getOnlinePlayers().forEach(p -> loadAccount(p.getUniqueId()));
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Economy loaded.");
    }

    public double getBalance(UUID uuid) {
        if (this.loadedBals.containsKey(uuid))
            return this.loadedBals.get(uuid);
        double bal = 0.0;
        try {
            Connection conn = DriverManager.getConnection(this.dbURL, this.properties);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT balance FROM users WHERE uuid = '" + uuid + "'");
            if (rs.next())
                bal = rs.getDouble("balance");
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ignored) {
        }
        return bal;
    }

    public void loadAccount(UUID uuid) { //Loads it into memory for temporary faster lookup
        if (!this.loadedBals.containsKey(uuid)) //Should possibly handle if the account does not exist but for now that is not going to happen
            try {
                Connection conn = DriverManager.getConnection(this.dbURL, this.properties);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT balance FROM users WHERE uuid = '" + uuid + "'");
                if (rs.next())
                    this.loadedBals.put(uuid, rs.getDouble("balance"));
                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception ignored) {
            }
    }

    public void unloadAccount(UUID uuid) {
        this.loadedBals.remove(uuid);
    }

    public void withdraw(UUID uuid, double amount) {
        if (this.loadedBals.containsKey(uuid))
            this.loadedBals.put(uuid, this.loadedBals.get(uuid) - amount);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(dbURL, properties);
                    Statement stmt = conn.createStatement();
                    stmt.execute("UPDATE users SET balance = balance - " + amount);
                    stmt.close();
                    conn.close();
                    regenBalTop = true;
                } catch (Exception ignored) {
                }
            }
        }.runTaskAsynchronously(Necessities.getInstance());
    }

    public void deposit(UUID uuid, double amount) {
        if (this.loadedBals.containsKey(uuid))
            this.loadedBals.put(uuid, this.loadedBals.get(uuid) + amount);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(dbURL, properties);
                    Statement stmt = conn.createStatement();
                    stmt.execute("UPDATE users SET balance = balance + " + amount);
                    stmt.close();
                    conn.close();
                    regenBalTop = true;
                } catch (Exception ignored) {
                }
            }
        }.runTaskAsynchronously(Necessities.getInstance());
    }

    public void setBalance(UUID uuid, double amount) {
        if (this.loadedBals.containsKey(uuid))
            this.loadedBals.put(uuid, amount);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Connection conn = DriverManager.getConnection(dbURL, properties);
                    Statement stmt = conn.createStatement();
                    stmt.execute("UPDATE users SET balance = " + amount);
                    stmt.close();
                    conn.close();
                    regenBalTop = true;
                } catch (Exception ignored) {
                }
            }
        }.runTaskAsynchronously(Necessities.getInstance());
    }

    private void updateBalTop() { //Should this just be cached and only update the cache if things change
        if (!this.regenBalTop)
            return;
        this.regenBalTop = false;
        this.balTop = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(this.dbURL, this.properties);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT uuid,balance FROM users ORDER BY balance DESC");
            while (rs.next())
                this.balTop.add(rs.getString("uuid") + " " + rs.getDouble("balance"));
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ignored) {
        }
    }

    public String balTop(int page, int time) {
        page *= 10;
        return (this.balTop.size() < time + page + 1 || time == 10) ? null : this.balTop.get(page + time);
    }

    public int baltopPages() {
        updateBalTop(); //make sure the bal top is up to date
        return this.balTop.size() % 10 != 0 ? (this.balTop.size() / 10) + 1 : (this.balTop.size() / 10);
    }

    public String format(double balance) {
        return Utils.formatMoney(balance) + " GGs";
    }
}