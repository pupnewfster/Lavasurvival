package com.crossge.necessities.Hats;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class TopHat extends Hat {
    public TopHat(Location loc) {
        this.trueLoc = loc;
        int turn = 5, turnV = 5;
        spawn(360 / turn, loc);
        for (int i = 0; i < 360 / turn; i++) {
            this.armorStands.get(i).setHelmet(new ItemStack(Material.CARPET, 1, (short) 15));
            this.armorStands.get(i).setHeadPose(new EulerAngle(0, Math.toRadians(i * turn), 0));
        }
        spawn(360 / turnV, loc.clone().add(0, 0.85, 0));
        for (int i = 0; i < 360 / turnV; i++) {
            this.armorStands.get(360 / turn + i).setHelmet(new ItemStack(Material.CARPET, 1, (short) 15));
            this.armorStands.get(360 / turn + i).setSmall(true);
            this.armorStands.get(360 / turn + i).setHeadPose(new EulerAngle(Math.toRadians(90), Math.toRadians(i * turnV), 0));
        }
    }
}