package me.eddiep.minecraft.ls.game.impl;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.options.FloodOptions;
import me.eddiep.minecraft.ls.system.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.List;

public class Fusion extends Gamemode {
    private int lastMinute, bonus, lvl = 1, sched = 0;
    private long lastEvent, duration, timeOut;
    private Score bonusScore, layersLeft;
    private boolean doubleReward;
    private Objective objective;

    @Override
    public void onStart() {
        this.doubleReward = Math.random() < 0.25;
        if (getScoreboard().getObjective("game") == null)
            this.objective = getScoreboard().registerNewObjective("game", "dummy");
        else
            this.objective = getScoreboard().getObjective("game");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName("Prepare Time");
        this.bonusScore = objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Reward Bonus");
        this.layersLeft = objective.getScore(ChatColor.RED + "" + ChatColor.BOLD + "Layers Left");
        this.type = "Fusion";
        super.onStart();
        this.duration = getCurrentMap().getFusionOptions().generateRandomPrepareTime();
        this.timeOut = getCurrentMap().getFusionOptions().generateRandomFusionTime();
        globalMessage("The current gamemode is " + ChatColor.RED + ChatColor.BOLD + "FUSION");
        globalMessage("The water and lava will rise every " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(this.timeOut));
        globalMessage("You have " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(this.duration) + ChatColor.RESET + " to prepare!");
        this.lastEvent = System.currentTimeMillis();
        this.lastMinute = 0;
        this.bonus = Gamemode.RANDOM.nextInt(80) + 50;
        this.bonusScore.setScore(this.bonus);
        this.layersLeft.setScore(getCurrentMap().getHeight());
        Gamemode.getPlayerListener().survival = false;
        if (this.doubleReward) {
            globalMessage("" + ChatColor.GREEN + ChatColor.BOLD + "All rewards this round are doubled!");
            globalMessage("but.." + ChatColor.RED + ChatColor.BOLD + "THE PREPARE TIME HAS BEEN CUT IN HALF");
            this.duration *= 0.5;
        }
        /*if (Gamemode.getPlayerListener().survival)
            globalMessage("The building style will be " + ChatColor.RED + "" + ChatColor.BOLD + "SURVIVAL STYLE");
        else
            globalMessage("The building style will be " + ChatColor.RED + "" + ChatColor.BOLD + "CLASSIC STYLE");*/
    }

    @Override
    protected void setIsLava(FloodOptions options) {
        super.setIsLava(getCurrentMap().getFusionOptions());
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
        this.objective.unregister();
        this.objective = null;
        Bukkit.getScheduler().cancelTask(this.sched);
        super.endRound();
    }

    @Override
    public void onTick() {
        if (this.objective == null)
            return;
        long since = System.currentTimeMillis() - this.lastEvent;
        int minutes = (int) ((this.duration - since) / 60000), seconds = (int) (((this.duration - since) / 1000) % 60);
        String time = (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
        if (isRoundEnding()) {
            this.objective.setDisplayName("Round Ends In: " + ChatColor.BOLD + time);
            return;
        }
        this.objective.setDisplayName((!super.poured ? "Prepare Time: " : "Next Pour: ") + ChatColor.BOLD + time);
        if (!super.poured && since < this.duration) {
            int nextMinute = (int) Math.floor(since / 60000.0);
            if (nextMinute != this.lastMinute) {
                this.lastMinute = nextMinute;
                List<Location> locations = getCurrentMap().getFusionOptions().getSpawnLocation(0, this.lvl - getCurrentMap().getHeight(), 0);
                getCurrentWorld().strikeLightningEffect(locations.get(RANDOM.nextInt(locations.size()))); //Changed to just effect not to kill unknowing player nearby
                globalMessage("The lava and water will rise in " + ChatColor.DARK_RED + TimeUtils.toFriendlyTime(this.duration - since));
            }
        } else if (!super.poured) {
            super.poured = true;
            globalMessage(ChatColor.DARK_RED + "Here comes the lava and water!");
            this.duration = this.timeOut; //The duration will not change
            this.objective.setDisplayName("Time Till Next Pour");
            pourAndAdvance(this.timeOut / 1000L);
        }
    }

    private void liquidUp(final long time) {
        this.sched = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> pourAndAdvance(time), 20 * time);
    }

    private void pourAndAdvance(long time) {
        List<Location> locs = getCurrentMap().getFusionOptions().getSpawnLocation(0, this.lvl - getCurrentMap().getHeight(), 0);
        int highestCurrentY = locs.get(0).getBlockY();
        for (Location loc : locs)
            if (loc.getBlockY() > highestCurrentY)
                highestCurrentY = loc.getBlockY();
        int lavaY = getCurrentMap().getFusionOptions().getHighestLocation().getBlockY();
        if (highestCurrentY > lavaY) { //If we have passed the original lava spawn, that means the previous pour was the last one
            if (!isRoundEnding()) {
                this.lastEvent = System.currentTimeMillis(); //Set the last event to now
                this.duration = getCurrentMap().getFusionOptions().generateRandomEndTime() / 1000L;
                super.endRoundIn(this.duration);
                this.duration *= 1000L;
            }
            return;
        }
        this.switchADoodle = RANDOM.nextInt();
        locs.forEach(l -> Lavasurvival.INSTANCE.getPhysicsHandler().forcePlaceClassicBlockAt(l, getMat()));
        this.lastEvent = System.currentTimeMillis(); //Set the last event to now
        getCurrentWorld().strikeLightningEffect(locs.get(RANDOM.nextInt(locs.size()))); //Actions are better than words :3
        this.lvl += getCurrentMap().getFusionOptions().getLayerCount();
        this.layersLeft.setScore(lavaY - highestCurrentY);
        BarColor cur = this.bars.get(0).getColor();
        if (cur.equals(BarColor.RED)) {
            this.bars.get(0).setTitle(ChatColor.GOLD + "Gamemode: " + ChatColor.RED + "Fusion");
            this.bars.get(0).setColor(BarColor.BLUE);
        } else if (cur.equals(BarColor.BLUE)) {
            this.bars.get(0).setTitle(ChatColor.GOLD + "Gamemode: " + ChatColor.BLUE + "Fusion");
            this.bars.get(0).setColor(BarColor.RED);
        }
        if (highestCurrentY <= lavaY)
            liquidUp(time); //Only advance up if we are still less than the actual lava spawn or if we are at the lava spawn (the next check will end the game, see above)
    }

    @Override
    public double calculateReward(Player player, int blockCount) {
        double multiplier = 1.0;//In case we want a triple reward
        if (this.doubleReward)
            multiplier = 2.0;
        return (super.getDefaultReward(player, blockCount) + this.bonus) * multiplier;
    }

    private boolean isRoundEnding() {
        return isEnding;
    }

    private int switchADoodle;

    @Override
    public Material getMat() {
        Material mat = this.switchADoodle % 2 == 0 ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER;
        this.switchADoodle++;
        return mat;
    }
}