package me.eddiep.minecraft.ls.game.items.impl;

public class MinorInvincibility extends Invincibility {
    @Override
    public int duration() {
        return 10;
    }

    @Override
    public String name() {
        return "Minor Invincibility";
    }
}
