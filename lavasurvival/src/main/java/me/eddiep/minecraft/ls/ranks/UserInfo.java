package me.eddiep.minecraft.ls.ranks;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.items.LavaItem;
import me.eddiep.minecraft.ls.game.status.PlayerStatusManager;
import me.eddiep.minecraft.ls.system.BukkitUtils;
import me.eddiep.minecraft.ls.system.PhysicsListener;
import me.eddiep.minecraft.ls.system.bank.BankInventory;
import net.minecraft.server.v1_11_R1.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UserInfo {
    private final ArrayList<MaterialData> ownedBlocks = new ArrayList<>();
    private long lastBreak = System.currentTimeMillis(), blockChangeCount;
    private boolean inWater = false;
    private Player bukkitPlayer;
    private int taskID = 0;
    private final UUID userUUID;
    private List<MaterialData> BANK = new ArrayList<>();
    private boolean generosity;

    public UserInfo(Player p) {
        this.bukkitPlayer = p;
        this.userUUID = p.getUniqueId();
        load();
    }

    public UserInfo(UUID uuid) {
        this.userUUID = uuid;
        load();
    }

    @SuppressWarnings("unused")
    public long getBlockChangeCount() {
        return this.blockChangeCount;
    }

    @SuppressWarnings("unused")
    public void resetBlockChangeCount() {
        this.blockChangeCount = 0;
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private void load() {
        if (this.ownedBlocks.isEmpty()) { //TODO see if this will end up ever getting called when it is not empty
            this.BANK.clear(); //Make sure the bank is empty
            new BukkitRunnable() {
                @Override
                public void run() {
                    String curOwned = "", curBank = "";
                    try {
                        Connection conn = DriverManager.getConnection(Lavasurvival.INSTANCE.getDBURL(), Lavasurvival.INSTANCE.getDBProperties());
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE uuid = '" + UserInfo.this.userUUID + "'");
                        if (rs.next()) {
                            curOwned = rs.getString("ownedBlocks");
                            curBank = rs.getString("bank");
                            String toAdd = rs.getString("addToBank");
                            if (!toAdd.equals("")) {
                                if (curBank.equals(""))
                                    curBank = toAdd;
                                else
                                    curBank += "|" + toAdd;
                                stmt.execute("UPDATE users SET addToBank = '', bank = '" + curBank + "' WHERE uuid = '" + UserInfo.this.userUUID + "'");
                            }
                        }
                        rs.close();
                        stmt.close();
                        conn.close();
                    } catch (Exception ignored) {
                    }
                    if (!curOwned.equals("")) {
                        for (String key : curOwned.split("\\|"))
                            if (!key.equals("") && key.split(":").length == 2) { //Key should never be an empty string unless something broke
                                String name = key.split(":")[0];
                                byte damage = (byte) Integer.parseInt(key.split(":")[1]);
                                UserInfo.this.ownedBlocks.add(new MaterialData(Material.valueOf(name), damage));
                            }
                    }
                    if (!curBank.equals(""))
                        for (String key : curBank.split("\\|"))
                            if (key.split(":").length == 2)
                                UserInfo.this.BANK.add(new MaterialData(Material.valueOf(key.split(":")[0]), (byte) Integer.parseInt(key.split(":")[1])));
                }
            }.runTaskAsynchronously(Lavasurvival.INSTANCE);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public Inventory createBankInventory(Player p) {
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                String toAdd = "";
                try {
                    Connection conn = DriverManager.getConnection(Lavasurvival.INSTANCE.getDBURL(), Lavasurvival.INSTANCE.getDBProperties());
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE uuid = '" + UserInfo.this.userUUID + "'");
                    if (rs.next()) {
                        toAdd = rs.getString("addToBank");
                        if (!toAdd.equals("")) {
                            String current = rs.getString("bank");
                            if (current.equals(""))
                                current = toAdd;
                            else
                                current += "|" + toAdd;
                            stmt.execute("UPDATE users SET addToBank = '', bank = '" + current + "' WHERE uuid = '" + UserInfo.this.userUUID + "'");
                        }
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (Exception ignored) {
                }
                if (!toAdd.equals(""))
                    for (String key : toAdd.split("\\|"))
                        if (key.split(":").length == 2)
                            UserInfo.this.BANK.add(new MaterialData(Material.valueOf(key.split(":")[0]), (byte) Integer.parseInt(key.split(":")[1])));
            }
        }.runTaskAsynchronously(Lavasurvival.INSTANCE);
        return BankInventory.create(p, this.BANK).openFor(p);
    }

    @SuppressWarnings("deprecation")
    public void saveBank() {
        ArrayList<MaterialData> cBank = new ArrayList<>();
        for (MaterialData e : this.BANK)
            if (!e.getItemType().equals(Material.AIR))
                cBank.add(e);
        this.BANK = cBank;
        String banked = "";
        boolean empty = true;
        for (MaterialData e : this.BANK)
            if (!e.getItemType().equals(Material.AIR)) {
                banked += (empty ? "" : "|") + e.getItemType() + ":" + e.getData();
                empty = false;
            }
        String finalBanked = banked;
        try {
            new BukkitRunnable() {
                @Override
                public void run() {
                    saveBank(finalBanked);
                }
            }.runTaskAsynchronously(Lavasurvival.INSTANCE);
        } catch (Exception e) {
            saveBank(finalBanked);
        }
    }

    private void saveBank(String banked) {
        try {
            Connection conn = DriverManager.getConnection(Lavasurvival.INSTANCE.getDBURL(), Lavasurvival.INSTANCE.getDBProperties());
            Statement stmt = conn.createStatement();
            stmt.execute("UPDATE users SET bank = '" + banked + "' WHERE uuid = '" + this.userUUID + "'");
            stmt.close();
            conn.close();
        } catch (Exception ignored) {
        }
    }

    public void logOut() {
        this.bukkitPlayer = null;
    }

    public long getLastBreak() {
        return this.lastBreak;
    }

    public void setLastBreak(long lastBreak) {
        this.lastBreak = lastBreak;
    }

    public boolean isInWater() {
        return this.inWater;
    }

    @SuppressWarnings("ConstantConditions")
    public void setInWater(boolean value) {
        this.inWater = value;
        if (!isInWater())
            Bukkit.getScheduler().cancelTask(this.taskID);
        if (value && getPlayer() != null && Gamemode.getCurrentGame() != null && Gamemode.DAMAGE != 0 && Gamemode.getCurrentGame().isAlive(getPlayer()))
            this.taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> {
                if (isInWater() && getPlayer() != null) {
                    if (!PlayerStatusManager.isInvincible(getPlayer()) && !getPlayer().getGameMode().equals(GameMode.CREATIVE) && !getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
                        damagePlayer();
                    }
                    Block b = getPlayer().getLocation().getBlock();
                    setInWater(((b.getType().equals(Material.WATER) || b.getType().equals(Material.STATIONARY_WATER)) && b.hasMetadata("classic_block")) ||
                            ((b.getRelative(BlockFace.UP).getType().equals(Material.WATER) || b.getRelative(BlockFace.UP).getType().equals(Material.STATIONARY_WATER)) &&
                                    b.getRelative(BlockFace.UP).hasMetadata("classic_block")));
                }
            }, (int) (20 * Gamemode.DAMAGE_FREQUENCY));
    }

    public void damagePlayer() {
        if (getPlayer() == null)
            return;
        if (getPlayer().getHealth() - Gamemode.DAMAGE <= 0 && LavaItem.SECOND_CHANCE.isItem(getPlayer().getInventory().getItemInOffHand())) {
            LavaItem.SECOND_CHANCE.consume(getPlayer());
            getPlayer().getInventory().setItemInOffHand(null);
            getPlayer().setHealth(1); //Set their health to half a heart to be in line with the totem mechanics
        } else
            ((CraftPlayer) getPlayer()).getHandle().damageEntity(DamageSource.OUT_OF_WORLD, (float) Gamemode.DAMAGE);
    }

    private Player getPlayer() {
        return this.bukkitPlayer;
    }

    public void setPlayer(Player p) {
        this.bukkitPlayer = p;
    }

    public boolean isInBank(MaterialData data) {
        for (MaterialData dat : this.BANK) {
            if (dat != null && data.equals(dat))
                return true;
        }
        return false;
    }

    public void giveBoughtBlocks() {
        Player p = getPlayer();
        if (p != null)
            for (MaterialData dat : this.ownedBlocks) {
                if (BukkitUtils.hasItem(p.getInventory(), dat) || isInBank(dat))
                    continue;
                p.getInventory().addItem(getItem(dat));
            }
    }

    @SuppressWarnings("deprecation")
    private void addBlock(MaterialData dat) {
        if (!this.ownedBlocks.contains(dat)) {
            this.ownedBlocks.add(dat);
            final String datInfo = dat.getItemType().toString() + ":" + dat.getData();
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        Connection conn = DriverManager.getConnection(Lavasurvival.INSTANCE.getDBURL(), Lavasurvival.INSTANCE.getDBProperties());
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE uuid = '" + UserInfo.this.userUUID + "'");
                        String curOwned = "", curBank = "";
                        if (rs.next()) {
                            curOwned = rs.getString("ownedBlocks");
                            if (!curOwned.equals(""))
                                curOwned += "|";
                            curBank = rs.getString("bank");
                            if (!curBank.equals(""))
                                curBank += "|";
                        }
                        rs.close();
                        stmt.execute("UPDATE users SET ownedBlocks = '" + curOwned + datInfo + "', bank = '" + curBank + datInfo + "' WHERE uuid = '" + UserInfo.this.userUUID + "'");
                        stmt.close();
                        conn.close();
                    } catch (Exception ignored) {
                    }
                }
            }.runTaskAsynchronously(Lavasurvival.INSTANCE);
            this.BANK.add(dat);
        }
    }

    private ItemStack getItem(MaterialData dat) {
        ItemStack i = dat.toItemStack(1);
        ItemMeta im = i.getItemMeta();
        im.setLore(Arrays.asList("Lava MeltTime: " + PhysicsListener.getLavaMeltTimeAsString(dat), "Water MeltTime: " + PhysicsListener.getWaterMeltTimeAsString(dat)));
        i.setItemMeta(im);
        return i;
    }

    private boolean ownsBlock(MaterialData dat) {
        return this.ownedBlocks.contains(dat);
    }

    @SuppressWarnings("deprecation")
    public void buyBlock(Material mat, double price, byte data) {
        buyBlock(new MaterialData(mat, data), price, true);
    }

    public void buyBlock(Material mat, double price) {
        buyBlock(new MaterialData(mat), price, false);
    }

    @SuppressWarnings("deprecation")
    private void buyBlock(MaterialData dat, double price, boolean hasData) {
        if (getPlayer() == null)
            return;
        if (ownsBlock(dat))
            getPlayer().sendMessage(ChatColor.RED + "You already own that block..");
        else if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(getPlayer()) || Lavasurvival.INSTANCE.getEconomy().getBalance(getPlayer()) < price)
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough money to buy the block type " + dat.getItemType().toString().replaceAll("_", " ").toLowerCase() +
                    (hasData ? " with data value " + dat.getData() : "") + "..");
        else {
            addBlock(dat);
            Lavasurvival.INSTANCE.withdrawAndUpdate(getPlayer(), price);
            getPlayer().sendMessage(ChatColor.GREEN + "You bought the block type " + dat.getItemType().toString().replaceAll("_", " ").toLowerCase() + (hasData ? " with data value " + dat.getData() : "") +
                    "! It was added to your bank.");
        }
    }

    public void incrementBlockCount() {
        this.blockChangeCount++;
    }

    public void usedGenerosity() {
        this.generosity = true;
    }

    public void resetGenerosity() {
        this.generosity = false;
    }

    public boolean wasGenerous() {
        return this.generosity;
    }
}