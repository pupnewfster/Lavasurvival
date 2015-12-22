package me.eddiep.minecraft.ls.game.options;

public class BaseOptions {
    private boolean isEnabled = true;

    public static BaseOptions defaults() {
        return new BaseOptions();
    }

    BaseOptions() { }

    public boolean isEnabled() {
        return isEnabled;
    }
}
