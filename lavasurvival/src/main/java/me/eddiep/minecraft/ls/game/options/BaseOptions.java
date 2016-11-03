package me.eddiep.minecraft.ls.game.options;

import java.util.Random;

public class BaseOptions {
    protected static final Random RANDOM = new Random();

    private boolean isEnabled = true;

    public static BaseOptions defaults() {
        return new BaseOptions();
    }

    BaseOptions() {
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}