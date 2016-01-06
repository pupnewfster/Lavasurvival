package com.crossge.necessities.Hats;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class StrawHat extends Hat {
    public StrawHat(Location loc) {
        this.trueLoc = loc;
        int turn = 5, turnV = 5;
        Location temp = loc.clone().add(0, 0.5, 0);
        for (int i = 0; i < 360 / turn; i++) {
            temp.setYaw(temp.getYaw() + turn);
            spawnYaw(1, temp);
        }
        for (int i = 0; i < 360 / turn; i++) {
            this.armorStands.get(i).setItemInHand(new ItemStack(Material.HAY_BLOCK, 1));
            this.armorStands.get(i).setRightArmPose(new EulerAngle(0, Math.toRadians(90), Math.toRadians(90)));
            this.armorStands.get(i).setSmall(true);
        }
        spawn(360 / turnV, loc.clone().add(0, 0.7, 0));
        for (int i = 0; i < 360 / turnV; i++) {
            this.armorStands.get(360 / turn + i).setHelmet(new ItemStack(Material.HAY_BLOCK, 1));
            this.armorStands.get(360 / turn + i).setSmall(true);
            this.armorStands.get(360 / turn + i).setHeadPose(new EulerAngle(Math.toRadians(45), Math.toRadians(i * turnV), 0));
        }
    }
}