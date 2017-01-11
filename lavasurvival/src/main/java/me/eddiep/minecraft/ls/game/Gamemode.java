package me.eddiep.minecraft.ls.game;

import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.Utils;
import me.eddiep.ClassicPhysics;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.impl.Flood;
import me.eddiep.minecraft.ls.game.impl.Fusion;
import me.eddiep.minecraft.ls.game.impl.Rise;
import me.eddiep.minecraft.ls.game.items.Intrinsic;
import me.eddiep.minecraft.ls.game.options.FloodOptions;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import me.eddiep.minecraft.ls.game.status.PlayerStatusManager;
import me.eddiep.minecraft.ls.ranks.UserInfo;
import me.eddiep.minecraft.ls.ranks.UserManager;
import me.eddiep.minecraft.ls.system.BukkitUtils;
import me.eddiep.minecraft.ls.system.FileUtils;
import me.eddiep.minecraft.ls.system.PhysicsListener;
import me.eddiep.minecraft.ls.system.PlayerListener;
import me.eddiep.minecraft.ls.system.specialblocks.SpecialInventory;
import net.nyvaria.googleanalytics.hit.EventHit;
import net.nyvaria.googleanalytics.hit.SocialInteractionHit;
import net.nyvaria.openanalytics.bukkit.client.Client;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.craftbukkit.v1_11_R1.boss.CraftBossBar;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftFallingBlock;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import static me.eddiep.minecraft.ls.system.util.RandomDistribution.NEGATIVE_EXPONENTIAL;
import static me.eddiep.minecraft.ls.system.util.RandomHelper.*;

public abstract class Gamemode {
    private static final Material[] DEFAULT_BLOCKS = new Material[]{
            Material.TORCH,
            Material.COBBLESTONE,
            Material.DIRT,
            Material.GRASS,
            Material.WOOD,
            Material.SAND
    };
    private static final Class[] GAMES = new Class[]{
            Rise.class,
            Flood.class,
            Fusion.class
    };

    private static final int VOTE_COUNT;
    private static boolean restart;
    private static String restartServer;

    static {
        if (LavaMap.getPossibleMaps().length == 0)
            VOTE_COUNT = 0;
        else
            VOTE_COUNT = LavaMap.getPossibleMaps().length <= 3 ? LavaMap.getPossibleMaps().length - 1 : 3;
    }

    public static final double DAMAGE = 3;
    public static final double DAMAGE_FREQUENCY = 0.5;
    protected static boolean LAVA = true;
    protected static Random RANDOM = new Random();
    private static boolean voting = false;
    private final LavaMap[] nextMaps = new LavaMap[VOTE_COUNT];
    protected final ArrayList<CraftBossBar> bars = new ArrayList<>();
    private final int[] votes = new int[VOTE_COUNT];
    private int voteCount;
    private static LavaMap lastMap, currentMap;
    private static ArrayList<UUID> alive, dead;
    private static Scoreboard scoreboard;
    private static PlayerListener listener;
    private static PhysicsListener physicsListener;
    private static Gamemode currentGame = null;
    protected String type = "Rise";
    protected boolean poured;
    private BukkitRunnable tickTask;
    private Gamemode nextGame;
    private LavaMap map;
    private boolean endGame;
    private final ChatColor specialColor = ChatColor.GREEN;
    private long startTime;
    private List<Block> spongeLocations = new ArrayList<>();
    private boolean suspended;

    protected static PlayerListener getPlayerListener() {
        return listener;
    }

    public static LavaMap getCurrentMap() {
        return currentMap;
    }

    public static World getCurrentWorld() {
        return currentMap.getWorld();
    }

    public static Gamemode getCurrentGame() {
        return currentGame;
    }

    public static Scoreboard getScoreboard() {
        return scoreboard;
    }

    public static PhysicsListener getPhysicsListener() {
        return physicsListener;
    }

    public static void cleanup() {
        if (listener != null)
            listener.cleanup();
        if (physicsListener != null)
            physicsListener.cleanup();
        if (scoreboard != null)
            scoreboard.getTeam("Special").unregister();
    }

