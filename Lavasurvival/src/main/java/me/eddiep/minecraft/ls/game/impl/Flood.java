package me.eddiep.minecraft.ls.game.impl;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.system.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.List;

public class Flood extends Gamemode {
    private long gameStart, duration;
    private int lastMinute, bonus;
    private boolean doubleReward;
    private Objective objective;
    private Score bonusScore;
    private List<Location> lavaPoints;

    @Override
    public void start() {
        if (getScoreboard().getObjective("game") == null)
            objective = getScoreboard().registerNewObjective("game", "dummy");
        else
            objective = getScoreboard().getObjective("game");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName((LAVA ? "Lava" : "Water") + "Pour");
        bonusScore = objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Reward Bonus");

        super.start();

        duration = getCurrentMap().getFloodOptions().generateRandomPrepareTime();
        lavaPoints = getCurrentMap().getLavaOptions().getSpawnLocations();
        //duration = Gamemode.RANDOM.nextInt(180000) + 300000;
        globalMessage("The " + (LAVA ? "lava" : "water") + " will pour in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration));
        gameStart = System.currentTimeMillis();
        lastMinute = 0;

        bonus = Gamemode.RANDOM.nextInt(80) + 50;
        bonusScore.setScore(bonus);

        Gamemode.getPlayerListener().survival = false;
        doubleReward = Math.random() < 0.25;


        if (doubleReward) {
            globalMessage("" + ChatColor.GREEN + ChatColor.BOLD + "All rewards this round are doubled!");
            globalMessage("but.." + ChatColor.RED + ChatColor.BOLD + "THE TIME HAS BEEN CUT IN HALF");
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
        bonus += Gamemode.RANDOM.nextInt(70) + 20;
        bonusScore.setScore(bonus);
    }

    @Override
    public void endRound() {
        objective.unregister();
        objective = null;
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

        if (!super.poured) {
            objective.setDisplayName((LAVA ? "Lava" : "Water") + " Pour: " + ChatColor.BOLD + time);
        } else {
            objective.setDisplayName("Round Ends In: " + ChatColor.BOLD + time);
        }


        if (!super.poured && since < duration) {
            int nextMinute = (int) Math.floor((since / 1000.0) / 60.0);
            if (nextMinute != lastMinute) {
                lastMinute = nextMinute;

                getCurrentWorld().strikeLightningEffect(lavaPoints.get(RANDOM.nextInt(lavaPoints.size()))); //Changed to just effect not to kill unknowing player nearby
                globalMessage("The " + (LAVA ? "lava" : "water") + " will pour in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration - since));
            }
        } else if (!super.poured) {
            super.poured = true;
            globalMessage(ChatColor.DARK_RED + "Here comes the " + (LAVA ? "lava" : "water") + "!");

            gameStart = System.currentTimeMillis();

            for (Location location : lavaPoints) {
                Lavasurvival.INSTANCE.getPhysicsHandler().forcePlaceClassicBlockAt(location, getMat());
            }

            duration = getCurrentMap().getFloodOptions().generateRandomEndTime();
            objective.setDisplayName("Round Ends In: " + ChatColor.BOLD + time);
        } else {
            if (since < duration) {
                int nextMinute = (int) Math.floor((since / 1000.0) / 60.0);
                if (nextMinute != lastMinute) {
                    lastMinute = nextMinute;

                    getCurrentWorld().strikeLightningEffect(lavaPoints.get(RANDOM.nextInt(lavaPoints.size()))); //Changed to just effect not to kill unknowing player nearby
                    globalMessage("The round will end in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration - since));
                }
            } else
                endRound();
        }
    }

    @Override
    public double calculateReward(Player player, int blockCount) {
        double multiplier = 1.0;//In case we want a triple reward
        if (doubleReward)
            multiplier = 2.0;
        return (super.getDefaultReward(player, blockCount) + bonus) * multiplier;
    }
}