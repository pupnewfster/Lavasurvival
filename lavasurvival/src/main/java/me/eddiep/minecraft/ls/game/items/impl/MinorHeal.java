package me.eddiep.minecraft.ls.game.items.impl;

public class MinorHeal extends Heal {
    @Override
    public double getPercent() {
        return 0.25;
    }

    @Override
    public String name() {
        return "Minor Heal";
    }

    @Override
    public int getPrice() {
        return 500;
    }
}
