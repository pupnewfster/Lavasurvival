package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.Intrinsic;

public class MajorInvincibility extends Invincibility {
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


    @Override
    public Intrinsic intrinsic() {
        return Intrinsic.UNCOMMON;
    }
}