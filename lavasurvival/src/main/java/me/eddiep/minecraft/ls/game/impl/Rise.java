package me.eddiep.minecraft.ls.game.impl;

import me.eddiep.ClassicPhysics;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.options.FloodOptions;
import me.eddiep.minecraft.ls.system.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.List;

public class Rise extends Gamemode {
    public static final String TYPE = "Rise";

    private int lastMinute, bonus, lavaY, highestCurrentY = 0, layerCount;
    private long lastEvent, duration, timeOut;
    private Score bonusScore, layersLeft;
    private boolean doubleReward;
    private Objective objective = null;
    private BukkitRunnable upTask;
    private List<Location> locations;

    @Override
    public void onStart() {
        this.doubleReward = Math.random() < 0.25;
        if (getScoreboard().getObjective("game") == null)
            this.objective = getScoreboard().registerNewObjective("game", "dummy");
        else
            this.objective = getScoreboard().getObjective("game");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName("Prepare Time");
        this.bonusScore = this.objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Reward Bonus");
        this.layersLeft = this.objective.getScore(ChatColor.RED + "" + ChatColor.BOLD + "Layers Left");
        this.type = TYPE;
        super.onStart();
        this.duration = getCurrentMap().getRiseOptions().generateRandomPrepareTime();
        this.timeOut = getCurrentMap().getRiseOptions().generateRandomRiseTime();
        globalMessage("The current gamemode is " + ChatColor.RED + ChatColor.BOLD + "RISE");
        globalMessage("The " + (LAVA ? "lava" : "water") + " will rise every " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(timeOut));
        globalMessage("You have " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(this.duration) + ChatColor.RESET + " to prepare!");
        this.lastEvent = System.currentTimeMillis();
        this.lastMinute = 0;
        this.bonus = Gamemode.RANDOM.nextInt(80) + 50;
        this.bonusScore.setScore(this.bonus);
        this.layersLeft.setScore(getCurrentMap().getHeight());
        if (this.doubleReward)
            this.objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Double Reward!");
        Gamemode.getPlayerListener().survival = false;
        if (this.doubleReward) {
            globalMessage("" + ChatColor.GREEN + ChatColor.BOLD + "All rewards this round are doubled!");
            globalMessage("but.." + ChatColor.RED + ChatColor.BOLD + "THE PREPARE TIME HAS BEEN CUT IN HALF");
            this.duration *= 0.5;
        }
        this.lavaY = getCurrentMap().getRiseOptions().getHighestLocation().getBlockY();
        this.layerCount = getCurrentMap().getRiseOptions().getLayerCount();
        this.upTask = new BukkitRunnable() {
            @Override
            public void run() {
                pourAndAdvance();
            }
        };
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
        this.bonus += Gamemode.RANDOM.nextInt(10);
        this.bonusScore.setScore(this.bonus);
    }

    @Override
    public void addToBonus(double takeOut) {
        this.bonus += takeOut;
        this.bonusScore.setScore(this.bonus);
    }

    @Override
    public boolean isRewardDoubled() {
        return this.doubleReward;
    }

    @Override
    public void endRound() {
        if (this.objective != null)
            this.objective.unregister();
        try {
            this.upTask.cancel();
        } catch (Exception ignored) {//Not running
        }
        this.locations = null;
        super.endRound();
    }

    private void setObjectiveDisplay(String display) {
        if (display == null || this.objective == null)
            return;
        try {
            this.objective.setDisplayName(display);
        } catch (IllegalStateException e) {
            if (getScoreboard().getObjective("game") == null)
                this.objective = getScoreboard().registerNewObjective("game", "dummy");
            else
                this.objective = getScoreboard().getObjective("game");
            this.objective.setDisplayName(display);
        }
    }

    @Override
    public void onTick() {
        if (this.objective == null)
            return;
        long since = System.currentTimeMillis() - this.lastEvent, dif = this.duration - since;
        int seconds = (int) (dif / 1000 % 60);
        String time = (int) (dif / 60000) + ":" + (seconds < 10 ? "0" + seconds : seconds);
        if (isRoundEnding()) { //TODO double check this shows the correct amount
            setObjectiveDisplay("Round Ends In: " + ChatColor.BOLD + time);
        } else if (super.poured)
            setObjectiveDisplay("Next Pour: " + ChatColor.BOLD + time);
        else {
            setObjectiveDisplay("Prepare Time: " + ChatColor.BOLD + time);
            if (since < this.duration) {
                int nextMinute = (int) since / 60000;
                if (nextMinute != this.lastMinute) {
                    this.lastMinute = nextMinute;
                    this.locations = getCurrentMap().getRiseOptions().getSpawnLocation(0, 1 - getCurrentMap().getHeight(), 0);
                    getCurrentWorld().strikeLightningEffect(this.locations.get(RANDOM.nextInt(this.locations.size()))); //Changed to just effect not to kill unknowing player nearby
                    for (Location loc : this.locations)
                        if (loc.getBlockY() > this.highestCurrentY)
                            this.highestCurrentY = loc.getBlockY();
                    globalMessage("The " + (LAVA ? "lava" : "water") + " will rise in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(dif));
                }
            } else {
                super.poured = true;
                globalMessage(ChatColor.DARK_RED + "Here comes the " + (LAVA ? "lava" : "water") + "!");
                this.duration = this.timeOut; //The duration will not change
                this.objective.setDisplayName("Time Till Next Pour");
                this.upTask.runTaskTimer(Lavasurvival.INSTANCE, 0, 20 * this.timeOut / 1000L);
            }
        }
    }

    private void pourAndAdvance() {
        if (this.highestCurrentY > this.lavaY) { //If we have passed the original lava spawn, that means the previous pour was the last one
            try {
                this.upTask.cancel();
            } catch (Exception ignored) {//Not running
            }
            if (!isRoundEnding()) {
                this.lastEvent = System.currentTimeMillis(); //Set the last event to now
                this.duration = getCurrentMap().getRiseOptions().generateRandomEndTime();
                super.endRoundIn(this.duration / 1000L);
            }
            return;
        }
        getCurrentWorld().strikeLightningEffect(this.locations.get(RANDOM.nextInt(this.locations.size()))); //Actions are better than words :3
        this.locations.forEach(l -> {
            ClassicPhysics.INSTANCE.getPhysicsHandler().forcePlaceClassicBlockAt(l, getMat());
            l.add(0, this.layerCount, 0);
        });
        this.highestCurrentY += this.layerCount;
        this.lastEvent = System.currentTimeMillis(); //Set the last event to now
        if (this.lavaY - this.highestCurrentY < 0)
            this.layersLeft.setScore(0);
        else
            this.layersLeft.setScore(this.lavaY - this.highestCurrentY);
    }

    @Override
    public double calculateReward(Player player, int blockCount) {
        double multiplier = 1.0;//In case we want a triple reward
        if (this.doubleReward)
            multiplier = 2.0;
        return (super.getDefaultReward(player, blockCount) + this.bonus) * multiplier;
    }

    private boolean isRoundEnding() {
        return this.isEnding;
    }
}