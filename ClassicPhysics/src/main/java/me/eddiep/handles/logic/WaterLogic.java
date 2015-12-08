package me.eddiep.handles.logic;

import org.bukkit.Location;
import org.bukkit.Material;

public class WaterLogic extends LavaLogic {

    @Override
    public boolean doesHandle(Material material) {
        return material == Material.WATER || material == Material.STATIONARY_WATER;
    }

    @Override
    protected void checkLocation(Location location) {
        super.checkLocation(location); //TODO Do different checks for water
    }

    @Override
    public Material logicFor() {
        return Material.STATIONARY_WATER;
    }
}