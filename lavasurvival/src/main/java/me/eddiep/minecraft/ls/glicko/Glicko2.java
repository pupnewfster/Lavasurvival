package me.eddiep.minecraft.ls.glicko;

import me.eddiep.minecraft.ls.Lavasurvival;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class Glicko2 {
    private static final File CONFIG_FILE = new File("ranking.conf");

    private double tau;
    private int default_rating;
    private int default_rd;
    private double default_vol;
    private Glicko2() { }

    private static Glicko2 INSTANCE;
    public static Glicko2 getInstance() {
        if (INSTANCE != null)
            return INSTANCE;
        INSTANCE = new Glicko2();
        FileConfiguration config = Lavasurvival.INSTANCE.getConfig();
        INSTANCE.tau = config.getDouble("glicko.tau", 0.5);
        INSTANCE.default_rating = config.getInt("glicko.defaultRank", 1500);
        INSTANCE.default_rd = config.getInt("glicko.defaultDeviation", 350);
        INSTANCE.default_vol = config.getDouble("glicko.defaultVolatility", 0.06);
        return INSTANCE;
    }

    public GlickoRank defaultRank() {
        return new GlickoRank(default_rating, default_rd, default_vol);
    }

    public double getTau() {
        return tau;
    }

    public int getDefaultRating() {
        return default_rating;
    }

    public double getDefaultRd() {
        return default_rd;
    }

    public double getDefaultVol() {
        return default_vol;
    }
}