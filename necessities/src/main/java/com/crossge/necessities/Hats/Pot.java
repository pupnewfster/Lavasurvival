package com.crossge.necessities.Hats;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class Pot extends Hat {
    public Pot(Location loc) {
        this.trueLoc = loc;
        int turn = 5;
        Location temp = loc.clone().add(0, 0.5, 0);
        for (int i = 0; i < 360 / turn; i++) {
            temp.setYaw(temp.getYaw() + turn);
            spawnYaw(1, temp);
        }
        for (int i = 0; i < 360 / turn; i++) {
            this.armorStands.get(i).setItemInHand(new ItemStack(Material.FISHING_ROD, 1));
            this.armorStands.get(i).setRightArmPose(new EulerAngle(Math.toRadians(135), Math.toRadians(90), 0));
            this.armorStands.get(i).setSmall(true);
        }
        spawn(1, loc.clone().add(0, 0.75, 0));
        this.armorStands.get(360/turn).setItemInHand(new ItemStack(Material.STICK, 1));
        this.armorStands.get(360/turn).setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), Math.toRadians(90)));
        this.armorStands.get(360 / turn).setSmall(true);
    }
}