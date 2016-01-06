package com.crossge.necessities.Hats;

import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class BoxTopHat extends Hat {
    private double dist = 0.22;

    public BoxTopHat(Location loc) {
        this.trueLoc = loc;
        spawn(1, loc);
        this.armorStands.get(0).setHelmet(new ItemStack(Material.CARPET, 1, (short) 15));
        spawn(1, loc.clone().add(0, 0.85, this.dist));
        this.armorStands.get(1).setHelmet(new ItemStack(Material.CARPET, 1, (short) 15));
        this.armorStands.get(1).setSmall(true);
        this.armorStands.get(1).setHeadPose(new EulerAngle(Math.toRadians(90), 0, Math.toRadians(90)));
        spawn(1, loc.clone().add(this.dist, 0.85, 0));
        this.armorStands.get(2).setHelmet(new ItemStack(Material.CARPET, 1, (short) 15));
        this.armorStands.get(2).setSmall(true);
        this.armorStands.get(2).setHeadPose(new EulerAngle(Math.toRadians(90), -Math.toRadians(90), 0));
        spawn(1, loc.clone().add(0, 0.85, -this.dist));
        this.armorStands.get(3).setHelmet(new ItemStack(Material.CARPET, 1, (short) 15));
        this.armorStands.get(3).setSmall(true);
        this.armorStands.get(3).setHeadPose(new EulerAngle(-Math.toRadians(90), 0, Math.toRadians(90)));
        spawn(1, loc.clone().add(-this.dist, 0.85, 0));
        this.armorStands.get(4).setHelmet(new ItemStack(Material.CARPET, 1, (short) 15));
        this.armorStands.get(4).setSmall(true);
        this.armorStands.get(4).setHeadPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), 0));
        spawn(1, loc.clone().add(0, 1.1, 0));
        this.armorStands.get(5).setHelmet(new ItemStack(Material.CARPET, 1, (short) 15));
        this.armorStands.get(5).setSmall(true);
        final float yaw = this.trueLoc.getYaw();
        try {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), new Runnable() {
                @Override
                public void run() {
                    move(0, 0, 0, yaw, 0);
                }
            }, 1);//Wait a tick and reposition orientation
        } catch (Exception er) {
        }
    }

    @Override
    public void move(double x, double y, double z, float yaw, float pitch) {
        double ang = Math.toRadians(yaw);
        this.trueLoc = this.trueLoc.clone().add(x, y, z);
        for (int i = 0; i < this.armorStands.size(); i++) {
            Location loc = this.armorStands.get(i).getLocation().clone().add(x, y, z);
            if (i != 0 && i != 5) {
                double an = Math.atan2(loc.getZ() - this.trueLoc.getZ(), loc.getX() - this.trueLoc.getX()) + ang;
                double xn = this.trueLoc.getX() + this.dist * Math.cos(an);
                double zn = this.trueLoc.getZ() + this.dist * Math.sin(an);
                loc.setX(xn);
                loc.setZ(zn);
            }
            loc.setYaw(loc.getYaw() + yaw);
            this.armorStands.get(i).teleport(loc);
        }
    }
}