package me.eddiep.ranks;

import me.eddiep.Lavasurvival;
import me.eddiep.game.Gamemode;
import me.eddiep.system.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
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
    private boolean inWater = false;
    private int taskID = 0;
    private Player bukkitPlayer;
    private UUID userUUID;
    private Rank rank;

    public UserInfo(Player p) {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(configFileUsers);
        this.bukkitPlayer = p;
        this.userUUID = p.getUniqueId();
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
        if (getPlayer() == null)
            return this.userUUID;
        return getPlayer().getUniqueId();
    }

    public String getName() {
        if (getPlayer() == null)
            return Bukkit.getOfflinePlayer(getUUID()).getName();
        return getPlayer().getName();
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
        this.attachment = getPlayer().addAttachment(Lavasurvival.INSTANCE);
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
        if (getPlayer() != null)
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

    public boolean isInWater() {
        return this.inWater;
    }

    public void setInWater(boolean value) {
        this.inWater = value;
        if(!isInWater())
            Bukkit.getScheduler().cancelTask(this.taskID);
        if(value && getPlayer() != null && Gamemode.getCurrentGame() != null && Gamemode.WATER_DAMAGE != 0 && Gamemode.getCurrentGame().isAlive(getPlayer()))
            this.taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                @Override
                public void run() {
                    if (isInWater() && getPlayer() != null) {
                        getPlayer().damage(Gamemode.WATER_DAMAGE);
                        setInWater(getPlayer().getLocation().getBlock().getType().equals(Material.WATER) || getPlayer().getLocation().getBlock().getType().equals(Material.STATIONARY_WATER) ||
                                getPlayer().getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.WATER) ||
                                getPlayer().getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.STATIONARY_WATER));
                    }
                }
            }, (int) (20 * Gamemode.DAMAGE_FREQUENCY));
    }

    public Player getPlayer() {
        return this.bukkitPlayer;
    }

    public void setPlayer(Player p) {
        this.bukkitPlayer = p;
    }

    private void addBlock(Material type) {
        MaterialData dat = new MaterialData(type);
        if (!this.ownedBlocks.contains(dat)) {
            this.ownedBlocks.add(dat);
            if(getPlayer() != null)
                getPlayer().getInventory().addItem(dat.toItemStack(1));
        }
    }

    public void giveBoughtBlocks() {
        if(getPlayer() != null)
            for(MaterialData dat : this.ownedBlocks)
                getPlayer().getInventory().addItem(dat.toItemStack(1));
    }

    private void addBlock(Material type, byte data) {
        MaterialData dat = new MaterialData(type, data);
        if (!this.ownedBlocks.contains(dat)) {
            this.ownedBlocks.add(dat);
            if(getPlayer() != null)
                getPlayer().getInventory().addItem(dat.toItemStack(1));
        }
    }

    public void clearBlocks() {
        if(getPlayer() != null)
            for(MaterialData dat : this.ownedBlocks)
                getPlayer().getInventory().remove(dat.toItemStack());
        this.ownedBlocks.clear();
    }

    public boolean ownsBlock(Material type) {
        return this.ownedBlocks.contains(new MaterialData(type));
    }

    public boolean ownsBlock(Material type, byte data) {
        return this.ownedBlocks.contains(new MaterialData(type, data));
    }

    public void buyBlock(Material mat, double price, byte data) {
        if (getPlayer() == null)
            return;
        if (ownsBlock(mat, data))
            getPlayer().sendMessage(ChatColor.RED + "You already own that block..");
        else if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(getPlayer()) || Lavasurvival.INSTANCE.getEconomy().getBalance(getPlayer()) < price) {
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough money to buy the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + " with datavalue " + data + "..");
        } else if (BukkitUtils.isInventoryFull(getPlayer().getInventory()))
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough inventory space to buy any more blocks..");
        else {
            addBlock(mat, data);
            getPlayer().sendMessage(ChatColor.GREEN + "You bought the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + " with datavalue " + data + "!");
        }
    }

    public void buyBlock(Material mat, double price) {
        if (getPlayer() == null)
            return;
        if (ownsBlock(mat))
            getPlayer().sendMessage(ChatColor.RED + "You already own that block..");
        else if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(getPlayer()) || Lavasurvival.INSTANCE.getEconomy().getBalance(getPlayer()) < price) {
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough money to buy the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + "..");
        } else if (BukkitUtils.isInventoryFull(getPlayer().getInventory()))
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough inventory space to buy any more blocks..");
        else {
            addBlock(mat);
            getPlayer().sendMessage(ChatColor.GREEN + "You bought the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + "!");
        }
    }
}