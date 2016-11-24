package me.eddiep.minecraft.ls.game.options;

public class TimeOptions extends BaseOptions {
    @SuppressWarnings("FieldCanBeLocal")
    private final long startTimeTick = 0;
    private final long dayLength = 24000;

    public static TimeOptions defaults() {
        return new TimeOptions();
    }

    private TimeOptions() {
    }

    public long getStartTimeTick() {
        return startTimeTick;
    }

    @SuppressWarnings("unused")
    public long getDayLength() {
        return dayLength;
    }

    public double getMultiplier() {
        return 24000.0 / (double) dayLength;
    }
}