    public final void prepare() {
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team t = scoreboard.getTeam("Special");
            if (t == null)
                t = scoreboard.registerNewTeam("Special");
            t.setPrefix(specialColor + "");
        }
        if (listener == null) {
            listener = new PlayerListener();
            Lavasurvival.INSTANCE.getServer().getPluginManager().registerEvents(listener, Lavasurvival.INSTANCE);
        }
        if (physicsListener == null) {
            physicsListener = new PhysicsListener();
            Lavasurvival.INSTANCE.getServer().getPluginManager().registerEvents(physicsListener, Lavasurvival.INSTANCE);
            physicsListener.prepare();
        }
        if (this.map == null) {
            String[] files = LavaMap.getPossibleMaps();
            lastMap = currentMap;
            do {
                String next = files[random(files.length)];
                try {
                    currentMap = LavaMap.load(next);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (true);
        } else
            currentMap = this.map;

        currentMap.prepare();
    }

    private void addBar(CraftBossBar bar) {
        this.bars.add(bar);
        bar.show();
    }

    public final void start() {
        start(false);
    }

    public final void start(boolean forceStart) {
        if (!forceStart && Bukkit.getOnlinePlayers().size() == 0) {
            Lavasurvival.log("No one is online...suspending start");
            suspended = true;
            currentGame = this;
            return;
        }

        if (restart) {
            Lavasurvival.INSTANCE.updating = true;
            for (Player p : Bukkit.getOnlinePlayers()) {
                try {
                    Lavasurvival.INSTANCE.changeServer(p, restartServer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> {
                //Unloading world
                if (lastMap != null) {
                    Lavasurvival.log("Unloading " + lastMap.getWorld().getName() + "..");
                    boolean success = Bukkit.unloadWorld(lastMap.getWorld(), false);
                    if (!success)
                        Lavasurvival.log("Failed to unload last map! A manual unload may be required..");
                    else
                        restoreBackup(lastMap.getWorld());
                }
                //Restart
                //Should this use Bukkit.spigot().restart(); instead of will that cause issues
                Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart"), 20);
            }, 20 * 2);
            return;
        }
        onStart();
    }

    private long lastMoneyCheck = System.currentTimeMillis();

    protected void onStart() {
        Lavasurvival.log("New game on " + getCurrentWorld().getName());
        if (Necessities.isTracking()) {
            EventHit hit;
            if (isRewardDoubled())
                hit = new EventHit(null, "GameReward", "DoubleRound");
            else
                hit = new EventHit(null, "GameReward", "NormalRound");
            EventHit roundStart = new EventHit(null, "GameInfo", "RoundStart");
            startTime = System.currentTimeMillis();
            Necessities.trackAction(hit);
            Necessities.trackAction(roundStart);
        }
        this.isEnding = false;
        this.hasEnded = false;
        this.suspended = false;
        setIsLava(currentMap.getFloodOptions());
        for (CraftBossBar bar : this.bars) {
            bar.hide();
            bar.removeAll();
        }
        this.bars.clear();
        addBar(new CraftBossBar(ChatColor.GOLD + "Gamemode: " + (LAVA ? ChatColor.RED : ChatColor.AQUA) + getType(), LAVA ? BarColor.RED : BarColor.BLUE, BarStyle.SEGMENTED_6));
        if (isRewardDoubled())
            addBar(new CraftBossBar(ChatColor.GOLD + "Reward is double", BarColor.WHITE, BarStyle.SEGMENTED_20));
        alive = new ArrayList<>();
        dead = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(this::playerJoin);
        currentGame = this;
        Bukkit.getOnlinePlayers().forEach(this::addBars);
        if (lastMap != null) {
            Lavasurvival.log("Unloading " + lastMap.getWorld().getName() + "..");
            boolean success = Bukkit.unloadWorld(lastMap.getWorld(), false);
            if (!success)
                Lavasurvival.log("Failed to unload last map! A manual unload may be required..");
            else
                new Thread(() -> restoreBackup(lastMap.getWorld())).start();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> {
            ClassicPhysics.INSTANCE.getPhysicsHandler().setPhysicsWorld(getCurrentWorld());
            spawnSpecialBlocks(false);
        }, 20); //Delay it slightly to ensure things are set
        Lavasurvival.INSTANCE.MONEY_VIEWER.run();
        this.tickTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastMoneyCheck >= 20000) {
                    Lavasurvival.INSTANCE.MONEY_VIEWER.run();
                    lastMoneyCheck = System.currentTimeMillis();
                }
                tick();
            }
        };
        this.tickTask.runTaskTimer(Lavasurvival.INSTANCE, 0, 1);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void restoreBackup(World world) {
        Lavasurvival.log("Restoring backup of " + world.getName());
        try {
            FileUtils.copyDirectory(new File(Lavasurvival.INSTANCE.getDataFolder(), world.getName()), world.getWorldFolder());
            new File(Lavasurvival.INSTANCE.getDataFolder(), world.getName()).delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long timeTickCount;
    private long lastBlockUpdate = 0;

    private void tick() {
        PlayerStatusManager.tick();
        if (currentMap.getTimeOptions().isEnabled()) {
            double multiplier = currentMap.getTimeOptions().getMultiplier();
            if (multiplier != 1.0) {
                this.timeTickCount++;
                long tick = (long) (this.timeTickCount * multiplier);
                getCurrentWorld().setTime(currentMap.getTimeOptions().getStartTimeTick() + tick);
            }
        }
        onTick();
    }

    protected abstract void onTick();

    protected abstract double calculateReward(Player player, int blockCount);

    public int countAirBlocksAround(Player player, int limit) {
        return airBlocksAround(player.getLocation(), limit);
    }

    public boolean isSuspended() {
        return suspended;
    }

    private int airBlocksAround(Location original, int limit) {
        Block starting = original.getBlock();
        Stack<Block> blocks = new Stack<>();
        ArrayList<Block> counted = new ArrayList<>();
        int count = 0;
        blocks.push(starting);
        while (!blocks.isEmpty()) {
            Block b = blocks.pop();
            if (b.getLocation().distance(original) >= limit || getCurrentMap().isInSafeZone(b.getLocation()))
                continue;
            Block north = b.getRelative(BlockFace.NORTH);
            Block south = b.getRelative(BlockFace.SOUTH);
            Block east = b.getRelative(BlockFace.EAST);
            Block west = b.getRelative(BlockFace.WEST);
            Block up = b.getRelative(BlockFace.UP);
            Block down = b.getRelative(BlockFace.DOWN);
            if (!north.getType().isSolid() && !north.isLiquid() && !counted.contains(north) && !getCurrentMap().isInSafeZone(north.getLocation())) {
                count++;
                blocks.push(north);
                counted.add(north);
            }
            if (!south.getType().isSolid() && !south.isLiquid() && !counted.contains(south) && !getCurrentMap().isInSafeZone(south.getLocation())) {
                count++;
                blocks.push(south);
                counted.add(south);
            }
            if (!east.getType().isSolid() && !east.isLiquid() && !counted.contains(east) && !getCurrentMap().isInSafeZone(east.getLocation())) {
                count++;
                blocks.push(east);
                counted.add(east);
            }
            if (!west.getType().isSolid() && !west.isLiquid() && !counted.contains(west) && !getCurrentMap().isInSafeZone(west.getLocation())) {
                count++;
                blocks.push(west);
                counted.add(west);
            }
            if (!up.getType().isSolid() && !up.isLiquid() && !counted.contains(up) && !getCurrentMap().isInSafeZone(up.getLocation())) {
                count++;
                blocks.push(up);
                counted.add(up);
            }
            if (!down.getType().isSolid() && !down.isLiquid() && !counted.contains(down) && !getCurrentMap().isInSafeZone(down.getLocation())) {
                count++;
                blocks.push(down);
                counted.add(down);
            }
        }
        counted.clear(); //Clear memory
        return count;
    }

    protected boolean isEnding;
    private boolean hasEnded;

    protected void endRoundIn(long seconds) {
        if (this.isEnding)
            return;
        this.isEnding = true;
        if (Math.random() <= 0.3)
            spawnSpecialBlocks(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, this::endRound, seconds * 20L);
    }

    public boolean hasEnded() {
        return this.hasEnded;
    }

    public void endRound() {
        endRound(false, true);
    }

    @SuppressWarnings("ConstantConditions")
    public void endRound(boolean skipVote, boolean giveRewards) {
        if (this.hasEnded)
            return;
        end();
        if (giveRewards) {
            CmdHide hide = Necessities.getHide();
            int amount = 0;
            for (UUID id : alive)
                if (id != null && Bukkit.getPlayer(id) != null && !hide.isHidden(Bukkit.getPlayer(id)) && !isInSpawn(Bukkit.getPlayer(id)))
                    amount++;
            if (amount == 0) {
                globalMessage("No one survived..");
            } else if (amount <= 45) {
                globalMessage("Congratulations to the survivors!");
                String survivors = "";
                for (UUID id : alive) {
                    if (id == null)
                        continue;
                    Player p = Bukkit.getPlayer(id);
                    if (p == null || hide.isHidden(p) || isInSpawn(p) || p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR))
                        continue;
                    if (survivors.equals(""))
                        survivors += Bukkit.getPlayer(id).getName();
                    else
                        survivors += ", " + Bukkit.getPlayer(id).getName();
                }
                globalMessage(survivors);
            } else
                globalMessage("Congratulations to all " + amount + " survivors!");
            final HashMap<UUID, Integer> winners = new HashMap<>();
            HashMap<Rank, Double[]> avgs = new HashMap<>();
            /*double count = 0;
            int avgAir = 0;
            double avgReward = 0;*/
            for (UUID id : alive) {
                if (id == null)
                    continue;
                Player p = Bukkit.getPlayer(id);
                if (p == null || hide.isHidden(p) || isInSpawn(Bukkit.getPlayer(id)) || p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR))
                    continue;
                Rank rank = Necessities.getUM().getUser(p.getUniqueId()).getRank();
                Double[] array;
                if (!avgs.containsKey(rank))
                    array = new Double[]{0.0, 0.0, 0.0};
                else
                    array = avgs.get(rank);
                int blockCount = countAirBlocksAround(p, 10);
                //avgAir += blockCount;
                array[0] += blockCount;
                double reward = calculateReward(p, blockCount);
                //avgReward += reward;
                array[1] += reward;
                array[2]++;
                avgs.put(rank, array);
                winners.put(p.getUniqueId(), blockCount);
                Lavasurvival.INSTANCE.depositPlayer(p, reward);
                p.getPlayer().sendMessage(ChatColor.GREEN + "+ " + ChatColor.GOLD + "You won " + ChatColor.BOLD + reward + ChatColor.RESET + "" + ChatColor.GOLD + " GGs!");
                p.sendTitle("You won!", ChatColor.GOLD + "" + ChatColor.BOLD + reward + ChatColor.GOLD + " GGs!", 0, 60, 0);
            }

            if (Necessities.isTracking()) {
                for (Rank rank : avgs.keySet()) {
                    Double[] array = avgs.get(rank);
                    array[0] = array[0] / array[2];
                    array[1] = array[1] / array[2];

                    EventHit rewardAvg = new EventHit(null, "GameInfo", "AverageReward-" + rank.getName());
                    rewardAvg.event_value = (int) array[1].doubleValue();

                    EventHit airAvg = new EventHit(null, "GameInfo", "AverageAir-" + rank.getName());
                    airAvg.event_value = (int) array[0].doubleValue();

                    Necessities.trackAction(rewardAvg);
                    Necessities.trackAction(airAvg);
                }
            }
            avgs.clear();
            new BukkitRunnable() {
                @Override
                public void run() {
                    recordMatch(winners, dead);
                }
            }.runTaskAsynchronously(Lavasurvival.INSTANCE);
        }

        Lavasurvival.INSTANCE.MONEY_VIEWER.run();
        this.lastMoneyCheck = System.currentTimeMillis();

        if (!skipVote)
            new Thread(this::startVoting).start();
        else {
            try {
                String[] files = LavaMap.getPossibleMaps();
                if (this.nextGame == null)
                    this.nextGame = pickRandomGame(null);
                if (this.nextGame.map == null)
                    this.nextGame.map = LavaMap.load(files[random(files.length)]);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> {
                    lastMap = getCurrentMap();
                    tryNextGame();
                }, 20);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*if (giveRewards) {
            new Thread(() -> {
                System.out.println("Updating ratings..");
                int count = 0;
                long start = System.currentTimeMillis();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    UserInfo info = um.getUser(p.getUniqueId());
                    if (info.getRanking().shouldUpdate()) {
                        info.getRanking().update();
                        count++;
                    }
                }
                System.out.println("Updated " + count + " in " + (System.currentTimeMillis() - start) + "ms !");
            }).start();
        }*/

        Bukkit.getOnlinePlayers().forEach(this::removeBars);
    }

    private String getType() {
        return this.type;
    }

    private void recordMatch(HashMap<UUID, Integer> winners, ArrayList<UUID> losers) {
        if ((winners == null || winners.isEmpty()) && (losers == null || losers.isEmpty()))
            return; //Do not record a match that no one is in
        String mode = this.getType();
        String winnerList = "{";
        String scoreList = "{";
        String loserList = "{";
        if (winners != null)
            for (UUID uuid : winners.keySet()) {
                winnerList += uuid + ",";
                scoreList += winners.get(uuid) + ",";
            }
        for (UUID uuid : losers)
            loserList += uuid + ",";
        winnerList = winnerList.substring(0, winnerList.length() - 1) + "}";
        scoreList = scoreList.substring(0, scoreList.length() - 1) + "}";
        loserList = loserList.substring(0, loserList.length() - 1) + "}";
        if (winnerList.length() == 1)
            winnerList = "{}";
        if (scoreList.length() == 1)
            scoreList = "{}";
        if (loserList.length() == 1)
            loserList = "{}";
        try { //Only connect once instead of connecting once per user being added
            Connection conn = DriverManager.getConnection(Lavasurvival.INSTANCE.getDBURL(), Lavasurvival.INSTANCE.getDBProperties());
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO matches (gamemode, winners, scores, losers) VALUES ('" + mode + "', '" + winnerList + "', '" + scoreList + "', '" + loserList + "')");
            if (winners != null)
                for (UUID uuid : winners.keySet()) {//Rating calculated off of avg blocks around, NOT reward since this is based on rank too
                    stmt.execute("UPDATE users SET matches = CONCAT('," + winners.get(uuid) + "', matches) WHERE uuid = '" + uuid + "'");
                    ResultSet rs = stmt.executeQuery("SELECT matches FROM users WHERE uuid = '" + uuid + "'");
                    if (rs.next())
                        stmt.execute("UPDATE users SET rating = '" + this.getRating(parseMatches(rs.getString("matches"))) + "' WHERE uuid = '" + uuid + "'");
                    rs.close();
                }
            for (UUID uuid : losers) {
                stmt.execute("UPDATE users SET matches = CONCAT(',0', matches) WHERE uuid= '" + uuid + "'");
                ResultSet rs = stmt.executeQuery("SELECT matches FROM users WHERE uuid = '" + uuid + "'");
                if (rs.next())
                    stmt.execute("UPDATE users SET rating = '" + this.getRating(parseMatches(rs.getString("matches"))) + "' WHERE uuid = '" + uuid + "'");
                rs.close();
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Integer> parseMatches(String matchesString) {
        String[] ms = matchesString.split(",");
        ArrayList<Integer> matches = new ArrayList<>();
        for (String match : ms)
            if (!match.equals("") && Utils.legalInt(match))
                matches.add(Integer.parseInt(match));
        return matches;
    }

    private double getRating(ArrayList<Integer> matches) { //Max blocks around is 7999
        if (matches.isEmpty())
            return 1000;
        double sum = 0.0;
        for (int i : matches)
            sum += i;
        double average = sum / matches.size();
        double total = 0.0;
        for (int i : matches)
            total += Math.pow(i - average, 2);
        double std = Math.sqrt(total / matches.size());
        return average;
    }

    public List<LavaMap> getMapsInVote() {
        return Collections.unmodifiableList(Arrays.asList(this.nextMaps));
    }

    public boolean hasVoted(Player player) {
        return this.voted.contains(player);
    }

    public void voteFor(int index) {
        this.votes[index]++;
        this.voteCount++;
    }

    private final ArrayList<OfflinePlayer> voted = new ArrayList<>();

    public void voteFor(int number, Player player) {
        if (number >= this.nextMaps.length) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid number! Please choose a number between (1 - " + this.nextMaps.length + ").");
            return;
        }
        this.voted.add(player);
        this.votes[number]++;
        this.voteCount++;
        player.sendMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + "" + ChatColor.BOLD + "You voted for " + this.nextMaps[number].getName() + "!");
        if (Necessities.isTracking()) {
            SocialInteractionHit vote = new SocialInteractionHit(new Client(player), "voter", "vote", this.nextMaps[number].getName());
            Necessities.trackAction(vote);
        }
    }

    public boolean isVoting() {
        return voting;
    }

    @SuppressWarnings("ConstantConditions")
    private void startVoting() {
        if (voting)
            return;
        String[] files = LavaMap.getPossibleMaps();
        if (files.length > 1) {
            String extra = "";
            for (int i = 0; i < this.nextMaps.length; i++) {
                this.votes[i] = 0; //reset votes
                boolean found;
                String next;
                do {
                    found = false;
                    next = files[random(files.length)];
                    File possibleNext = new File(next);
                    if (currentMap.getFile().equals(possibleNext)) {
                        found = true;
                        continue;
                    }
                    for (LavaMap nextMap : this.nextMaps)
                        if (nextMap != null && nextMap.getFile().equals(possibleNext)) {
                            found = true;
                            break;
                        }
                } while (found);
                try {
                    this.nextMaps[i] = LavaMap.load(next);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!extra.equals(""))
                    extra += ",{\"text\":\" \"},";
                extra += "{\"text\":\"" + (i + 1) + ". " + this.nextMaps[i].getName() + "\",\"underlined\":true,\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/lvote " + (i + 1) +
                        "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Vote for " + this.nextMaps[i].getName() + "\"}}";
            }
            this.voted.clear();
            globalMessage(ChatColor.GREEN + "It's time to vote for the next map!");
            voting = true;
            globalMessage(ChatColor.BOLD + "No talking will be allowed during the vote.");
            globalMessageNoPrefix(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Click the map you want to vote for:");
            globalMessageNoPrefix(" ");
            if (!extra.equals(""))
                globalRawMessage("{\"text\":\"\",\"extra\":[" + extra + "]}");
            globalMessageNoPrefix(" ");
            long start = System.currentTimeMillis();
            while (true) {
                long cur = System.currentTimeMillis();
                if (this.voteCount >= Bukkit.getServer().getOnlinePlayers().size() || cur - start >= 50000)
                    break;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            voting = false;
            this.voteCount = 0;
            LavaMap next = null;
            int highest = 0;
            for (int i = 0; i < this.nextMaps.length; i++) {
                globalMessage(ChatColor.BOLD + this.nextMaps[i].getName() + ChatColor.RESET + " - " + this.votes[i] + " votes");
                if (next == null) {
                    next = this.nextMaps[i];
                    highest = this.votes[i];
                } else if (this.votes[i] > highest) {
                    highest = this.votes[i];
                    next = this.nextMaps[i];
                }
            }
            if (next != null)
                globalMessage(ChatColor.BOLD + next.getName() + ChatColor.RESET + " won the vote!");
            if (this.nextGame == null)
                this.nextGame = pickRandomGame(next);
            this.nextGame.map = next;
        } else {
            try {
                if (this.nextGame == null)
                    this.nextGame = pickRandomGame(null);
                this.nextGame.map = LavaMap.load(files[random(files.length)]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> {
            lastMap = getCurrentMap();
            tryNextGame();
        }, 20);
    }

    protected void setIsLava(FloodOptions option) {
        if (option.isLavaEnabled() && option.isWaterEnabled())
            LAVA = random(100) < 75; //Have water/lava check be in here instead of as argument
        else
            LAVA = option.isLavaEnabled() || !option.isWaterEnabled() && random(100) < 75;
    }

    public void forceEnd() {
        end();
    }

    private void end() {
        if (isSuspended())
            return; //Is suspended no need to try to end again
        PlayerStatusManager.cleanup();
        this.tickTask.cancel();
        if (scoreboard != null) {
            scoreboard.getTeam("Special").unregister();
            Team t = scoreboard.registerNewTeam("Special");
            t.setPrefix(specialColor + "");
        }
        //Bukkit.getScheduler().cancelTasks(Lavasurvival.INSTANCE);
        globalMessage(ChatColor.GREEN + "The round has ended!");
        ClassicPhysics.INSTANCE.getPhysicsHandler().setPhysicsWorld(null);
        this.isEnding = false;
        this.hasEnded = true;
        getPhysicsListener().clearBlockedLocations();
        long duration = System.currentTimeMillis() - startTime;

        duration /= 1000;

        if (Necessities.isTracking()) {
            EventHit endRound = new EventHit(null, "GameInfo", "RoundEnd");
            endRound.event_value = (int) duration;

            Necessities.trackAction(endRound);
        }
    }

    private Gamemode pickRandomGame(LavaMap map) {
        if (map == null) {
            Class<?> nextGameClass = GAMES[random(GAMES.length)];
            try {
                return (Gamemode) nextGameClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Class<? extends Gamemode>[] games = map.getEnabledGames();
            Class<? extends Gamemode> nextGameClass = games[random(games.length)];
            try {
                return nextGameClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void tryNextGame() {
        if (this.nextGame != null) {
            globalMessage("Preparing next game..");
            this.nextGame.prepare();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> this.nextGame.start(), 40); //2 seconds
        }
    }

    protected double getDefaultReward(OfflinePlayer player, int blockCount) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null)
            return 0.0;
        double base = 100.0;
        Rank rank = Necessities.getUM().getUser(player.getUniqueId()).getRank();
        double bonusAdd = (5 + Necessities.getRM().getOrder().indexOf(rank)) / 2.0;
        //int blockCount = countAirBlocksAround(onlinePlayer, 20);
        //System.out.println(onlinePlayer.getName() + " had " + blockCount + " blocks around them!");
        return base + (bonusAdd * blockCount);
    }

    private void setNextGame(Gamemode game) {
        this.nextGame = game;
    }

    private void setNextMap(LavaMap map) {
        if (this.nextGame != null)
            this.nextGame.map = map;
    }

    public boolean setNextMap(String map, String type) {
        String[] files = LavaMap.getPossibleMaps();
        map = map.toLowerCase() + ".map";
        for (String file : files)
            if (file.toLowerCase().replaceAll(" ", "").endsWith(map)) {
                LavaMap lavaMap;
                Gamemode g = null;
                try {
                    lavaMap = LavaMap.load(file);
                    if (type != null) {
                        type = type.toLowerCase();
                        Class<? extends Gamemode>[] games = lavaMap.getEnabledGames();
                        for (Class<? extends Gamemode> game : games)
                            if (game.getName().toLowerCase().endsWith(type))
                                g = game.newInstance();
                    }
                } catch (IOException | IllegalAccessException | InstantiationException ignored) {
                    return false;
                }
                setNextGame(g == null ? pickRandomGame(lavaMap) : g);
                setNextMap(lavaMap);
                return true;
            }
        return false;
    }

    public void playerJoin(Player player) {
        setAlive(player);
        player.teleport(new Location(getCurrentWorld(), getCurrentMap().getMapSpawn().getX(), getCurrentMap().getMapSpawn().getY(), getCurrentMap().getMapSpawn().getZ()));
        player.setGameMode(GameMode.SURVIVAL);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getHealth(Necessities.getUM().getUser(player.getUniqueId()).getRank()));
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setGlowing(true);
        UserManager um = Lavasurvival.INSTANCE.getUserManager();
        UserInfo u = um.getUser(player.getUniqueId());
        u.resetGenerosity();
        u.resetBlockChangeCount();
        PlayerInventory inv = player.getInventory();
        for (Material DEFAULT_BLOCK : DEFAULT_BLOCKS) {
            ItemStack toGive = new ItemStack(DEFAULT_BLOCK, 1);
            if (BukkitUtils.hasItem(player.getInventory(), toGive) || u.isInBank(new MaterialData(DEFAULT_BLOCK)))
                continue;
            ItemMeta im = toGive.getItemMeta();
            im.setLore(Arrays.asList("Lava MeltTime: " + PhysicsListener.getLavaMeltTimeAsString(toGive.getData()), "Water MeltTime: " + PhysicsListener.getWaterMeltTimeAsString(toGive.getData())));
            toGive.setItemMeta(im);
            player.getInventory().addItem(toGive);
        }
        ItemStack[] armor = inv.getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            ItemStack item = armor[i];
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0).equalsIgnoreCase("Special"))
                armor[i] = null;
        }
        inv.setArmorContents(armor);
        ItemStack offhand = inv.getItemInOffHand();
        if (offhand != null && offhand.hasItemMeta() && offhand.getItemMeta().hasLore() && offhand.getItemMeta().getLore().get(0).equalsIgnoreCase("Special"))
            inv.setItemInOffHand(null);
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0).equalsIgnoreCase("Special"))
                inv.remove(item);
        }
        if (!player.getInventory().contains(Material.WRITTEN_BOOK))
            player.getInventory().addItem(Lavasurvival.INSTANCE.getRules());
        ShopFactory.validateInventory(inv);
        u.giveBoughtBlocks();
        if (getCurrentMap().getCreator().equals(""))
            player.sendTitle(ChatColor.GOLD + getCurrentMap().getName(), "", 0, 60, 0);
        else
            player.sendTitle(ChatColor.GOLD + getCurrentMap().getName(), ChatColor.GOLD + "Map by: " + getCurrentMap().getCreator(), 0, 60, 0);
    }

