package me.eddiep.minecraft.ls.system.ubot;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.ubot.UBot;
import me.eddiep.ubot.module.UpdateNotifier;
import me.eddiep.ubot.utils.Schedule;
import me.eddiep.ubot.utils.UpdateType;
import org.bukkit.Bukkit;

import java.util.Calendar;
import java.util.GregorianCalendar;
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
        if (updateType == UpdateType.BUGFIX) {

            Lavasurvival.log("BugFix Patch detected! Will patch when server is empty");
            //Only patch bugs when the server is empty
            return Schedule.when(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return Bukkit.getOnlinePlayers().size() == 0;
                }
            });
        } else if (updateType == UpdateType.MINOR) {

            Lavasurvival.log("Minor Patch detected! Will patch when server is empty OR at midnight");
            //Patch minor updates when the server is empty or when it's midnight
            Calendar date = new GregorianCalendar();
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            date.add(Calendar.DAY_OF_MONTH, 1);

            return Schedule.combind(
                    Schedule.when(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return Bukkit.getOnlinePlayers().size() == 0;
                        }
                    }),
                    Schedule.at(date.getTime())
            );
        } else {
            Lavasurvival.log(updateType.name() + " Patch detected! Will patch now");
            return Schedule.now();
        }
    }

    @Override
    public void patchComplete(UpdateType updateType, UBot uBot) {
        if (updateType == UpdateType.URGENT) {
            Lavasurvival.log("Urgent patch applied. Will restart in 20 seconds");
            Lavasurvival.globalMessage("An urgent update needs to be patched!");
            Lavasurvival.globalMessage("The server will restart in 20 seconds.");
            Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                @Override
                public void run() {
                    Gamemode.restartNextGame("lobby");
                    if (!Gamemode.getCurrentGame().hasEnded()) {
                        Gamemode.getCurrentGame().endRound(true, false);
                    }
                }
            }, 20 * 20);
            Lavasurvival.INSTANCE.stopUbot();
            return;
        }

        Lavasurvival.log("Patch applied, restart has been queued for next game");
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
