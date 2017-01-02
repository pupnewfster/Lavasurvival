package me.eddiep.handles.logic;

import org.bukkit.Location;
import org.bukkit.Material;

public class WaterLogic extends LavaLogic {
    @Override
    public boolean doesHandle(Material material) {
        return Material.WATER.equals(material) || Material.STATIONARY_WATER.equals(material);
    }

    @Override
    protected void checkLocation(Location location, Location from) {
        super.checkLocation(location, from); //TODO Do different checks for water
    }

    @Override
    public Material logicFor() {
        return Material.STATIONARY_WATER;
    }
}