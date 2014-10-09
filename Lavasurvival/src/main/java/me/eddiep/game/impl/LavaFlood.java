package me.eddiep.game.impl;

import com.mysql.jdbc.TimeUtil;
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

public class LavaFlood extends Gamemode {
    private long gameStart;
    private long duration;
    private int lastMinute;
    private int bonus;
    private Objective objective;
    private Score bonusScore;
    @Override
    public void start() {
        super.start();

        duration = Gamemode.RANDOM.nextInt(240000) + 180000;
        globalMessage("The lava will pour in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration));
        gameStart = System.currentTimeMillis();
        lastMinute = 0;

        objective = getScoreboard().registerNewObjective("game", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Lava Pour");
        bonusScore = objective.getScore(ChatColor.BOLD + "" + ChatColor.GOLD + "Reward Bonus");
        bonus = Gamemode.RANDOM.nextInt(300) + 50;
        bonusScore.setScore(bonus);

        Gamemode.getPlayerListener().survival = RANDOM.nextBoolean();

        if (Gamemode.getPlayerListener().survival) {
            globalMessage("The building style will be " + ChatColor.BOLD + "" + ChatColor.RED + "SURVIVAL STYLE");
        } else {
            globalMessage("The building style will be " + ChatColor.BOLD + "" + ChatColor.RED + "CLASSIC STYLE");
        }
    }

    @Override
    public void playerJoin(Player player) {
        super.playerJoin(player);

        bonus += Gamemode.RANDOM.nextInt(10);

        if (Gamemode.RANDOM.nextInt(500) < 100) {
            bonus += Gamemode.RANDOM.nextInt(100) + 50;
        }

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
        objective.setDisplayName("Lava Pour: " + ChatColor.BOLD + time);

        if (!super.poured && since < duration) {
            int nextMinute = (int)Math.floor((since / 1000.0) / 60.0);
            if (nextMinute != lastMinute) {
                lastMinute = nextMinute;

                Location lavaPoint = getCurrentMap().getLavaSpawnAsLocation();
                getCurrentWorld().strikeLightning(lavaPoint);
                globalMessage("The lava will pour in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration - since));
            }
        } else if (!super.poured) {
            super.poured = true;
            globalMessage(ChatColor.DARK_RED + "Here comes the lava!");

            gameStart = System.currentTimeMillis();
            getCurrentWorld().getBlockAt(getCurrentMap().getLavaSpawnAsLocation()).setType(Material.STATIONARY_LAVA);
            duration = Gamemode.RANDOM.nextInt(240000) + 180000;
            globalMessage("The round will end in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration));
            objective.setDisplayName("Time Till Round End");
        } else {
            if (since < duration) {
                int nextMinute = (int)Math.floor((since / 1000.0) / 60.0);
                if (nextMinute != lastMinute) {
                    lastMinute = nextMinute;

                    Location lavaPoint = getCurrentMap().getLavaSpawnAsLocation();
                    getCurrentWorld().strikeLightning(lavaPoint);
                    globalMessage("The round will end in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration - since));
                }
            } else {
                endRound();
            }
        }
    }

    @Override
    public double calculateReward(OfflinePlayer player) {
        return 100.0; //TODO Get amount from classic version
    }
}
