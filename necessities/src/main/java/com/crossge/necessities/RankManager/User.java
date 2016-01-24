package com.crossge.necessities.RankManager;

import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.Hats.Hat;
import com.crossge.necessities.Hats.HatType;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.ScoreBoards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class User {
    private File configFileSubranks = new File("plugins/Necessities/RankManager", "subranks.yml"), configFileUsers = new File("plugins/Necessities/RankManager", "users.yml");
    private ArrayList<String> permissions = new ArrayList<>(), subranks = new ArrayList<>();
    private String appended = "", nick = null, lastContact, status = "dead";
    private boolean opChat = false, muted = false, slackChat = false;
    private ArrayList<UUID> ignored = new ArrayList<>();
    private long login = 0, lastRequest = 0;
    private PermissionAttachment attachment;
    private Location right, left;
    private Player bukkitPlayer;
    private int pastTotal = 0;
    private Hat hat = null;
    private UUID userUUID;
    private Rank rank;

    public User(Player p) {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        RankManager rm = new RankManager();
        this.bukkitPlayer = p;
        this.right = p.getLocation();
        this.left = p.getLocation();
        this.userUUID = this.bukkitPlayer.getUniqueId();
        if (configUsers.contains(getUUID().toString() + ".rank"))
            this.rank = rm.getRank(configUsers.getString(getUUID().toString() + ".rank"));
        if (configUsers.contains(getUUID().toString() + ".nick"))
            this.nick = ChatColor.translateAlternateColorCodes('&', configUsers.getString(getUUID().toString() + ".nick"));
        if (this.nick != null && !this.nick.startsWith("~"))
            this.nick = "~" + this.nick;
        if (configUsers.contains(getUUID().toString() + ".muted"))
            this.muted = configUsers.getBoolean(getUUID().toString() + ".muted");
        if (configUsers.contains(getUUID().toString() + ".timePlayed"))
            this.pastTotal = configUsers.getInt(getUUID().toString() + ".timePlayed");
        if (configUsers.contains(getUUID().toString() + ".hat"))
            this.hat = Hat.fromType(HatType.fromString(configUsers.getString(getUUID().toString() + ".hat")), this.bukkitPlayer.getLocation());
        this.login = System.currentTimeMillis();
        readIgnored();
        updateListName();
        Necessities.getInstance().updateAll(this.bukkitPlayer);
        CmdHide hide = new CmdHide();
        if (hide.isHidden(this.bukkitPlayer)) {
            this.opChat = true;
            hide.hidePlayer(this.bukkitPlayer);
        }
    }

    public User(UUID uuid) {
        this.userUUID = uuid;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        RankManager rm = new RankManager();
        if (configUsers.contains(getUUID().toString() + ".rank"))
            this.rank = rm.getRank(configUsers.getString(getUUID().toString() + ".rank"));
        for (String subrank : configUsers.getStringList(uuid + ".subranks"))
            if (!subrank.equals(""))
                this.subranks.add(subrank);
        for (String node : configUsers.getStringList(uuid + ".permissions"))
            if (!node.equals(""))
                this.permissions.add(node);
        if (configUsers.contains(getUUID().toString() + ".nick"))
            this.nick = ChatColor.translateAlternateColorCodes('&', configUsers.getString(getUUID().toString() + ".nick"));
        if (this.nick != null && !this.nick.startsWith("~"))
            this.nick = "~" + this.nick;
        if (configUsers.contains(getUUID().toString() + ".timePlayed"))
            this.pastTotal = configUsers.getInt(getUUID().toString() + ".timePlayed");
        readIgnored();
    }

    public void updateTimePlayed() {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        if (this.login != 0) {
            configUsers.set(getUUID().toString() + ".timePlayed", (int) (this.pastTotal + (System.currentTimeMillis() - this.login) / 1000));
            try {
                configUsers.save(configFileUsers);
            } catch (Exception e) {
            }
        }
        this.pastTotal = 0;
        this.login = 0;
    }

    public void logOut() {
        updateTimePlayed();
        ScoreBoards sb = new ScoreBoards();
        sb.delPlayer(this);
        if (this.hat != null)
            this.hat.despawn();
        this.bukkitPlayer = null;
    }

    private void readIgnored() {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        if (!configUsers.contains(getUUID().toString()))
            return;
        if (configUsers.contains(getUUID().toString() + ".ignored")) {
            for (String name : configUsers.getStringList(getUUID().toString() + ".ignored"))
                if (!name.equals(""))
                    this.ignored.add(UUID.fromString(name));
        } else {
            configUsers.set(getUUID().toString() + ".ignored", Arrays.asList(""));
            try {
                configUsers.save(configFileUsers);
            } catch (Exception e) {
            }
        }
    }

    public boolean isIgnoring(UUID uuid) {
        return this.ignored.contains(uuid);
    }

    public void ignore(UUID uuid) {
        if (!this.ignored.contains(uuid)) {//this should already be checked but whatevs
            this.ignored.add(uuid);
            YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
            if (!configUsers.contains(getUUID().toString()))
                return;
            List<String> ign = configUsers.getStringList(uuid.toString() + ".ignored");
            if (ign.contains(""))
                ign.remove("");
            ign.add(uuid.toString());
            configUsers.set(uuid.toString() + ".ignored", ign);
            try {
                configUsers.save(configFileUsers);
            } catch (Exception e) {
            }
        }
    }

    public void unignore(UUID uuid) {
        if (this.ignored.contains(uuid)) {//this should already be checked but whatevs
            this.ignored.remove(uuid);
            YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
            if (!configUsers.contains(getUUID().toString()))
                return;
            List<String> ign = configUsers.getStringList(uuid.toString() + ".ignored");
            ign.remove(uuid.toString());
            if (ign.isEmpty())
                ign.add("");
            configUsers.set(uuid.toString() + ".ignored", ign);
            try {
                configUsers.save(configFileUsers);
            } catch (Exception e) {
            }
        }
    }

    public String getAppended() {
        return this.appended;
    }

    public void setAppended(String toAppend) {
        this.appended = toAppend;
    }

    public UUID getUUID() {
        return this.bukkitPlayer == null ? this.userUUID : this.bukkitPlayer.getUniqueId();
    }

    public String getName() {
        return this.bukkitPlayer == null ? Bukkit.getOfflinePlayer(this.userUUID).getName() : this.bukkitPlayer.getName();
    }

    public String getDispName() {
        return this.bukkitPlayer == null ? Bukkit.getOfflinePlayer(this.userUUID).getName() : ChatColor.translateAlternateColorCodes('&', getRank().getTitle() + this.bukkitPlayer.getDisplayName());
    }

    public long getLastRequest() {
        return this.lastRequest == 0 ? System.currentTimeMillis() : this.lastRequest;
    }

    public void setLastRequest(long time) {
        this.lastRequest = time;
    }

    public Rank getRank() {
        return this.rank;
    }

    public void setRank(Rank r) {
        this.rank = r;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        if (!configUsers.contains(getUUID().toString()))
            return;
        configUsers.set(getUUID().toString() + ".rank", r.getName());
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
        }
        refreshPerms();
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String message) {
        this.nick = message != null ? ChatColor.translateAlternateColorCodes('&', message) : null;
        updateListName();
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        if (!configUsers.contains(getUUID().toString()))
            return;
        configUsers.set(getUUID().toString() + ".nick", message);
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public Hat getHat() {
        return this.hat;
    }

    public void respawnHat() {
        if (this.hat == null || this.bukkitPlayer == null)
            return;
        this.hat.despawn();
        this.hat = Hat.fromType(this.hat.getType(), this.bukkitPlayer.getLocation());
    }

    public void setHat(Hat hat) {
        if (this.hat != null)
            this.hat.despawn();
        this.hat = hat;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        if (!configUsers.contains(getUUID().toString()))
            return;
        configUsers.set(getUUID().toString() + ".hat", this.hat == null ? null : this.hat.getType().getName());
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
        }
    }

    public void updateListName() {
        if (this.bukkitPlayer == null)
            return;
        this.bukkitPlayer.setDisplayName(getRank().getColor() + (this.nick == null ? this.bukkitPlayer.getName() : this.nick));
        Necessities.getInstance().updateName(this.bukkitPlayer);
    }

    public boolean isMuted() {
        return this.muted;
    }

    public void setMuted(boolean tomute) {
        this.muted = tomute;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        if (!configUsers.contains(getUUID().toString()))
            return;
        configUsers.set(getUUID().toString() + ".muted", this.muted);
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
        }
    }

    public String getLastC() {
        return this.lastContact;
    }

    public void setLastC(String last) {
        this.lastContact = last;
    }

    public void givePerms() {
        ScoreBoards sb = new ScoreBoards();
        sb.addPlayer(this);
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        YamlConfiguration configSubranks = YamlConfiguration.loadConfiguration(configFileSubranks);
        this.attachment = this.bukkitPlayer.addAttachment(Necessities.getInstance());
        for (String node : this.rank.getNodes())
            setPerm(node);
        for (String subrank : configUsers.getStringList(getUUID().toString() + ".subranks")) {
            if (!subrank.equals("") && configSubranks.contains(subrank)) {
                this.subranks.add(subrank);
                for (String node : configSubranks.getStringList(subrank))
                    setPerm(node);
            }
        }
        for (String node : configUsers.getStringList(getUUID().toString() + ".permissions")) {
            if (!node.equals(""))
                this.permissions.add(node);
            setPerm(node);
        }
    }

    private void setPerm(String node) {
        if (node.equals("") || node.equals("-*"))
            return;
        if (node.startsWith("-"))
            this.attachment.setPermission(node.replaceFirst("-", ""), false);
        else
            this.attachment.setPermission(node, true);
    }

    public void updateRank(Rank r) {
        this.rank = r;
        if (this.bukkitPlayer != null)
            refreshPerms();
    }

    public void refreshPerms() {
        this.subranks.clear();
        this.permissions.clear();
        removePerms();
        givePerms();
        updateListName();
    }

    public void removePerms() {
        for (String p : this.attachment.getPermissions().keySet())
            this.attachment.unsetPermission(p);
    }

    public String getSubranks() {
        String sublist = "";
        if (this.subranks.isEmpty())
            return null;
        for (String sub : this.subranks)
            sublist += sub + ", ";
        return sublist.trim().substring(0, sublist.length() - 2);
    }

    public String getPermissions() {
        String permlist = "";
        if (this.permissions.isEmpty())
            return null;
        for (String perm : this.permissions)
            permlist += perm + ", ";
        return permlist.trim().substring(0, permlist.length() - 2);
    }

    public void addPerm(String permission) {
        setPerm(permission);
    }

    public void removePerm(String permission) {
        this.attachment.unsetPermission(permission);
        if (this.permissions.contains(permission))
            this.permissions.remove(permission);
    }

    public Player getPlayer() {
        return this.bukkitPlayer;
    }

    public Location getLeft() {
        return this.left;
    }

    public void setLeft(Location l) {
        this.left = l;
    }

    public Location getRight() {
        return this.right;
    }

    public void setRight(Location l) {
        this.right = l;
    }

    public boolean opChat() {
        return this.opChat;
    }

    public void toggleOpChat() {
        this.opChat = !this.opChat;
    }

    public boolean slackChat() {
        return this.slackChat;
    }

    public void toggleSlackChat() {
        this.slackChat = !this.slackChat;
    }

    public String getTimePlayed() {
        long seconds = this.pastTotal;
        if (this.login != 0)
            seconds += (System.currentTimeMillis() - this.login) / 1000;
        long first = seconds % 31536000;
        long second = first % 2592000;
        long third = second % 604800;
        long fourth = third % 86400;
        long fifth = fourth % 3600;
        int years = (int) (seconds / 31536000);
        int months = (int) (first / 2592000);
        int weeks = (int) (second / 604800);
        int days = (int) (third / 86400);
        int hours = (int) (fourth / 3600);
        int min = (int) (fifth / 60);
        int sec = (int) (fifth % 60);
        String time = "";
        if (years != 0)
            time = Integer.toString(years) + " year" + plural(years) + " ";
        if (months != 0)
            time += Integer.toString(months) + " month" + plural(months) + " ";
        if (weeks != 0)
            time += Integer.toString(weeks) + " week" + plural(weeks) + " ";
        if (days != 0)
            time += Integer.toString(days) + " day" + plural(days) + " ";
        if (hours != 0)
            time += Integer.toString(hours) + " hour" + plural(hours) + " ";
        if (min != 0)
            time += Integer.toString(min) + " minute" + plural(min) + " ";
        if (sec != 0)
            time += Integer.toString(sec) + " second" + plural(sec) + " ";
        time = time.trim();
        if (time.equals(""))
            time = "This player has not spent any time on our server";
        return time + ".";
    }

    private String plural(int times) {
        if (times == 1)
            return "";
        return "s";
    }
}