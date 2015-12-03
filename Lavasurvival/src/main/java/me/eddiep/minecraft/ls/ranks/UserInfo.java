package me.eddiep.minecraft.ls.ranks;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.system.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.UUID;

public class UserInfo {
    private ArrayList<MaterialData> ownedBlocks = new ArrayList<>();
    private boolean inWater = false;
    private long lastBreak = System.currentTimeMillis();
    private int taskID = 0;
    private Player bukkitPlayer;
    private UUID userUUID;
    private long blockChangeCount;

    public UserInfo(Player p) {
        this.bukkitPlayer = p;
        this.userUUID = p.getUniqueId();
    }

    public UserInfo(UUID uuid) {
        this.userUUID = uuid;
        load();
    }

    public long getBlockChangeCount() {
        return blockChangeCount;
    }

    public void resetBlockChangeCount() {
        blockChangeCount = 0;
    }

    public void load() {
        //TODO Load all persistent data
    }

    public void save() {
        //TODO Save all persistent data
    }

    public void logOut() {
        this.bukkitPlayer = null;
        save();
    }

    public long getLastBreak() {
        return this.lastBreak;
    }

    public void setLastBreak(long lastBreak) {
        this.lastBreak = lastBreak;
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
            Lavasurvival.INSTANCE.withdrawAndUpdate(getPlayer(), price);
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
            Lavasurvival.INSTANCE.withdrawAndUpdate(getPlayer(), price);
            getPlayer().sendMessage(ChatColor.GREEN + "You bought the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + "!");
        }
    }

    public void incrimentBlockCount() {
        blockChangeCount++;
    }
}