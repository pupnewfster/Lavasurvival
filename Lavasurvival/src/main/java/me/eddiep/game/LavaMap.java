package me.eddiep.game;

import me.eddiep.Lavasurvival;
import me.eddiep.system.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.util.FileUtil;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LavaMap {
    private String name;
    private int lavax;
    private int lavay;
    private int lavaz;
    private int mapHeight;
    private Vector minSafeZone;
    private Vector maxSafeZone;
    private Vector mapSpawn;
    private int signx;
    private int signy;
    private int signz;
    private String worldName;
    private String filePath;

    private volatile Sign joinSign;
    private volatile World world;
    private volatile boolean poured;

    public void save() throws IOException {
        String json = Lavasurvival.GSON.toJson(this);

        me.eddiep.system.FileUtils.writeText(new File(Lavasurvival.INSTANCE.getDataFolder(), "maps/" + name + ".map").getAbsolutePath(), json);
    }

    public static LavaMap load(String file) throws IOException {
        String contents = FileUtils.readAllText(file);

        LavaMap map = Lavasurvival.GSON.fromJson(contents, LavaMap.class);

        map.poured = false;
        map.filePath = file;

        return map;
    }

    public static String[] getPossibleMaps() {
        File configDir = new File(Lavasurvival.INSTANCE.getDataFolder(), "maps");

        ArrayList<String> maps = new ArrayList<String>();
        File[] files = configDir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getAbsolutePath().endsWith(".map"))
                    maps.add(f.getAbsolutePath());
            }
        }

        return maps.toArray(new String[maps.size()]);
    }

    public Location getLavaSpawnAsLocation() {
        return world.getBlockAt(lavax, lavay, lavaz).getLocation();
    }

    public void prepare() {
        world = loadOrGetWorld(worldName);
        world.setAutoSave(false);
        world.setPVP(false);
        world.setAnimalSpawnLimit(0);
        world.setMonsterSpawnLimit(0);
        world.setAnimalSpawnLimit(0);
        try {
            Lavasurvival.log("Backing up " + world.getName() + "...");
            FileUtils.copyDirectory(world.getWorldFolder(), new File(Lavasurvival.INSTANCE.getDataFolder(), world.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Entity entity : world.getEntities()) {
            entity.remove();
        }

        Block block = world.getBlockAt(signx, signy - 1, signz).getRelative(BlockFace.UP);
        if (!(block.getState() instanceof Sign)) {
            block.setType(Material.SIGN_POST);
        }

        joinSign = (Sign)block.getState();
    }

    private World loadOrGetWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.getServer().createWorld(new WorldCreator(worldName));
        }
        return world;
    }

    public Sign getJoinSign() {
        return joinSign;
    }

    public World getWorld() {
        return world;
    }

    public Vector getMapSpawn() {
        return mapSpawn;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setLavaSpawn(Location lavaSpawn) {
        this.lavax = lavaSpawn.getBlockX();
        this.lavay = lavaSpawn.getBlockY();
        this.lavaz = lavaSpawn.getBlockZ();
    }

    public int getLavaX() {
        return lavax;
    }

    public int getLavaY() {
        return lavay;
    }

    public int getLavaZ() {
        return lavaz;
    }

    public void setHeight(int height) {
        this.mapHeight = height;
    }

    public int getHeight() {
        return mapHeight;
    }

    public void setMapSpawn(Location mapSpawn) {
        this.mapSpawn = new Vector(mapSpawn.getX(), mapSpawn.getY(), mapSpawn.getZ());
    }

    public void setSafeZoneBounds(Location temp, Location temp2) {
        this.minSafeZone = new Vector(temp.getX(), temp.getY(), temp.getZ());
        this.maxSafeZone = new Vector(temp2.getX(), temp2.getY(), temp2.getZ());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSignLocation(Location signLocation) {
        signx = signLocation.getBlockX();
        signy = signLocation.getBlockY();
        signz = signLocation.getBlockZ();
    }

    public String getFilePath() {
        return filePath;
    }
}
