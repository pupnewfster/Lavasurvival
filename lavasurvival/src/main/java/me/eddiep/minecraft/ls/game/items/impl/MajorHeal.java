package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.Intrinsic;

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


    @Override
    public Intrinsic intrinsic() {
        return Intrinsic.UNCOMMON;
    }
}