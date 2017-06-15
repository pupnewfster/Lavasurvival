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
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;

public final class ClassicPhysicsHandler implements Listener {
    private final HashSet<BlockVector> playerPlaced = new HashSet<>();
    private PhysicsEngine pe;
    private World current;
    private final Plugin owner;

    public ClassicPhysicsHandler(Plugin plugin) {
        this.owner = plugin;
        pe = new PhysicsEngine();
    }

    public boolean isClassicBlock(Vector v) {
        return pe.isClassicBlock(v.getBlockX(), v.getBlockY(), v.getBlockZ());
    }

    public void removeClassicBlock(Vector v) {
        //classicBlocks.remove(v.toBlockVector());//TODO fix
    }

    //Player placed is stored in classic physics now instead of ls so that we can clear it at the correct time
    public boolean isPlayerPlaced(Vector v) { //If we want to know who placed the block it will need to go to a hash map instead
        return playerPlaced.contains(v.toBlockVector());
    }

    public void addPlayerPlaced(Vector v) { //Make sure that isPlayerPlaced is called before adding it through here
        playerPlaced.add(v.toBlockVector().clone());
    }

    public void removePlayerPlaced(Vector v) {
        playerPlaced.remove(v.toBlockVector());
    }

    public Plugin getOwner() {
        return owner;
    }

    public void setPhysicsWorld(World w) {
        this.current = w;
        if (w == null)
            pe.end();
        else
            pe.start(w.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent event) {
        if (!event.isCancelled() && event.getWorld().equals(this.current))
            setPhysicsWorld(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();
        pe.addMeltTimer(b.getX(), b.getY(), b.getZ(), new MaterialData(b.getType(), b.getData()));
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