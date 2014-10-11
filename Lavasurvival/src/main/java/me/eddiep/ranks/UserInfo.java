package me.eddiep.ranks;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import me.eddiep.Lavasurvival;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class UserInfo {
    private File configFileUsers = new File(Lavasurvival.INSTANCE.getDataFolder(), "userinfo.yml");
    private ArrayList<Material> ownedBlocks = new ArrayList<Material>();
    private ArrayList<String> permissions = new ArrayList<String>();
    private PermissionAttachment attachment;
    private Player bukkitPlayer;
    private UUID userUUID;
    private Rank rank;

    public UserInfo(Player p) {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        this.bukkitPlayer = p;
        this.userUUID = this.bukkitPlayer.getUniqueId();
        if(configUsers.contains(getUUID().toString() + ".rank"))
            this.rank = Lavasurvival.INSTANCE.getRankManager().getRank(configUsers.getString(getUUID().toString() + ".rank"));
        givePerms();
    }

    public UserInfo(UUID uuid) {
        this.userUUID = uuid;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        if(configUsers.contains(getUUID().toString() + ".rank"))
            this.rank = Lavasurvival.INSTANCE.getRankManager().getRank(configUsers.getString(getUUID().toString() + ".rank"));
    }

    public void logOut() {
        this.bukkitPlayer = null;
    }

    public UUID getUUID() {
        if(this.bukkitPlayer == null)
            return this.userUUID;
        return this.bukkitPlayer.getUniqueId();
    }

    public String getName() {
        if(this.bukkitPlayer == null)
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
        } catch (Exception e){}
        refreshPerms();
    }

    public void givePerms() {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        this.attachment = this.bukkitPlayer.addAttachment(Lavasurvival.INSTANCE);
        for(String node : this.rank.getNodes())
            setPerm(node);
    }

    private void setPerm(String node) {
        if(node.equals("") || node.equals("-*"))
            return;
        if(node.startsWith("-"))
            this.attachment.setPermission(node.replaceFirst("-", ""), false);
        else
            this.attachment.setPermission(node, true);
    }

    public void updateRank(Rank r) {
        this.rank = r;
        if(this.bukkitPlayer != null)
            refreshPerms();
    }

    public void refreshPerms() {
        this.permissions.clear();
        removePerms();
        givePerms();
    }

    public void setPlayer(Player p) {
        this.bukkitPlayer = p;
    }

    public void removePerms() {
        for (String p : this.attachment.getPermissions().keySet())
            this.attachment.unsetPermission(p);
    }

    public Player getPlayer() {
        return this.bukkitPlayer;
    }

    public void addBlock(Material type) {
        if(!this.ownedBlocks.contains(type))
            this.ownedBlocks.add(type);
    }

    public void clearBlocks() {
        this.ownedBlocks.clear();
    }

    public boolean ownsBlock(Material type) {
        return this.ownedBlocks.contains(type);
    }
}