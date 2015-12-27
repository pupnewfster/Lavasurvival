package me.eddiep.minecraft.ls.game.options;

import me.eddiep.minecraft.ls.game.LavaMap;

public class RiseOptions extends FloodOptions {
    private int minRiseTimeSeconds = 15, maxRiseTimeSeconds = 30, layerCount = 1;

    public static RiseOptions defaults(LavaMap owner) {
        return new RiseOptions(owner);
    }

    RiseOptions(LavaMap owner) { super(owner); }

    public long generateRandomRiseTime() {
        int seconds = RANDOM.nextInt(maxRiseTimeSeconds - minRiseTimeSeconds) + minRiseTimeSeconds;
        return seconds * 1000L;
    }


    public int getLayerCount() {
        return layerCount;
    }
}