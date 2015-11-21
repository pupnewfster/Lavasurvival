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

        checkLocation(location.add(x + 1, y ,z));
        checkLocation(location.add(x - 1, y , z));
        checkLocation(location.add(x, y, z + 1));
        checkLocation(location.add(x, y, z - 1));
        checkLocation(location.add(x, y - 1, z));
    }

    protected void checkLocation(Location location) {
        Block block = location.getBlock();

        switch (block.getType()) {
            //TODO Add more things here
            case AIR:
                placeClassicBlock(Material.LAVA, location);
        }
    }

    @Override
    public int updateRate() {
        return 2; //Every other tick
    }

    @Override
    public boolean doesHandle(Material material) {
        return material == Material.LAVA || material == Material.STATIONARY_LAVA;
    }
}
