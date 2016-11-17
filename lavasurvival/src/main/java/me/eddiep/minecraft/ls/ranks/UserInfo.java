package me.eddiep.minecraft.ls.ranks;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.status.PlayerStatusManager;
import me.eddiep.minecraft.ls.glicko.Glicko2;
import me.eddiep.minecraft.ls.glicko.GlickoRank;
import me.eddiep.minecraft.ls.glicko.Rankable;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UserInfo implements Rankable {
    private File configFileUsers = new File(Lavasurvival.INSTANCE.getDataFolder(), "userinfo.yml");
    private ArrayList<MaterialData> ownedBlocks = new ArrayList<>();
    private long lastBreak = System.currentTimeMillis(), blockChangeCount;
    private boolean inWater = false;
    private Player bukkitPlayer;
    private int taskID = 0;
    private UUID userUUID;
    private List<ItemStack> BANK = new ArrayList<ItemStack>();
    private GlickoRank rank;
    private boolean generosity;

    public UserInfo(Player p) {
        this.bukkitPlayer = p;
        this.userUUID = p.getUniqueId();
        this.rank = Glicko2.getInstance().defaultRank();
        load();
    }

    public UserInfo(UUID uuid) {
        this.userUUID = uuid;
        this.rank = Glicko2.getInstance().defaultRank();
        load();
    }

    public long getBlockChangeCount() {
        return this.blockChangeCount;
    }

    public void resetBlockChangeCount() {
        this.blockChangeCount = 0;
    }

    public void load() {
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(this.configFileUsers);
        if (!configUsers.contains(getUUID().toString()))
            return;

        if (configUsers.contains(getUUID() + ".bank")) {
            this.BANK = (List<ItemStack>) configUsers.getList(getUUID() + ".bank");
        }

        this.rank.load(getUUID().toString(), configUsers);

        if (configUsers.contains(getUUID().toString() + ".boughtBlocks") || !this.ownedBlocks.isEmpty()) {
            for (String key : configUsers.getStringList(getUUID().toString() + ".boughtBlocks"))
                if (!key.equals("") && key.split("-").length == 2) {
                    String name = key.split("-")[0];
                    byte damage = (byte) Integer.parseInt(key.split("-")[1]);
                    this.ownedBlocks.add(new MaterialData(Material.valueOf(name), damage));
                }
        } else {
            configUsers.set(getUUID().toString() + ".boughtBlocks", Arrays.asList(""));
            try {
                configUsers.save(this.configFileUsers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Inventory createBankInventory(Player p) {
        return BankInventory.create(p, this.BANK).openFor(p);
    }

    public void saveBank(Player owner) {
        BankInventory bank = BankInventory.from(owner);
        if (bank != null)
            this.BANK = bank.getItems();
    }

    public void save() {
        if (this.ownedBlocks.isEmpty())
            return;
        YamlConfiguration configUsers = YamlConfiguration.loadConfiguration(this.configFileUsers);
        if (!configUsers.contains(getUUID().toString()))
            return;
        List<String> bl = configUsers.getStringList(getUUID().toString() + ".boughtBlocks");
        if (bl.contains(""))
            bl.remove("");
        this.ownedBlocks.stream().filter(data -> !bl.contains(data.getItemType().toString() + "-" + data.getData())).forEach(data -> bl.add(data.getItemType().toString() + "-" + data.getData()));
        configUsers.set(getUUID().toString() + ".boughtBlocks", bl);
        configUsers.set(getUUID() + ".bank", this.BANK);
        rank.save(getUUID().toString(), configUsers);
        try {
            configUsers.save(this.configFileUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private UUID getUUID() {
        return getPlayer() == null ? this.userUUID : getPlayer().getUniqueId();
    }

    public String getName() {
        return getPlayer() == null ? Bukkit.getOfflinePlayer(getUUID()).getName() : getPlayer().getName();
    }

    public boolean isInWater() {
        return this.inWater;
    }

    public void setInWater(boolean value) {
        this.inWater = value;
        if (!isInWater())
            Bukkit.getScheduler().cancelTask(this.taskID);
        if (value && getPlayer() != null && Gamemode.getCurrentGame() != null && Gamemode.DAMAGE != 0 && Gamemode.getCurrentGame().isAlive(getPlayer()))
            this.taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> {
                if (isInWater() && getPlayer() != null) {
                    if (!PlayerStatusManager.isInvincible(getPlayer())  && !getPlayer().getGameMode().equals(GameMode.CREATIVE) && !getPlayer().getGameMode().equals(GameMode.SPECTATOR))
                        ((CraftPlayer) getPlayer()).getHandle().damageEntity(DamageSource.OUT_OF_WORLD, (float) Gamemode.DAMAGE);
                    Block b = getPlayer().getLocation().getBlock();
                    setInWater(((b.getType().equals(Material.WATER) || b.getType().equals(Material.STATIONARY_WATER)) && b.hasMetadata("classic_block")) ||
                            ((b.getRelative(BlockFace.UP).getType().equals(Material.WATER) || b.getRelative(BlockFace.UP).getType().equals(Material.STATIONARY_WATER)) &&
                                    b.getRelative(BlockFace.UP).hasMetadata("classic_block")));
                }
            }, (int) (20 * Gamemode.DAMAGE_FREQUENCY));
    }

    public Player getPlayer() {
        return this.bukkitPlayer;
    }

    public void setPlayer(Player p) {
        this.bukkitPlayer = p;
    }

    public boolean isInBank(MaterialData data) {
        for (ItemStack item : this.BANK) {
            if (item == null)
                continue;

            if (data.equals(item.getData()))
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

    private void addBlock(MaterialData dat) {
        if (!this.ownedBlocks.contains(dat)) {
            this.ownedBlocks.add(dat);
            if (getPlayer() != null)
                getPlayer().getInventory().addItem(getItem(dat));
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

    public void buyBlock(Material mat, double price, byte data) {
        buyBlock(new MaterialData(mat, data), price, true);
    }

    public void buyBlock(Material mat, double price) {
        buyBlock(new MaterialData(mat), price, false);
    }

    private void buyBlock(MaterialData dat, double price, boolean hasData) {
        if (getPlayer() == null)
            return;
        if (ownsBlock(dat))
            getPlayer().sendMessage(ChatColor.RED + "You already own that block..");
        else if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(getPlayer()) || Lavasurvival.INSTANCE.getEconomy().getBalance(getPlayer()) < price)
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough money to buy the block type " + dat.getItemType().toString().replaceAll("_", " ").toLowerCase() +
                    (hasData ? " with datavalue " + dat.getData() : "") + "..");
        else if (BukkitUtils.isInventoryFull(getPlayer().getInventory()))
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough inventory space to buy any more blocks..");
        else {
            addBlock(dat);
            Lavasurvival.INSTANCE.withdrawAndUpdate(getPlayer(), price);
            getPlayer().sendMessage(ChatColor.GREEN + "You bought the block type " + dat.getItemType().toString().replaceAll("_", " ").toLowerCase() + (hasData ? " with datavalue " + dat.getData() : "") + "!");
        }
    }

    public void incrimentBlockCount() {
        this.blockChangeCount++;
    }

    @Override
    public GlickoRank getRanking() {
        return this.rank;
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