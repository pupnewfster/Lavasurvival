package me.eddiep.minecraft.ls.game.items.impl;

public class MajorHeal extends Heal {
    @Override
    public double getPercent() {
        return 0.5;
    }

    @Override
    public String name() {
        return "Major Heal";
    }

    @Override
    public int getPrice() {
        return 900;
    }
}
