package com.crossge.necessities.WorldManager;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class WorldManager {
    private final File configFileWM = new File("plugins/Necessities/WorldManager", "worlds.yml");

    public void initiate() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Loading worlds...");
        YamlConfiguration configWM = YamlConfiguration.loadConfiguration(this.configFileWM);
        for (String world : configWM.getKeys(false)) {
            if (!worldExists(world) && (new File(world + "/level.dat")).exists()) {
                WorldCreator creator = new WorldCreator(world);
                if (configWM.contains(world + ".environment"))
                    creator.environment(getEnvironment(configWM.getString(world + ".environment")));
                if (configWM.contains(world + ".type"))
                    creator.type(getType(configWM.getString(world + ".type")));
                if (configWM.contains(world + ".structures"))
                    creator.generateStructures(configWM.getBoolean(world + ".structures"));
                if (configWM.contains(world + ".generator"))
                    creator.generator(configWM.getString(world + ".generator"));
                Bukkit.createWorld(creator);
            }
            setWorldProps(world);
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "All worlds loaded.");
    }

    @SuppressWarnings("unused")
    public GameMode getGameMode(String world) {
        YamlConfiguration configWM = YamlConfiguration.loadConfiguration(this.configFileWM);
        if (configWM.contains(world + ".gamemode")) {
            String gamemode = configWM.getString(world + ".gamemode");
            if (gamemode.equalsIgnoreCase("adventure"))
                return GameMode.ADVENTURE;
            if (gamemode.equalsIgnoreCase("creative"))
                return GameMode.CREATIVE;
            if (gamemode.equalsIgnoreCase("spectator"))
                return GameMode.SPECTATOR;
        }
        return GameMode.SURVIVAL;
    }

    private void setWorldProps(String world) {
        YamlConfiguration configWM = YamlConfiguration.loadConfiguration(this.configFileWM);
        if (worldExists(world)) {
            World w = Bukkit.getWorld(world);
            if (configWM.contains(world + ".difficulty"))
                w.setDifficulty(getDifficulty(configWM.getString(world + ".difficulty")));
            if (configWM.contains(world + ".pvp"))
                w.setPVP(configWM.getBoolean(world + ".pvp"));
            boolean spawnMonsters = true;
            boolean spawnAnimals = true;
            if (configWM.contains(world + ".spawning.animals"))
                spawnAnimals = configWM.getBoolean(world + ".spawning.animals");
            if (configWM.contains(world + ".spawning.monsters"))
                spawnMonsters = configWM.getBoolean(world + ".spawning.monsters");
            w.setSpawnFlags(spawnMonsters, spawnAnimals);
            if (configWM.contains(world + ".defaultSpawn") && !configWM.getBoolean(world + ".defaultSpawn")) {
                int x = 0;
                int y = 0;
                int z = 0;
                if (configWM.contains(world + ".spawn.x"))
                    x = configWM.getInt(world + ".spawn.x");
                if (configWM.contains(world + ".spawn.y"))
                    y = configWM.getInt(world + ".spawn.y");
                if (configWM.contains(world + ".spawn.z"))
                    z = configWM.getInt(world + ".spawn.z");
                w.setSpawnLocation(x, y, z);
            }
            w.setKeepSpawnInMemory(true);
        }
    }

    public Difficulty getDifficulty(String difficulty) {
        if (difficulty.equalsIgnoreCase("easy"))
            return Difficulty.EASY;
        if (difficulty.equalsIgnoreCase("normal"))
            return Difficulty.NORMAL;
        if (difficulty.equalsIgnoreCase("hard"))
            return Difficulty.HARD;
        return Difficulty.PEACEFUL;
    }

    public World.Environment getEnvironment(String environment) {
        if (environment.equalsIgnoreCase("nether"))
            return World.Environment.NETHER;
        if (environment.equalsIgnoreCase("the_end"))
            return World.Environment.THE_END;
        return World.Environment.NORMAL;
    }

    public WorldType getType(String type) {
        if (type.equalsIgnoreCase("amplified"))
            return WorldType.AMPLIFIED;
        if (type.equalsIgnoreCase("flat"))
            return WorldType.FLAT;
        if (type.equalsIgnoreCase("large_biomes"))
            return WorldType.LARGE_BIOMES;
        return WorldType.NORMAL;
    }

    public void unloadWorld(String name) {//WARNING: WILL GIVE ERRORS IF THERE ARE PLAYERS IN THE WORLD
        Bukkit.unloadWorld(name, true);
    }

    public void loadWorld(String name) {
        YamlConfiguration configWM = YamlConfiguration.loadConfiguration(this.configFileWM);
        if (!worldExists(name) && (new File(name + "/level.dat")).exists()) {
            WorldCreator creator = new WorldCreator(name);
            if (configWM.contains(name + ".environment"))
                creator.environment(getEnvironment(configWM.getString(name + ".environment")));
            if (configWM.contains(name + ".type"))
                creator.type(getType(configWM.getString(name + ".type")));
            if (configWM.contains(name + ".structures"))
                creator.generateStructures(configWM.getBoolean(name + ".structures"));
            creator.generator(configWM.getString(name + ".generator"));
            Bukkit.createWorld(creator);
        }
        setWorldProps(name);
    }

    public void setWorldSpawn(Location l) {
        YamlConfiguration configWM = YamlConfiguration.loadConfiguration(this.configFileWM);
        configWM.set(l.getWorld().getName() + ".defaultSpawn", false);
        configWM.set(l.getWorld().getName() + ".spawn.x", l.getBlockX());
        configWM.set(l.getWorld().getName() + ".spawn.y", l.getBlockY());
        configWM.set(l.getWorld().getName() + ".spawn.z", l.getBlockZ());
        try {
            configWM.save(this.configFileWM);
        } catch (Exception ignored) {
        }
        l.getWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public void setSetting(String name, String setting, String value) {
        YamlConfiguration configWM = YamlConfiguration.loadConfiguration(this.configFileWM);
        configWM.set(name + "." + setting, value);
        try {
            configWM.save(this.configFileWM);
        } catch (Exception ignored) {
        }
    }

    public void setSetting(String name, String setting, boolean value) {
        YamlConfiguration configWM = YamlConfiguration.loadConfiguration(this.configFileWM);
        configWM.set(name + "." + setting, value);
        try {
            configWM.save(this.configFileWM);
        } catch (Exception ignored) {
        }
    }

    public void createWorld(String name, World.Environment environment, String generator, WorldType type) {
        YamlConfiguration configWM = YamlConfiguration.loadConfiguration(this.configFileWM);
        configWM.set(name + ".difficulty", "EASY");
        configWM.set(name + ".gamemode", "SURVIVAL");
        configWM.set(name + ".environment", environment.toString());
        if (generator != null)
            configWM.set(name + ".generator", generator);
        configWM.set(name + ".type", type.getName().toUpperCase());
        configWM.set(name + ".inventorySystem", "default");
        configWM.set(name + ".structures", true);
        configWM.set(name + ".pvp", true);
        configWM.set(name + ".spawning.animals", true);
        configWM.set(name + ".spawning.monsters", true);
        configWM.set(name + ".defaultSpawn", true);
        configWM.set(name + ".spawn.x", 0);
        configWM.set(name + ".spawn.y", 64);
        configWM.set(name + ".spawn.z", 0);
        try {
            configWM.save(this.configFileWM);
        } catch (Exception ignored) {
        }
        WorldCreator creator = new WorldCreator(name);
        creator.environment(environment);
        creator.type(type);
        creator.generateStructures(true);
        creator.generator(generator);
        Bukkit.createWorld(creator);
        setWorldProps(name);
    }

    public void removeWorld(String name) {
        YamlConfiguration configWM = YamlConfiguration.loadConfiguration(this.configFileWM);
        configWM.set(name, null);
        try {
            configWM.save(this.configFileWM);
        } catch (Exception ignored) {
        }
    }

    public boolean worldUnloaded(String name) {
        return !worldExists(name) && YamlConfiguration.loadConfiguration(this.configFileWM).contains(name);
    }

    public boolean worldExists(String name) {
        return Bukkit.getWorld(name) != null;
    }
}