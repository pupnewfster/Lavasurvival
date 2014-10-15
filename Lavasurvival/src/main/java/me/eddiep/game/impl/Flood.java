package me.eddiep.game.impl;

import me.eddiep.game.Gamemode;
import me.eddiep.system.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class Flood extends Gamemode {
    private long gameStart;
    private long duration;
    private int lastMinute;
    private int bonus;
    private Objective objective;
    private Score bonusScore;
    private boolean doubleReward;

    @Override
    public void start(boolean lava) {
        super.start(lava);
        duration = Gamemode.RANDOM.nextInt(240000) + 180000;
        globalMessage("The " + (Gamemode.LAVA ? "lava" : "water") + " will pour in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration));
        gameStart = System.currentTimeMillis();
        lastMinute = 0;

        objective = getScoreboard().registerNewObjective("game", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName((Gamemode.LAVA ? "Lava" : "Water") + "Pour");
        bonusScore = objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Reward Bonus");
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

        bonus += Gamemode.RANDOM.nextInt(10);

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
        objective.setDisplayName((Gamemode.LAVA ? "Lava" : "Water") + " Pour: " + ChatColor.BOLD + time);

        if (!super.poured && since < duration) {
            int nextMinute = (int) Math.floor((since / 1000.0) / 60.0);
            if (nextMinute != lastMinute) {
                lastMinute = nextMinute;

                Location lavaPoint = getCurrentMap().getLavaSpawnAsLocation();
                getCurrentWorld().strikeLightningEffect(lavaPoint);//Changed to just effect not to kill unknowing player nearby
                globalMessage("The " + (Gamemode.LAVA ? "lava" : "water") + " will pour in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration - since));
            }
        } else if (!super.poured) {
            super.poured = true;
            globalMessage(ChatColor.DARK_RED + "Here comes the " + (Gamemode.LAVA ? "lava" : "water") + "!");

            gameStart = System.currentTimeMillis();
            getCurrentWorld().getBlockAt(getCurrentMap().getLavaSpawnAsLocation()).setType(Gamemode.LAVA ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER);
            duration = Gamemode.RANDOM.nextInt(240000) + 180000;
            globalMessage("The round will end in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration));
            objective.setDisplayName("Time Till Round End");
        } else {
            if (since < duration) {
                int nextMinute = (int) Math.floor((since / 1000.0) / 60.0);
                if (nextMinute != lastMinute) {
                    lastMinute = nextMinute;

                    Location lavaPoint = getCurrentMap().getLavaSpawnAsLocation();
                    getCurrentWorld().strikeLightning(lavaPoint);
                    globalMessage("The round will end in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration - since));
                }
            } else
                endRound();
        }
    }

    @Override
    public double calculateReward(OfflinePlayer player) {
        if (doubleReward)
            return (super.getDefaultReward(player) + bonus) * 2.0;

        return super.getDefaultReward(player) + bonus;
    }
}