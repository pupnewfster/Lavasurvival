package me.eddiep.minecraft.ls.game.options;

import me.eddiep.minecraft.ls.game.LavaMap;

public class FusionOptions extends FloodOptions {
    private int minRiseTimeSeconds = 15, maxRiseTimeSeconds = 30, layerCount = 1, alternateDistance = 0;//alternate every other layer

    public static FusionOptions defaults(LavaMap owner, RiseOptions options) {
        FusionOptions foptions = new FusionOptions(owner);
        foptions.minRiseTimeSeconds = options.minRiseTimeSeconds;
        foptions.maxRiseTimeSeconds = options.maxRiseTimeSeconds;
        foptions.layerCount = options.layerCount;
        return foptions;
    }

    FusionOptions(LavaMap owner) { super(owner); }

    public long generateRandomFusionTime() {
        int seconds = RANDOM.nextInt(this.maxRiseTimeSeconds - this.minRiseTimeSeconds) + this.minRiseTimeSeconds;
        return seconds * 1000L;
    }

    public int getLayerCount() {
        return this.layerCount;
    }

    public int getAlternateDistance() {
        return this.alternateDistance;
    }
}