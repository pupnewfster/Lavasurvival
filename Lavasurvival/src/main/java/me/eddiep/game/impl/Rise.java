package me.eddiep.game.impl;

import me.eddiep.ClassicPhysics;
import me.eddiep.Lavasurvival;
import me.eddiep.game.Gamemode;
import me.eddiep.system.TimeUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class Rise extends Gamemode {
    private long gameStart;
    private long duration;


    private long timeOut;

    private int lastMinute;
    private int bonus;
    private Objective objective;
    private Score bonusScore;
    private boolean doubleReward;
    private int lvl = 1;
    private int sched = 0;

    @Override
    public void start() {
        super.start();
        duration = Gamemode.RANDOM.nextInt(240000) + 180000;
        timeOut = Gamemode.RANDOM.nextInt(45000) + 30000;
        globalMessage("The current gamemode is " + ChatColor.RED + ChatColor.BOLD + "RISE");
        globalMessage("The " + (LAVA ? "lava" : "water") + " will rise every " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(timeOut));
        globalMessage("You have " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration) + ChatColor.RESET + " to prepare!");
        gameStart = System.currentTimeMillis();
        lastMinute = 0;

        objective = getScoreboard().registerNewObjective("game", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Prepare Time");
        bonusScore = objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Reward Bonus");
        bonus = Gamemode.RANDOM.nextInt(80) + 50;
        bonusScore.setScore(bonus);

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

        long since = System.currentTimeMillis() - gameStart;

        int minutes = (int) (((duration - since) / 1000) / 60);
        int seconds = (int) (((duration - since) / 1000) % 60);

        String time = (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
        objective.setDisplayName((!super.poured ? "Prepare Time: " : "Next Pour: ") + ChatColor.BOLD + time);

        if (!super.poured && since < duration) {
            int nextMinute = (int) Math.floor((since / 1000.0) / 60.0);
            if (nextMinute != lastMinute) {
                lastMinute = nextMinute;

                Location lavaPoint = getCurrentMap().getLavaSpawnAsLocation();
                getCurrentWorld().strikeLightningEffect(lavaPoint);//Changed to just effect not to kill unknowing player nearby
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
        final Location loc = getCurrentMap().getLavaSpawnAsLocation(0, -(getCurrentMap().getHeight()) + lvl, 0);

        if (loc.getBlockY() > getCurrentMap().getLavaY()) { //If we have passed the original lava spawn, that means the previous pour was the last one
            super.endRound(); //Thus, we should end the game
            return;
        }

        ClassicPhysics.placeClassicBlockAt(loc, getMat());

        gameStart = System.currentTimeMillis(); //Set the last event to now
        getCurrentWorld().strikeLightningEffect(loc); //Actions are better than words :3

        Bukkit.getPluginManager().callEvent(new BlockPhysicsEvent(loc.getBlock(), 0)); //Force a physics check

        lvl++;
        if (loc.getBlockY() <= getCurrentMap().getLavaY()) liquidUp(time); //Only advance up if we are still less than the actual lava spawn or if we are at the lava spawn (the next check will end the game, see above)
    }

    @Override
    public double calculateReward(OfflinePlayer player) {
        double multiplier = 1.0;//In case we want a triple reward
        if (doubleReward)
            multiplier = 2.0;
        return (super.getDefaultReward(player) + bonus) * multiplier;
    }
}