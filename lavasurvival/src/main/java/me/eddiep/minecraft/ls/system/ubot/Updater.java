package me.eddiep.minecraft.ls.system.ubot;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.ubot.UBot;
import me.eddiep.ubot.module.UpdateNotifier;
import me.eddiep.ubot.utils.Schedule;
import me.eddiep.ubot.utils.UpdateType;

import java.util.concurrent.Callable;

public class Updater implements UpdateNotifier {
    @Override
    public void onPreCheck(UBot uBot) {

    }

    @Override
    public Schedule<UpdateType> shouldBuild(UpdateType updateType, UBot uBot) {
        return Schedule.now(); //Always build
    }

    @Override
    public Schedule<UpdateType> shouldPatch(UpdateType updateType, UBot uBot) {
        return Schedule.now();
    }

    @Override
    public void patchComplete(UpdateType updateType, UBot uBot) {
        Lavasurvival.log("Update downloaded, restart has been queued");
        Gamemode.restartNextGame("lobby");
        Lavasurvival.INSTANCE.stopUbot();
    }

    @Override
    public void init() {

    }

    @Override
    public void deinit() {

    }
}
