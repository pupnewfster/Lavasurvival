package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.Intrinsic;

public class EpicInvincibility extends Invincibility {
    @Override
    public String name() {
        return "Epic Invincibility";
    }

    @Override
    public int getPrice() {
        return 2000;
    }

    @Override
    protected int duration() {
        return 60;
    }


    @Override
    public Intrinsic intrinsic() {
        return Intrinsic.EPIC;
    }
}
