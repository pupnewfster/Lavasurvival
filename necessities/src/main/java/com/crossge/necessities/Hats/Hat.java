package com.crossge.necessities.Hats;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;

@SuppressWarnings("SameParameterValue")
public abstract class Hat {
    final ArrayList<ArmorStand> armorStands = new ArrayList<>();
    Location trueLoc;
    private HatType type;
    //private double x1, y1, z1 = 1, pitch, yaw;

    public static Hat fromType(HatType type, Location loc) {
        Hat h = null;
        loc = loc.clone().add(0, 0.5, 0);
        if (type.equals(HatType.BoxTopHat))
            h = new BoxTopHat(loc);
        else if (type.equals(HatType.TopHat))
            h = new TopHat(loc);
        else if (type.equals(HatType.StrawHat))
            h = new StrawHat(loc);
        else if (type.equals(HatType.Fedora))
            h = new Fedora(loc);
        else if (type.equals(HatType.Pot))
            h = new Pot(loc);
        else if (type.equals(HatType.RimmedHat))
            h = new RimmedHat(loc);
        else if (type.equals(HatType.Trippy))
            h = new Trippy(loc);
        else if (type.equals(HatType.SunHat))
            h = new SunHat(loc);
        else if (type.equals(HatType.Design))
            h = new Design(loc);
        if (h != null)
            h.setType(type);
        return h;
    }

    public void despawn() {
        for (ArmorStand a : this.armorStands)
            a.remove();
    }

    void spawn(int num, Location loc) {
        World w = loc.getWorld();
        for (int i = 0; i < num; i++) {
            ArmorStand a = (ArmorStand) w.spawnEntity(new Location(w, loc.getX(), loc.getY(), loc.getZ()), EntityType.ARMOR_STAND);
            a.setVisible(false);
            a.setGravity(false);
            a.setMarker(true);
            this.armorStands.add(a);
        }
    }

    void spawnYaw(int num, Location loc) {
        World w = loc.getWorld();
        for (int i = 0; i < num; i++) {
            ArmorStand a = (ArmorStand) w.spawnEntity(new Location(w, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 0), EntityType.ARMOR_STAND);
            a.setVisible(false);
            a.setGravity(false);
            a.setMarker(true);
            this.armorStands.add(a);
        }
    }

    @SuppressWarnings("unused")
    public void move(double x, double y, double z, float yaw, float pitch) {
        /*this.pitch = ((90 - pitch) * Math.PI) / 180;
        this.yaw = ((yaw + 90 + 180) * Math.PI) / 180;
        double x2 = this.x1, y2 = this.y1, z2 = this.z1;
        this.y1 = Math.sin(Math.toRadians(this.pitch)) * Math.sin(Math.toRadians(this.yaw));
        this.x1 = Math.sin(Math.toRadians(this.pitch)) * Math.cos(Math.toRadians(this.yaw));
        this.z1 = Math.cos(Math.toRadians(this.pitch));
        int xChange = dir.getX() >= 0 ? 1 : -1;
        int zChange = dir.getZ() >= 0 ? 1 : -1;*/
        this.trueLoc = this.trueLoc.clone().add(x, y, z);
        //double ang = Math.toRadians(yaw);//Math.atan2(rx, rz));
        for (ArmorStand a : this.armorStands) {
            //Location loc = a.getLocation().add(x + (x2 - this.x1) * xChange, y + y2 - this.y1, z + (z2 - this.z1) * zChange);
            Location loc = a.getLocation().clone().add(x, y, z);
            //loc.setYaw(loc.getYaw() + yaw);
            a.teleport(loc);
            //a.setHeadPose(a.getHeadPose().add(Math.toRadians(pitchChange * xChange * zChange * -1), 0, 0));
        }
    }

    private void setType(HatType type) {
        this.type = type;
    }

    public HatType getType() {
        return this.type;
    }
}