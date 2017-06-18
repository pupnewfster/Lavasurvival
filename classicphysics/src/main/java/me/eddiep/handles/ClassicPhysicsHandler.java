package me.eddiep.handles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFallingBlock;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import java.util.List;

public final class ClassicPhysicsHandler implements Listener {
    private PhysicsEngine pe;
    private final Plugin owner;

    public ClassicPhysicsHandler(Plugin plugin) {
        this.owner = plugin;
        pe = new PhysicsEngine();
    }

    public PhysicsEngine getPhysicsEngine() {
        return pe;
    }

    public Plugin getOwner() {
        return owner;
    }

    public void setPhysicsWorld(World w) {
        if (w == null)
            pe.end();
        else
            pe.start(w.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();
        pe.addMeltTimer(b.getX(), b.getY(), b.getZ(), new MaterialData(b.getType(), b.getData()));
        if (b.getType().toString().contains("DOOR") && b.getRelative(BlockFace.UP).getType().equals(b.getType()))
            pe.addMeltTimer(b.getX(), b.getY() + 1, b.getZ(), new MaterialData(b.getType(), b.getData()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        pe.addMeltTimer(b.getX(), b.getY(), b.getZ(), new MaterialData(Material.AIR));
    }

    @EventHandler
    public void onIceMelt(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCactusGrow(BlockGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void fireSpread(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void blockFall(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            FallingBlock f = (FallingBlock) event.getEntity();
            event.setCancelled(true);
            if (!f.isGlowing())
                event.getBlock().getState().update(true, false);
            else {
                String uid = f.getUniqueId().toString();
                f = f.getWorld().spawnFallingBlock(f.getLocation(), new MaterialData(f.getMaterial(), f.getBlockData()));
                f.setGlowing(true);
                f.setGravity(false);
                ((CraftFallingBlock) f).getHandle().ticksLived = -2147483648; //Bypass the spigot check of it being negative
                Team t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Special");
                if (t != null) {
                    t.removeEntry(uid);
                    t.addEntry(f.getUniqueId().toString());
                }
            }
        }
    }

    private List<MaterialData> fallingTypes;

    public void addFallingTypes(List<MaterialData> types) {
        this.fallingTypes = types;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void itemSpawn(ItemSpawnEvent event) {
        org.bukkit.entity.Item i = event.getEntity();
        MaterialData data = i.getItemStack().getData();
        if (data.getItemType().equals(Material.DROPPER))
            data = new MaterialData(Material.DROPPER, (byte) 1);
        if (i.getTicksLived() == 0 && this.fallingTypes.contains(data)) {
            FallingBlock f = i.getWorld().spawnFallingBlock(i.getLocation(), data);
            f.setGlowing(true);
            f.setGravity(false);
            ((CraftFallingBlock) f).getHandle().ticksLived = -2147483648; //Bypass the spigot check of it being negative
            Team t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Special");
            if (t != null)
                t.addEntry(f.getUniqueId().toString());
        }
    }

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    public void forcePlaceClassicBlockAt(Location location, Material type) {//Force place block
        pe.placeAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPhysicsUpdate(BlockPhysicsEvent event) {
        if (!event.getBlock().getType().toString().contains("DOOR") || !event.getChangedType().toString().contains("PLATE") || (event.getBlock().getType().toString().contains("DOOR") &&
                event.getChangedType().toString().contains("PLATE") && !event.getBlock().getType().equals(event.getBlock().getRelative(BlockFace.UP).getType())))
            event.setCancelled(true);
    }

    public void enable() {
        owner.getServer().getPluginManager().registerEvents(this, owner);
    }

    public void disable() {
        BlockPhysicsEvent.getHandlerList().unregister(this);
        pe.end();
    }
}