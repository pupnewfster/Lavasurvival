package me.eddiep.minecraft.ls.game;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.impl.Flood;
import me.eddiep.minecraft.ls.game.impl.Rise;
import me.eddiep.minecraft.ls.game.options.FloodOptions;
import me.eddiep.minecraft.ls.game.options.LavaOptions;
import me.eddiep.minecraft.ls.game.options.RiseOptions;
import me.eddiep.minecraft.ls.game.options.TimeOptions;
import me.eddiep.minecraft.ls.system.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LavaMap {
    public static final int CONFIG_VERSION = 3;

    private String name, worldName, filePath;
    private int lavax, lavay, lavaz, mapHeight;
    private Vector minSafeZone, maxSafeZone, mapSpawn;
    private int configVersion = 1; //Default version

    private RiseOptions riseOptions = RiseOptions.defaults();
    private FloodOptions floodOptions = FloodOptions.defaults();
    private TimeOptions time = TimeOptions.defaults();
    private LavaOptions lavaOptions = LavaOptions.defaults(this);

    private volatile World world;
    private volatile boolean poured;

    public static LavaMap load(String file) throws IOException {
        String contents = FileUtils.readAllText(file);
        LavaMap map = Lavasurvival.GSON.fromJson(contents, LavaMap.class);
        map.poured = false;
        map.filePath = file;

        if (!map.lavaOptions.hasParent()) {
            map.lavaOptions.setParent(map);
        }

        if (map.configVersion < CONFIG_VERSION) { //Update config with new values
            map.configVersion = CONFIG_VERSION;
            map.save();
        }

        return map;
    }

    public static String[] getPossibleMaps() {
        File configDir = new File(Lavasurvival.INSTANCE.getDataFolder(), "maps");
        ArrayList<String> maps = new ArrayList<>();
        File[] files = configDir.listFiles();
        if (files != null)
            for (File f : files)
                if (f.getAbsolutePath().endsWith(".map"))
                    maps.add(f.getAbsolutePath());

        return maps.toArray(new String[maps.size()]);
    }

    public void restoreBackup() {
        File directoy = new File(worldName);
        File backup = new File(Lavasurvival.INSTANCE.getDataFolder(), worldName);
        if (!backup.exists())
            return;

        if (directoy.exists()) {
            boolean val = directoy.delete();
            if (!val) {
                System.err.println("Could not delete world!");
                return;
            }
        } else
            return;
        try {
            FileUtils.copyDirectory(backup, directoy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() throws IOException {
        String json = Lavasurvival.GSON.toJson(this);
        me.eddiep.minecraft.ls.system.FileUtils.writeText(new File(Lavasurvival.INSTANCE.getDataFolder(), "maps/" + name + ".map").getAbsolutePath(), json);
    }

    @Deprecated
    public Location getLavaSpawnAsLocation() {
        return world.getBlockAt(lavax, lavay, lavaz).getLocation();
    }

    @Deprecated
    public Location getLavaSpawnAsLocation(int xoffset, int yoffset, int zoffet) {
        return world.getBlockAt(lavax + xoffset, lavay + yoffset, lavaz + zoffet).getLocation();
    }

    public void prepare() {
        restoreBackup();
        world = loadOrGetWorld(worldName);
        world.setAutoSave(false);
        world.setPVP(false);
        world.setAnimalSpawnLimit(0);
        world.setMonsterSpawnLimit(0);
        world.setSpawnFlags(false, false);//Do not let mobs or animals spawn
        world.setSpawnLocation(mapSpawn.getBlockX(), mapSpawn.getBlockY(), mapSpawn.getBlockZ());
        world.setTime(time.getStartTimeTick());

        for (Entity e : world.getEntities())
            e.remove();

        try {
            Lavasurvival.log("Backing up " + world.getName() + "...");
            FileUtils.copyDirectory(world.getWorldFolder(), new File(Lavasurvival.INSTANCE.getDataFolder(), world.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Entity entity : world.getEntities())
            entity.remove();
    }

    private World loadOrGetWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null)
            world = Bukkit.getServer().createWorld(new WorldCreator(worldName));
        return world;
    }

    public World getWorld() {
        return world;
    }

    public Vector getMapSpawn() {
        return mapSpawn;
    }

    public void setMapSpawn(Location mapSpawn) {
        this.mapSpawn = new Vector(mapSpawn.getX(), mapSpawn.getY(), mapSpawn.getZ());
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void setLavaSpawn(Location lavaSpawn) {
        this.lavax = lavaSpawn.getBlockX();
        this.lavay = lavaSpawn.getBlockY();
        this.lavaz = lavaSpawn.getBlockZ();
    }

    @Deprecated
    public int getLavaX() {
        return lavax;
    }

    @Deprecated
    public int getLavaY() {
        return lavay;
    }

    @Deprecated
    public int getLavaZ() {
        return lavaz;
    }

    public int getHeight() {
        return mapHeight;
    }

    public void setHeight(int height) {
        this.mapHeight = height;
    }

    public void setSafeZoneBounds(Location temp, Location temp2) {
        this.minSafeZone = new Vector(temp.getX(), temp.getY(), temp.getZ());
        this.maxSafeZone = new Vector(temp2.getX(), temp2.getY(), temp2.getZ());
    }

    public boolean isInSafeZone(Location loc) {
        double minX = this.minSafeZone.getX() - 1;
        double maxX = this.maxSafeZone.getX() + 1;
        double minY = this.minSafeZone.getY() - 1;
        double maxY = this.maxSafeZone.getY() + 1;
        double minZ = this.minSafeZone.getZ() - 1;
        double maxZ = this.maxSafeZone.getZ() + 1;

        return minX <= loc.getX() && minY <= loc.getY() && minZ <= loc.getZ() &&
                loc.getX() <= maxX && loc.getY() <= maxY && loc.getZ() <= maxZ;
    }

    public Class<? extends Gamemode>[] getEnabledGames() {
        List<Class<? extends Gamemode>> games = new ArrayList<>();

        if (riseOptions.isEnabled())
            games.add(Rise.class);

        if (floodOptions.isEnabled())
            games.add(Flood.class);

        return games.toArray(new Class[games.size()]);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return new File(filePath).getName();
    }

    public File getFile() {
        return new File(filePath);
    }

    public TimeOptions getTimeOptions() {
        return time;
    }

    public FloodOptions getFloodOptions() {
        return floodOptions;
    }

    public RiseOptions getRiseOptions() {
        return riseOptions;
    }

    public LavaOptions getLavaOptions() {
        return lavaOptions;
    }
}