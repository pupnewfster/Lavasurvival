package me.eddiep.minecraft.ls.game.options;

import java.util.Random;

public class BaseOptions {
    static final Random RANDOM = new Random();

    @SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
    private boolean isEnabled = true;

    @SuppressWarnings("unused")
    public static BaseOptions defaults() {
        return new BaseOptions();
    }

    BaseOptions() {
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }
}