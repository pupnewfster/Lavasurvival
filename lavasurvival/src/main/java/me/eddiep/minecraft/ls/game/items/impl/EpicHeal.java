package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.Intrinsic;

public class EpicHeal extends Heal {
    @Override
    public String name() {
        return "Full Heal";
    }

    @Override
    public Intrinsic intrinsic() {
        return Intrinsic.EPIC;
    }

    @Override
    public int getPrice() {
        return 2000;
    }

    @Override
    protected double getPercent() {
        return 1;
    }
}