    public void addBars(Player p) {
        this.bars.forEach(bar -> bar.addPlayer(p));
    }

    public void removeBars(Player p) {
        this.bars.forEach(bar -> bar.removePlayer(p));
    }

    public double getHealth(Rank r) {
        if (r == null)
            return 1;
        switch (Necessities.getRM().getOrder().indexOf(r)) {
            case 0:
                return 10;
            case 1:
                return 15;
            case 2:
                return 20;
            case 3:
                return 25;
            case 4:
                return 30;
            case 5:
                return 40;
            default:
                return 40;
        }
    }

    private void setAlive(Player player) {
        if (player == null)
            return;
        UUID uuid = player.getUniqueId();
        if (dead.contains(uuid))
            dead.remove(uuid);
        if (!alive.contains(uuid))
            alive.add(uuid);
        Necessities.getUM().getUser(uuid).setStatus("alive");
        player.setGameMode(GameMode.SURVIVAL);
        Lavasurvival.log(player.getName() + " has joined the alive team.");
    }

    public void setDead(Player player) {
        if (player == null)
            return;
        UUID uuid = player.getUniqueId();
        player.setGlowing(false);
        if (alive.contains(uuid))
            alive.remove(uuid);
        if (!dead.contains(uuid))
            dead.add(uuid);
        Necessities.getUM().getUser(uuid).setStatus("dead");
        player.setGameMode(GameMode.SPECTATOR);
        Lavasurvival.log(player.getName() + " has joined the dead team.");
        if (allDead())
            Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> getCurrentGame().endRound(), 20 * 3);
    }

    public boolean isAlive(Player player) {
        return player != null && (alive.contains(player.getUniqueId()));
    }

    public boolean isDead(Player player) {
        return player != null && dead.contains(player.getUniqueId());
    }

    public boolean isInGame(Player player) {
        return player != null && (alive.contains(player.getUniqueId()) || dead.contains(player.getUniqueId()));
    }

    public void globalMessage(String message) {
        if (getCurrentWorld() != null)
            getCurrentWorld().getPlayers().forEach(p -> p.sendMessage(ChatColor.RED + "[Lavasurvival] " + ChatColor.RESET + message));
    }

    private void globalMessageNoPrefix(String message) {
        getCurrentWorld().getPlayers().forEach(p -> p.sendMessage(message));
    }

    private void globalRawMessage(String rawMessage) {
        getCurrentWorld().getPlayers().forEach(p -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + p.getName() + " " + rawMessage));
    }

    protected Material getMat() {
        return LAVA ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER;
    }

    private boolean isInSpawn(Player player) {
        return player != null && (getCurrentMap().isInSafeZone(player.getLocation()) || getCurrentMap().isInSafeZone(player.getEyeLocation()));
    }

    public boolean allDead() {
        CmdHide hide = Necessities.getHide();
        boolean allDead = Bukkit.getOnlinePlayers().size() != 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isAlive(p) && !hide.isHidden(p)) {
                allDead = false;
                break;
            }
        }
        return allDead;
    }

    public boolean isEndGame() {
        return this.endGame;
    }

    private final MaterialData money = new MaterialData(Material.BOOKSHELF);
    private final MaterialData common = new MaterialData(Material.CAULDRON);
    private final MaterialData uncommon = new MaterialData(Material.ENDER_PORTAL_FRAME);
    private final MaterialData epic = new MaterialData(Material.ENCHANTMENT_TABLE);

    public void interactSpecial(Player p, FallingBlock b) {
        if (b.hasGravity()) //Make it so that the block has to have landed already. This way we don't have to worry about dupes
            return;
        MaterialData data = new MaterialData(b.getMaterial(), b.getBlockData());
        Intrinsic tier = null;
        if (data.equals(money)) { //Give them money and calculate how much
            int baseDistribution;
            if (this instanceof Flood)
                baseDistribution = (int) (Math.log(1 - uniformRandom()) / -0.47) + 1;
            else {
                int totalLayers = getCurrentMap().getHeight();
                int layersLeft = 0;
                if (this instanceof Rise)
                    layersLeft = ((Rise) this).layersLeft.getScore();
                else if (this instanceof Fusion)
                    layersLeft = ((Fusion) this).layersLeft.getScore();
                if (layersLeft <= 0)
                    layersLeft = 1;
                double inverseLayersLeft = (double) layersLeft / (double) totalLayers;
                baseDistribution = (int) (Math.log(1 - uniformRandom()) / -(Math.abs(inverseLayersLeft - 1) <= 0.15 ? 0.909 : inverseLayersLeft / 1.1)) + 1;
            }
            int reward = baseDistribution * 100;
            if (isRewardDoubled())
                reward *= 2;
            globalMessage(ChatColor.GOLD + p.getName() + ChatColor.GREEN + " found " + Necessities.getEconomy().format(reward));
            Lavasurvival.INSTANCE.depositPlayer(p, reward);
        } else if (data.equals(common)) //Give them some common items/blocks
            tier = Intrinsic.COMMON;
        else if (data.equals(uncommon)) //Give them some uncommon items/blocks
            tier = Intrinsic.UNCOMMON;
        else if (data.equals(epic)) //Give them some epic items/blocks
            tier = Intrinsic.EPIC;
        if (tier == null) {
            if (scoreboard != null)
                scoreboard.getTeam("Special").removeEntry(b.getUniqueId().toString());
            b.remove();
        } else {
            SpecialInventory inv = SpecialInventory.from(b);
            if (inv == null)
                inv = SpecialInventory.create(b, tier);
            inv.openFor(p);
        }
    }

    protected void spawnSpecialBlocks(boolean isEndWave) {
        LavaMap cmap = getCurrentMap();
        int minX = cmap.getMinX(), maxX = cmap.getMaxX(), minZ = cmap.getMinZ(), maxZ = cmap.getMaxZ();
        int difX = maxX - minX, difZ = maxZ - minZ;
        if (difX < 0 || difZ < 0)
            return; //There is an error in the config
        //Increment by one so that the random is inclusive and that if number > 1 it does not have to recalculate the +1
        difX++;
        difZ++;
        com.crossge.necessities.RankManager.UserManager um = Necessities.getUM();
        RankManager rm = Necessities.getRM();
        int survivor = rm.getOrder().indexOf(rm.getRank("Survivor"));
        int min = alive.size() == 0 ? 1 : 0, max = alive.size() / 3;
        for (UUID uuid : alive)
            if (rm.getOrder().indexOf(um.getUser(uuid).getRank()) < survivor) {
                min = 1;
                break;
            }
        if (max < min)
            max = alive.size();
        if (max <= 0)
            max = 1;
        int number = random(min, max), y = getCurrentMap().getSpecialY();
        for (int i = 0; i < number; i++) {
            MaterialData data;
            double u = random(1, 100, NEGATIVE_EXPONENTIAL);
            if (u < 50) {
                if (randomBoolean())
                    data = money;
                else
                    data = common;
            } else if (u < 80)
                data = uncommon;
            else
                data = epic;
            if (isEndWave) {
                BlockVector location = findOpenSpace();
                if (location != null)
                    spawnSpecialBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), data, false);
            } else
                spawnSpecialBlock(minX + random(difX), y, minZ + random(difZ), data, true);
        }
    }

    private static BlockVector findOpenSpace() {
        return findOpenSpace(100);
    }

    private static BlockVector findOpenSpace(int limit) {
        LavaMap cmap = getCurrentMap();
        int minX = cmap.getMinX(), maxX = cmap.getMaxX(), minZ = cmap.getMinZ(), maxZ = cmap.getMaxZ();
        int minY = cmap.getLavaY() - cmap.getHeight(), maxY = cmap.getLavaY();

        for (int i = 0; i < limit; i++) {
            int x = random(minX, maxX), y = random(minY, maxY), z = random(minZ, maxZ);
            Block block = cmap.getWorld().getBlockAt(x, y, z);
            if (block.getType() == Material.AIR)
                return block.getLocation().toVector().toBlockVector();
        }

        return null;
    }

    public void spawnSpecialBlock(int x, int y, int z, MaterialData data, boolean gravity) {
        //TODO: Announce what type of block is falling
        FallingBlock b = getCurrentWorld().spawnFallingBlock(new Location(getCurrentWorld(), x + 0.5, y, z + 0.5), data); //Should y be + 0.5 as well probably not
        b.setGlowing(true);
        b.setDropItem(true);
        b.setGravity(gravity);
        if (!gravity) //If no gravity make the block not expire after 30 seconds
            ((CraftFallingBlock) b).getHandle().ticksLived = -2147483648; //Bypass the spigot check of it being negative
        if (scoreboard != null)
            scoreboard.getTeam("Special").addEntry(b.getUniqueId().toString());
    }

    public abstract void addToBonus(double takeOut);

    protected abstract boolean isRewardDoubled();

    public static void restartNextGame(String serverToJoin) {
        restart = true;
        restartServer = serverToJoin;
    }

    public Gamemode getNextGame() {
        return nextGame;
    }
}