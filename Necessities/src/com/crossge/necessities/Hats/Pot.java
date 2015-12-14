package com.crossge.necessities.Hats;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class Pot extends Hat {
    public Pot(Location loc) {
        this.trueLoc = loc;
        int turn = 5;
        Location temp = loc.clone().add(0, 0.5, 0);
        for (int i = 0; i < 360 / turn; i++) {
            temp.setYaw(temp.getYaw() + turn);
            spawn(1, temp);
        }
        for (int i = 0; i < 360 / turn; i++) {
            this.armorStands.get(i).setItemInHand(new ItemStack(Material.FISHING_ROD, 1));
            this.armorStands.get(i).setRightArmPose(new EulerAngle(Math.toRadians(135), Math.toRadians(90), 0));
            this.armorStands.get(i).setSmall(true);
        }
        super.spawn(1, loc.clone().add(0, 0.75, 0));
        this.armorStands.get(360/turn).setItemInHand(new ItemStack(Material.STICK, 1));
        this.armorStands.get(360/turn).setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), Math.toRadians(90)));
        this.armorStands.get(360 / turn).setSmall(true);
    }

    @Override
    protected void spawn(int num, Location loc) {
        World w = loc.getWorld();
        for (int i = 0; i < num; i ++) {
            ArmorStand a = (ArmorStand) w.spawnEntity(new Location(w, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 0), EntityType.ARMOR_STAND);
            a.setVisible(false);
            a.setGravity(false);
            a.setMarker(true);
            this.armorStands.add(a);
        }
    }
}