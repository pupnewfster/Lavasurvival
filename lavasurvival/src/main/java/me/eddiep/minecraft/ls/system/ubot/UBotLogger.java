package me.eddiep.minecraft.ls.system.ubot;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.ubot.module.Logger;

@SuppressWarnings("unused")
class UBotLogger implements Logger {
    @Override
    public void log(String s) {
        Lavasurvival.log(s);
    }

    @Override
    public void warning(String s) {
        Lavasurvival.warn(s);
    }

    @Override
    public void init() {

    }

    @Override
    public void deinit() {

    }
}