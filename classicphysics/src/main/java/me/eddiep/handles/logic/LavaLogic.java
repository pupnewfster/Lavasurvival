package me.eddiep.handles.logic;

import me.eddiep.ClassicPhysics;
import me.eddiep.handles.ClassicBlockPlaceEvent;
import me.eddiep.handles.ClassicPhysicsEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class LavaLogic extends AbstractLogicContainer {
    @Override
    protected void tickForBlock(Location location) {
        if (location == null)
            return;
        checkLocation(location.clone().add(1, 0, 0), location);
        checkLocation(location.clone().add(-1, 0, 0), location);
        checkLocation(location.clone().add(0, 0, 1), location);
        checkLocation(location.clone().add(0, 0, -1), location);
        checkLocation(location.clone().add(0, -1, 0), location);
    }

    void checkLocation(Location location, Location from) {
        if (location == null)
            return;
        synchronized (ClassicPhysics.Sync) {
            try {
                if (!location.getChunk().isLoaded())//World isn't loaded
                    return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            Vector lv = location.toVector();
            if (ClassicPhysics.INSTANCE.getPhysicsHandler().isClassicBlock(lv))
                return;
            Block block = location.getBlock();
            Material newBlock = block.getType();

            if (!block.getType().isSolid() && !doesHandle(block.getType()))
                newBlock = logicFor();
            if (newBlock.equals(Material.WATER))
                newBlock = Material.STATIONARY_WATER;
            else if (newBlock.equals(Material.LAVA))
                newBlock = Material.STATIONARY_LAVA;

            ClassicPhysicsEvent event = new ClassicPhysicsEvent(location.getBlock(), newBlock, location, this, from);
            ClassicPhysics.INSTANCE.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled())
                return;

            if (newBlock != block.getType())
                ClassicPhysics.INSTANCE.getPhysicsHandler().placeClassicBlockAt(location, newBlock, from);
            else if (!ClassicPhysics.INSTANCE.getPhysicsHandler().isClassicBlock(lv)) {
                ClassicPhysics.INSTANCE.getPhysicsHandler().addClassicBlock(lv);
                ClassicPhysics.INSTANCE.getServer().getPluginManager().callEvent(new ClassicBlockPlaceEvent(location));
                if (doesHandle(block.getType()))
                    queueBlock(location);
            }
        }
    }

    @Override
    public int updateRate() {
        return 10;
    }

    @Override
    public boolean doesHandle(Material material) {
        return Material.LAVA.equals(material) || Material.STATIONARY_LAVA.equals(material);
    }

    @Override
    public Material logicFor() {
        return Material.STATIONARY_LAVA;
    }
}