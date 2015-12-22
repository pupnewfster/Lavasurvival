package me.eddiep.minecraft.ls.game.options;

public class TimeOptions extends BaseOptions {
    private long startTimeTick = 0;
    private long dayLength = 24000;

    public static TimeOptions defaults() {
        return new TimeOptions();
    }

    TimeOptions() { }

    public long getStartTimeTick() {
        return startTimeTick;
    }

    public long getDayLength() {
        return dayLength;
    }

    public double getMultiplier() {
        return 24000.0 / (double)dayLength;
    }
}
