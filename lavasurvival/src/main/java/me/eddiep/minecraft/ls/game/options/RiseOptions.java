package me.eddiep.minecraft.ls.game.options;

import me.eddiep.minecraft.ls.game.LavaMap;

public class RiseOptions extends FloodOptions {
    final int minRiseTimeSeconds = 15;
    final int maxRiseTimeSeconds = 30;
    final int layerCount = 1;

    public static RiseOptions defaults(LavaMap owner) {
        return new RiseOptions(owner);
    }

    private RiseOptions(LavaMap owner) {
        super(owner);
    }

    public long generateRandomRiseTime() {
        return (RANDOM.nextInt(this.maxRiseTimeSeconds - this.minRiseTimeSeconds) + this.minRiseTimeSeconds) * 1000L;
    }

    public int getLayerCount() {
        return this.layerCount;
    }
}