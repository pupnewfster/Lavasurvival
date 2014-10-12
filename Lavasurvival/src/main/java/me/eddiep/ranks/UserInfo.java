package me.eddiep.ranks;

import me.eddiep.Lavasurvival;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class UserInfo {
    private File configFileUsers = new File(Lavasurvival.INSTANCE.getDataFolder(), "userinfo.yml");
    private ArrayList<MaterialData> ownedBlocks = new ArrayList<MaterialData>();
    private PermissionAttachment attachment;
    private Player bukkitPlayer;
    private UUID userUUID;
    private Rank rank;

    public UserInfo(Player p) {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        this.bukkitPlayer = p;
        this.userUUID = this.bukkitPlayer.getUniqueId();
        if (configUsers.contains(getUUID().toString() + ".rank"))
            this.rank = Lavasurvival.INSTANCE.getRankManager().getRank(configUsers.getString(getUUID().toString() + ".rank"));
        givePerms();
    }

    public UserInfo(UUID uuid) {
        this.userUUID = uuid;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        if (configUsers.contains(getUUID().toString() + ".rank"))
            this.rank = Lavasurvival.INSTANCE.getRankManager().getRank(configUsers.getString(getUUID().toString() + ".rank"));
    }

    public void logOut() {
        this.bukkitPlayer = null;
    }

    public UUID getUUID() {
        if (this.bukkitPlayer == null)
            return this.userUUID;
        return this.bukkitPlayer.getUniqueId();
    }

    public String getName() {
        if (this.bukkitPlayer == null)
            return Bukkit.getOfflinePlayer(this.userUUID).getName();
        return this.bukkitPlayer.getName();
    }

    public Rank getRank() {
        return this.rank;
    }

    public void setRank(Rank r) {
        this.rank = r;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        configUsers.set(getUUID().toString() + ".rank", r.getName());
        try {
            configUsers.save(configFileUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPerms();
    }

    public void givePerms() {
        this.attachment = this.bukkitPlayer.addAttachment(Lavasurvival.INSTANCE);
        for (String node : this.rank.getNodes())
            setPerm(node);
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
        removePerms();
        givePerms();
    }

    public void removePerms() {
        for (String p : this.attachment.getPermissions().keySet())
            this.attachment.unsetPermission(p);
    }

    public Player getPlayer() {
        return this.bukkitPlayer;
    }

    public void setPlayer(Player p) {
        this.bukkitPlayer = p;
    }

    public void addBlock(Material type) {
        MaterialData dat = new MaterialData(type);
        if (!this.ownedBlocks.contains(dat))
            this.ownedBlocks.add(dat);
    }

    public void addBlock(Material type, byte data) {
        MaterialData dat = new MaterialData(type, data);
        if (!this.ownedBlocks.contains(dat))
            this.ownedBlocks.add(dat);
    }

    public void clearBlocks() {
        this.ownedBlocks.clear();
    }

    public boolean ownsBlock(Material type) {
        return this.ownedBlocks.contains(new MaterialData(type));
    }

    public boolean ownsBlock(Material type, byte data) {
        return this.ownedBlocks.contains(new MaterialData(type, data));
    }

    public void buyBlock(Material mat, double price, byte data) {
        if (this.bukkitPlayer == null)
            return;
        if (ownsBlock(mat, data))
            getPlayer().sendMessage(ChatColor.RED + "You already own that block..");
        else if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(getPlayer()) || Lavasurvival.INSTANCE.getEconomy().getBalance(getPlayer()) < price) {
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough money to buy the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + " with datavalue " + data + "..");
        } else {
            addBlock(mat, data);
            getPlayer().sendMessage(ChatColor.GREEN + "You bought the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + " with datavalue " + data + "!");
        }
    }

    public void buyBlock(Material mat, double price) {
        if (this.bukkitPlayer == null)
            return;
        if (ownsBlock(mat))
            getPlayer().sendMessage(ChatColor.RED + "You already own that block..");
        else if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(getPlayer()) || Lavasurvival.INSTANCE.getEconomy().getBalance(getPlayer()) < price) {
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough money to buy the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + "..");
        } else {
            addBlock(mat);
            getPlayer().sendMessage(ChatColor.GREEN + "You bought the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + "!");
        }
    }
}