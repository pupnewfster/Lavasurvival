package me.eddiep.minecraft.ls.game.options;

public class RiseOptions extends FloodOptions {

    private int minRiseTimeSeconds = 15;
    private int maxRiseTimeSeconds = 30;
    private int layerCount = 1;

    public static RiseOptions defaults() {
        return new RiseOptions();
    }

    RiseOptions() { }


    public long generateRandomRiseTime() {
        int seconds = RANDOM.nextInt(maxRiseTimeSeconds - minRiseTimeSeconds) + minRiseTimeSeconds;

        return seconds * 1000L;
    }


}
