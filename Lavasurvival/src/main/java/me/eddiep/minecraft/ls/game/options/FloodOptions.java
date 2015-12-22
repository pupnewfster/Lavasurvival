package me.eddiep.minecraft.ls.game.options;

import java.util.Random;

public class FloodOptions extends BaseOptions {

    private int minPrepareTimeSeconds = 300;
    private int maxPrepareTimeSeconds = 480;
    private int minEndTimeSeconds = 180;
    private int maxEndTimeSeconds = 420;
    private boolean enableLava = true;
    private boolean enableWater = true;

    public static FloodOptions defaults() {
        return new FloodOptions();
    }

    FloodOptions() { }

    public long generateRandomPrepareTime() {
        int seconds = RANDOM.nextInt(maxPrepareTimeSeconds - minPrepareTimeSeconds) + minPrepareTimeSeconds;

        return seconds * 1000L;
    }

    public long generateRandomEndTime() {
        int seconds = RANDOM.nextInt(maxEndTimeSeconds - minEndTimeSeconds) + minEndTimeSeconds;

        return seconds * 1000L;
    }

    public boolean isLavaEnabled() {
        return enableLava;
    }

    public boolean isWaterEnabled() {
        return enableWater;
    }
}
