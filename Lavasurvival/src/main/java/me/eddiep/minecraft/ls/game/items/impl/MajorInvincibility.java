package me.eddiep.minecraft.ls.game.items.impl;

public class MajorInvincibility extends Invincibility{
    @Override
    public int duration() {
        return 20;
    }

    @Override
    public String name() {
        return "Major Invincibility";
    }

    @Override
    public int getPrice() {
        return 1400;
    }
}
