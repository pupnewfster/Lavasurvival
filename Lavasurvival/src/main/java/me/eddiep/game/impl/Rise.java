package me.eddiep.game.impl;

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
    private int lastMinute;
    private int bonus;
    private Objective objective;
    private Score bonusScore;
    private boolean doubleReward;
    private int lvl = 1;
    private int sched = 0;

    @Override
    public void start(boolean lava) {
        super.start(lava);
        duration = Gamemode.RANDOM.nextInt(240000) + 180000;
        globalMessage("The " + (LAVA ? "lava" : "water") + " will pour in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration));
        gameStart = System.currentTimeMillis();
        lastMinute = 0;

        objective = getScoreboard().registerNewObjective("game", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName((LAVA ? "Lava" : "Water") + "Pour");
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
        objective.setDisplayName((LAVA ? "Lava" : "Water") + " Pour: " + ChatColor.BOLD + time);

        if (!super.poured && since < duration) {
            int nextMinute = (int) Math.floor((since / 1000.0) / 60.0);
            if (nextMinute != lastMinute) {
                lastMinute = nextMinute;

                Location lavaPoint = getCurrentMap().getLavaSpawnAsLocation();
                getCurrentWorld().strikeLightningEffect(lavaPoint);//Changed to just effect not to kill unknowing player nearby
                globalMessage("The " + (LAVA ? "lava" : "water") + " will pour in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration - since));
            }
        } else if (!super.poured) {
            super.poured = true;
            globalMessage(ChatColor.DARK_RED + "Here comes the " + (LAVA ? "lava" : "water") + "!");

            gameStart = System.currentTimeMillis();
            getCurrentMap().getLavaSpawnAsLocation().getBlock().setType(getMat());
            duration = Gamemode.RANDOM.nextInt(240000) + 180000;
            objective.setDisplayName("Time Till Round End");
            liquidUp(16);
        } else {
            if (since < duration) {
                int nextMinute = (int) Math.floor((since / 1000.0) / 60.0);
                if (nextMinute != lastMinute) {
                    lastMinute = nextMinute;

                    Location lavaPoint = getCurrentMap().getLavaSpawnAsLocation();
                    getCurrentWorld().strikeLightningEffect(lavaPoint);
                    globalMessage("The round will end in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration - since));
                }
            } else
                endRound();
        }
    }

    private void liquidUp(final int time) {
        sched = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
            @Override
            public void run() {
                Location l = getCurrentMap().getLavaSpawnAsLocation();
                final Location loc = new Location(l.getWorld(), l.getX(), l.getY() + lvl, l.getZ());
                if(loc.getBlock().getType().equals(Material.AIR)) {
                    loc.getBlock().setType(getMat());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getPluginManager().callEvent(new BlockPhysicsEvent(loc.getBlock(), 0));
                        }
                    }, LAVA ? 30 : 5);
                    lvl++;
                    liquidUp(time);
                }
            }
        }, 20 * time);
    }

    @Override
    public double calculateReward(OfflinePlayer player) {
        double multiplier = 1.0;//In case we want a triple reward
        if (doubleReward)
            multiplier = 2.0;
        return (super.getDefaultReward(player) + bonus) * multiplier;
    }
}