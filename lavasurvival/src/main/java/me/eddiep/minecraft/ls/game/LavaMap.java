package me.eddiep.minecraft.ls.game;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.impl.Flood;
import me.eddiep.minecraft.ls.game.impl.Fusion;
import me.eddiep.minecraft.ls.game.impl.Rise;
import me.eddiep.minecraft.ls.game.options.FloodOptions;
import me.eddiep.minecraft.ls.game.options.FusionOptions;
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
    private static final int CONFIG_VERSION = 8;

    private String name, worldName, filePath;
    private int lavax, lavay, lavaz, mapHeight;
    private Vector minSafeZone, maxSafeZone, mapSpawn;
    private int configVersion = 1; //Default version
    private boolean isThundering = false;
    private String creator = "";
    private double meltMultiplier = 0.5;
    private RiseOptions riseOptions = RiseOptions.defaults(this);
    private FloodOptions floodOptions = FloodOptions.defaults(this);
    private FusionOptions fusionOptions = FusionOptions.defaults(this, riseOptions);
    private TimeOptions time = TimeOptions.defaults();

    private volatile World world;
    private volatile boolean poured;

    static LavaMap load(String file) throws IOException {
        String contents = FileUtils.readAllText(file);
        LavaMap map = Lavasurvival.GSON.fromJson(contents, LavaMap.class);
        map.poured = false;
        map.filePath = file;
        if (!map.riseOptions.hasParent())
            map.riseOptions.setParent(map);
        if (!map.floodOptions.hasParent())
            map.floodOptions.setParent(map);
        if (!map.fusionOptions.hasParent())
            map.fusionOptions.setParent(map);
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

    private void restoreBackup() {
        File directoy = new File(this.worldName);
        File backup = new File(Lavasurvival.INSTANCE.getDataFolder(), worldName);
        if (!backup.exists())
            return;
        if (directoy.exists()) {
            if (!directoy.delete()) {
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
        me.eddiep.minecraft.ls.system.FileUtils.writeText(new File(Lavasurvival.INSTANCE.getDataFolder(), "maps/" + this.name + ".map").getAbsolutePath(), Lavasurvival.GSON.toJson(this));
    }

    @Deprecated
    public Location getLavaSpawnAsLocation() {
        return this.world.getBlockAt(this.lavax, this.lavay, this.lavaz).getLocation();
    }

    @Deprecated
    public Location getLavaSpawnAsLocation(int xoffset, int yoffset, int zoffet) {
        return this.world.getBlockAt(this.lavax + xoffset, this.lavay + yoffset, this.lavaz + zoffet).getLocation();
    }

    void prepare() {
        restoreBackup();
        this.world = loadOrGetWorld(this.worldName);
        this.world.setAutoSave(false);
        this.world.setPVP(false);
        this.world.setAnimalSpawnLimit(0);
        this.world.setWaterAnimalSpawnLimit(0);
        this.world.setMonsterSpawnLimit(0);
        this.world.setThundering(this.isThundering);
        this.world.setThunderDuration(1);
        this.world.setWeatherDuration(Integer.MAX_VALUE);
        this.world.setGameRuleValue("randomTickSpeed", "0");
        this.world.setGameRuleValue("mobGriefing", "false");
        this.world.setGameRuleValue("spectatorsGenerateChunks", "false");
        this.world.setSpawnFlags(false, false);//Do not let mobs or animals spawn
        this.world.setSpawnLocation(this.mapSpawn.getBlockX(), this.mapSpawn.getBlockY(), this.mapSpawn.getBlockZ());
        this.world.setTime(this.time.getStartTimeTick());
        this.world.setKeepSpawnInMemory(true);
        if (!this.time.isEnabled())
            this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.getEntities().forEach(Entity::remove);
        File backupFile = new File(Lavasurvival.INSTANCE.getDataFolder(), this.world.getName());
        if (!backupFile.exists()) //Make it so that only makes a backup if there is not already a backup
            try {
                Lavasurvival.log("Backing up " + this.world.getName() + "...");
                FileUtils.copyDirectory(this.world.getWorldFolder(), backupFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private World loadOrGetWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        return world == null ? Bukkit.getServer().createWorld(new WorldCreator(worldName)) : world;
    }

    public World getWorld() {
        return this.world;
    }

    public Vector getMapSpawn() {
        return this.mapSpawn;
    }

    public void setMapSpawn(Location mapSpawn) {
        this.mapSpawn = new Vector(mapSpawn.getX(), mapSpawn.getY(), mapSpawn.getZ());
    }

    public String getWorldName() {
        return this.worldName;
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
        return this.lavax;
    }

    @Deprecated
    public int getLavaY() {
        return this.lavay;
    }

    @Deprecated
    public int getLavaZ() {
        return this.lavaz;
    }

    public int getHeight() {
        return this.mapHeight;
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
        return minX <= loc.getX() && minY <= loc.getY() && minZ <= loc.getZ() && loc.getX() <= maxX && loc.getY() <= maxY && loc.getZ() <= maxZ;
    }

    Class<? extends Gamemode>[] getEnabledGames() {
        List<Class<? extends Gamemode>> games = new ArrayList<>();
        if (this.riseOptions.isEnabled())
            games.add(Rise.class);
        if (this.floodOptions.isEnabled())
            games.add(Flood.class);
        if (this.fusionOptions.isEnabled())
            games.add(Fusion.class);
        return games.toArray(new Class[games.size()]);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getFileName() {
        return new File(this.filePath).getName();
    }

    File getFile() {
        return new File(this.filePath);
    }

    TimeOptions getTimeOptions() {
        return this.time;
    }

    public FloodOptions getFloodOptions() {
        return this.floodOptions;
    }

    public RiseOptions getRiseOptions() {
        return this.riseOptions;
    }

    public FusionOptions getFusionOptions() {
        return this.fusionOptions;
    }

    String getCreator() {
        return this.creator;
    }

    public double getMeltMultiplier() {
        return this.meltMultiplier;
    }
}