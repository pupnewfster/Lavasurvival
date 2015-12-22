package me.eddiep.minecraft.ls.game.impl;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.options.FloodOptions;
import me.eddiep.minecraft.ls.system.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.List;

public class Rise extends Gamemode {
    private int lastMinute, bonus, lvl = 1, sched = 0;
    private long lastEvent, duration, timeOut;
    private Score bonusScore, layersLeft;
    private boolean doubleReward;
    private Objective objective;

    @Override
    public void start() {
        if (getScoreboard().getObjective("game") == null)
            objective = getScoreboard().registerNewObjective("game", "dummy");
        else
            objective = getScoreboard().getObjective("game");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Prepare Time");
        bonusScore = objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Reward Bonus");
        layersLeft = objective.getScore(ChatColor.RED + "" + ChatColor.BOLD + "Layers Left");

        super.start();

        duration = getCurrentMap().getRiseOptions().generateRandomPrepareTime();
        timeOut = getCurrentMap().getRiseOptions().generateRandomRiseTime();
        globalMessage("The current gamemode is " + ChatColor.RED + ChatColor.BOLD + "RISE");
        globalMessage("The " + (LAVA ? "lava" : "water") + " will rise every " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(timeOut));
        globalMessage("You have " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration) + ChatColor.RESET + " to prepare!");
        lastEvent = System.currentTimeMillis();
        lastMinute = 0;
        bonus = Gamemode.RANDOM.nextInt(80) + 50;
        bonusScore.setScore(bonus);
        layersLeft.setScore(getCurrentMap().getHeight());

        Gamemode.getPlayerListener().survival = false;
        doubleReward = Math.random() < 0.25;


        if (doubleReward) {
            globalMessage("" + ChatColor.GREEN + ChatColor.BOLD + "All rewards this round are doubled!");
            globalMessage("but.." + ChatColor.RED + ChatColor.BOLD + "THE PREPARE TIME HAS BEEN CUT IN HALF");

            duration *= 0.5;
        }
        /*if (Gamemode.getPlayerListener().survival)
            globalMessage("The building style will be " + ChatColor.RED + "" + ChatColor.BOLD + "SURVIVAL STYLE");
        else
            globalMessage("The building style will be " + ChatColor.RED + "" + ChatColor.BOLD + "CLASSIC STYLE");*/
    }

    @Override
    protected void setIsLava(FloodOptions options) {
        super.setIsLava(getCurrentMap().getRiseOptions());
    }

    @Override
    public void playerJoin(Player player) {
        super.playerJoin(player);
        bonus += Gamemode.RANDOM.nextInt(10);
        bonusScore.setScore(bonus);
    }

    @Override
    public void endRound() {
        objective.unregister();
        objective = null;
        Bukkit.getScheduler().cancelTask(sched);
        super.endRound();
    }

    @Override
    public void onTick() {
        if (objective == null)
            return;

        long since = System.currentTimeMillis() - lastEvent;

        int minutes = (int) (((duration - since) / 1000) / 60);
        int seconds = (int) (((duration - since) / 1000) % 60);

        String time = (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);

        if (isRoundEnding()) {
            objective.setDisplayName("Round Ends In: " + ChatColor.BOLD + time);
            return;
        }
        objective.setDisplayName((!super.poured ? "Prepare Time: " : "Next Pour: ") + ChatColor.BOLD + time);

        if (!super.poured && since < duration) {
            int nextMinute = (int) Math.floor((since / 1000.0) / 60.0);
            if (nextMinute != lastMinute) {
                lastMinute = nextMinute;

                List<Location> locations = getCurrentMap().getLavaOptions().getSpawnLocation(0, lvl - getCurrentMap().getHeight(), 0);
                getCurrentWorld().strikeLightningEffect(locations.get(RANDOM.nextInt(locations.size()))); //Changed to just effect not to kill unknowing player nearby
                globalMessage("The " + (LAVA ? "lava" : "water") + " will rise in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration - since));
            }
        } else if (!super.poured) {
            super.poured = true;
            globalMessage(ChatColor.DARK_RED + "Here comes the " + (LAVA ? "lava" : "water") + "!");

            duration = timeOut; //The duration will not change
            objective.setDisplayName("Time Till Next Pour");
            pourAndAdvance(timeOut / 1000L);
        }
    }

    private void liquidUp(final long time) {
        sched = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
            @Override
            public void run() {
                pourAndAdvance(time);
            }
        }, 20 * time);
    }

    private void pourAndAdvance(long time) {
        List<Location> locs = getCurrentMap().getLavaOptions().getSpawnLocation(0, lvl - getCurrentMap().getHeight(), 0);

        int highestCurrentY = locs.get(0).getBlockY();
        for (Location loc : locs) {
            if (loc.getBlockY() > highestCurrentY)
                highestCurrentY = loc.getBlockY();
        }

        //final Location loc = getCurrentMap().getLavaSpawnAsLocation(0, lvl - getCurrentMap().getHeight(), 0);

        int lavaY = getCurrentMap().getLavaOptions().getHighestLocation().getBlockY();

        if (highestCurrentY > lavaY) { //If we have passed the original lava spawn, that means the previous pour was the last one
            if (!isRoundEnding()) {
                lastEvent = System.currentTimeMillis(); //Set the last event to now
                duration = getCurrentMap().getRiseOptions().generateRandomEndTime() / 1000L;
                super.endRoundIn(duration);
                duration *= 1000L;
            }
            return;
        }

        for (Location location : locs) {
            Lavasurvival.INSTANCE.getPhysicsHandler().forcePlaceClassicBlockAt(location, getMat());
        }


        lastEvent = System.currentTimeMillis(); //Set the last event to now
        getCurrentWorld().strikeLightningEffect(locs.get(RANDOM.nextInt(locs.size()))); //Actions are better than words :3

        lvl += getCurrentMap().getRiseOptions().getLayerCount();
        layersLeft.setScore(lavaY - highestCurrentY);
        if (highestCurrentY <= lavaY)
            liquidUp(time); //Only advance up if we are still less than the actual lava spawn or if we are at the lava spawn (the next check will end the game, see above)
    }

    @Override
    public double calculateReward(Player player, int blockCount) {
        double multiplier = 1.0;//In case we want a triple reward
        if (doubleReward)
            multiplier = 2.0;
        return (super.getDefaultReward(player, blockCount) + bonus) * multiplier;
    }

    public boolean isRoundEnding() {
        return isEnding;
    }
}