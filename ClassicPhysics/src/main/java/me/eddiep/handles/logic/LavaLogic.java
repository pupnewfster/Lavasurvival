package me.eddiep.handles.logic;


import me.eddiep.ClassicPhysics;
import me.eddiep.handles.ClassicPhysicsEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;


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
        Block block = location.getBlock();
        Material newBlock = block.getType();

        if (!block.getType().isSolid() && !doesHandle(block.getType())) {
            newBlock = logicFor();
        }

        ClassicPhysicsEvent event = new ClassicPhysicsEvent(location.getBlock(), newBlock, true, location, this, from);
        synchronized (ClassicPhysics.INSTANCE.getServer()) {
            ClassicPhysics.INSTANCE.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled())
                return;

            if (newBlock != block.getType())
                placeClassicBlock(newBlock, location, from);
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