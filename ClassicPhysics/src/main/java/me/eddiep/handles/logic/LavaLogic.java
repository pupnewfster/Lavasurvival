package me.eddiep.handles.logic;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;


public class LavaLogic extends AbstractLogicContainer {
    @Override
    protected void tickForBlock(Block block, Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        checkLocation(location.clone().add(1, 0, 0));
        checkLocation(location.clone().add(-1, 0, 0));
        checkLocation(location.clone().add(0, 0, 1));
        checkLocation(location.clone().add(0, 0, -1));
        checkLocation(location.clone().add(0, -1, 0));
    }

    protected void checkLocation(Location location) {
        Block block = location.getBlock();

        if (!block.getType().isSolid() && !doesHandle(block.getType()))
            placeClassicBlock(Material.STATIONARY_LAVA, location);
    }

    @Override
    public int updateRate() {
        return 20; //Every other tick
    }

    @Override
    public boolean doesHandle(Material material) {
        return material == Material.LAVA || material == Material.STATIONARY_LAVA;
    }
}
