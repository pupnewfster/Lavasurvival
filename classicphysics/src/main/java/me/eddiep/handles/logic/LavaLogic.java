package me.eddiep.handles.logic;

import me.eddiep.ClassicPhysics;
import me.eddiep.handles.ClassicBlockPlaceEvent;
import me.eddiep.handles.ClassicPhysicsEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

public class LavaLogic extends AbstractLogicContainer {
    @Override
    protected void tickForBlock(Block block, Location location) {
        checkLocation(location.clone().add(1, 0, 0), location);
        checkLocation(location.clone().add(-1, 0, 0), location);
        checkLocation(location.clone().add(0, 0, 1), location);
        checkLocation(location.clone().add(0, 0, -1), location);
        checkLocation(location.clone().add(0, -1, 0), location);
    }

    protected void checkLocation(Location location, Location from) {
        synchronized (ClassicPhysics.Sync) {
            try {
                if (location == null || location.getWorld() == null || location.getChunk() == null || !location.getChunk().isLoaded() || location.getBlock() == null)//World isn't loaded
                    return;
            } catch (Exception e) {
                return;
            }
            Block block = location.getBlock();
            if (block.hasMetadata("classic_block") && block.isLiquid())
                return;
            Material newBlock = block.getType();

            if (!block.getType().isSolid() && !doesHandle(block.getType()))
                newBlock = logicFor();
            if (newBlock.equals(Material.WATER))
                newBlock = Material.STATIONARY_WATER;
            else if (newBlock.equals(Material.LAVA))
                newBlock = Material.STATIONARY_LAVA;

            ClassicPhysicsEvent event = new ClassicPhysicsEvent(location.getBlock(), newBlock, true, location, this, from);
            ClassicPhysics.INSTANCE.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled())
                return;

            if (newBlock != block.getType())
                placeClassicBlock(newBlock, location, from);
            else if (!block.hasMetadata("classic_block")) {
                block.setMetadata("classic_block", new FixedMetadataValue(ClassicPhysics.INSTANCE, true));
                ClassicPhysics.INSTANCE.getServer().getPluginManager().callEvent(new ClassicBlockPlaceEvent(location));
                if (doesHandle(block.getType()))
                    queueBlock(block);
            }
        }
    }

    @Override
    public int updateRate() {
        return 20; //Every other tick
    }

    @Override
    public boolean doesHandle(Material material) {
        return material == Material.LAVA || material == Material.STATIONARY_LAVA;
    }

    @Override
    public Material logicFor() {
        return Material.STATIONARY_LAVA;
    }
}