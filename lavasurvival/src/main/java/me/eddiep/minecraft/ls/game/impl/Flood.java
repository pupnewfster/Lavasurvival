package me.eddiep.minecraft.ls.game.impl;

import me.eddiep.ClassicPhysics;
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
    public static final String TYPE = "Flood";

    private long gameStart, duration;
    private int lastMinute, bonus;
    private boolean doubleReward;
    private Objective objective;
    private Score bonusScore;
    private List<Location> lavaPoints;

    @Override
    public void onStart() {
        this.doubleReward = Math.random() < 0.25;
        this.objective = getScoreboard().getObjective("game") == null ? getScoreboard().registerNewObjective("game", "dummy") : getScoreboard().getObjective("game");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName("Lava Pour");
        this.bonusScore = this.objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Reward Bonus");
        this.type = TYPE;
        super.onStart();
        this.duration = getCurrentMap().getFloodOptions().generateRandomPrepareTime();
        this.lavaPoints = getCurrentMap().getFloodOptions().getSpawnLocations();
        globalMessage("The lava will pour in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(duration));
        this.gameStart = System.currentTimeMillis();
        this.lastMinute = 0;

        this.bonus = Gamemode.RANDOM.nextInt(80) + 50;
        this.bonusScore.setScore(this.bonus);

        if (this.doubleReward) {
            globalMessage("" + ChatColor.GREEN + ChatColor.BOLD + "All rewards this round are doubled!");
            globalMessage("but.." + ChatColor.RED + ChatColor.BOLD + "THE TIME HAS BEEN CUT IN HALF");
            this.duration *= 0.5;
        }
        /*if (Gamemode.getPlayerListener().survival)
            globalMessage("The building style will be " + ChatColor.RED + "" + ChatColor.BOLD + "SURVIVAL STYLE");
        else
            globalMessage("The building style will be " + ChatColor.RED + "" + ChatColor.BOLD + "CLASSIC STYLE");*/
    }

    @Override
    public void playerJoin(Player player) {
        super.playerJoin(player);
        this.bonus += Gamemode.RANDOM.nextInt(70) + 20;
        this.bonusScore.setScore(this.bonus);
    }

    @Override
    public void addToBonus(double takeOut) {
        this.bonus += takeOut;
        this.bonusScore.setScore(this.bonus);
    }

    @Override
    public boolean isRewardDoubled() {
        return doubleReward;
    }

    @Override
    public void endRound(boolean skipVote, boolean giveRewards) {
        if (this.objective != null)
            this.objective.unregister();
        this.objective = null;
        super.endRound(skipVote, giveRewards);
    }

    @Override
    public boolean isLocationNearLavaSpawn(Location location, int distance) {
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
        return this.lavaPoints.stream().anyMatch(l -> Math.abs(l.getBlockX() - x) < distance && Math.abs(l.getBlockY() - y) < distance && Math.abs(l.getBlockZ() - z) < distance);
    }

    private void setObjectiveDisplay(String display) {
        if (display == null)
            return;
        if (this.objective == null)
            this.objective = getScoreboard().getObjective("game") == null ? getScoreboard().registerNewObjective("game", "dummy") : getScoreboard().getObjective("game");
        try {
            this.objective.setDisplayName(display);
        } catch (IllegalStateException e) {
            //this.objective.unregister(); It's already unregistered
            this.objective = getScoreboard().registerNewObjective("game", "dummy");
            this.objective.setDisplayName(display);
        }
    }

    @Override
    public void onTick() {
        if (this.objective == null || hasEnded())
            return;
        long since = System.currentTimeMillis() - this.gameStart, dif = this.duration - since;
        int seconds = (int) ((dif) / 1000 % 60);
        String time = (int) ((dif) / 60000) + ":" + (seconds < 10 ? "0" + seconds : seconds);
        if (!super.poured)
            setObjectiveDisplay("Lava Pour: " + ChatColor.BOLD + time);
        else
            setObjectiveDisplay("Round Ends In: " + ChatColor.BOLD + time);
        if (super.poured) {
            if (since < this.duration) {
                int nextMinute = (int) since / 60000;
                if (nextMinute != this.lastMinute) {
                    this.lastMinute = nextMinute;
                    getCurrentWorld().strikeLightningEffect(this.lavaPoints.get(RANDOM.nextInt(this.lavaPoints.size()))); //Changed to just effect not to kill unknowing player nearby
                    globalMessage("The round will end in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(dif));
                }
            } else
                endRound();
        } else if (since < this.duration) {
            int nextMinute = (int) since / 60000;
            if (nextMinute != this.lastMinute) {
                this.lastMinute = nextMinute;
                getCurrentWorld().strikeLightningEffect(this.lavaPoints.get(RANDOM.nextInt(this.lavaPoints.size()))); //Changed to just effect not to kill unknowing player nearby
                globalMessage("The lava will pour in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(dif));
            }
        } else {
            super.poured = true;
            globalMessage(ChatColor.DARK_RED + "Here comes the lava!");
            this.gameStart = System.currentTimeMillis();
            this.lavaPoints.forEach(l -> ClassicPhysics.getPhysicsEngine().placeClassicBlock(l));
            this.duration = getCurrentMap().getFloodOptions().generateRandomEndTime();
            this.objective.setDisplayName("Round Ends In: " + ChatColor.BOLD + time);
        }
    }

    @Override
    public double calculateReward(Player player, int blockCount) {
        double multiplier = 1.0;//In case we want a triple reward
        if (this.doubleReward)
            multiplier = 2.0;
        return (super.getDefaultReward(player, blockCount) + this.bonus) * multiplier;
    }
}