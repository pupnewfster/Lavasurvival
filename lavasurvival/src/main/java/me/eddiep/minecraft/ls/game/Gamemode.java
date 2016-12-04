package me.eddiep.minecraft.ls.game;

import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.Rank;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.impl.Flood;
import me.eddiep.minecraft.ls.game.impl.Fusion;
import me.eddiep.minecraft.ls.game.impl.Rise;
import me.eddiep.minecraft.ls.game.options.FloodOptions;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import me.eddiep.minecraft.ls.game.status.PlayerStatusManager;
import me.eddiep.minecraft.ls.ranks.UserInfo;
import me.eddiep.minecraft.ls.ranks.UserManager;
import me.eddiep.minecraft.ls.system.BukkitUtils;
import me.eddiep.minecraft.ls.system.FileUtils;
import me.eddiep.minecraft.ls.system.PhysicsListener;
import me.eddiep.minecraft.ls.system.PlayerListener;
import me.eddiep.minecraft.ls.game.lsrating.LSRating;
import mkremins.fanciful.FancyMessage;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.boss.CraftBossBar;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@SuppressWarnings({"SameParameterValue", "unused"})
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

    protected static final Random RANDOM = new Random();
    public static final double DAMAGE = 3;
    public static final double DAMAGE_FREQUENCY = 0.5;
    protected static boolean LAVA = true;
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
    private List<Block> spongeLocations = new ArrayList<>();

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

    public static void cleanup() {
        if (listener != null)
            listener.cleanup();
        if (physicsListener != null)
            physicsListener.cleanup();
    }

    public final void prepare() {
        if (scoreboard == null)
            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
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
                String next = files[RANDOM.nextInt(files.length)];
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
                    else {
                        restoreBackup(lastMap.getWorld());
                    }
                }
                //Restart
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
            if (isRewardDoubled())
                Necessities.trackAction("DoubleRound", getCurrentMap().getName());
            Necessities.trackAction("RoundStart", getCurrentMap().getName());
        }
        this.isEnding = false;
        this.hasEnded = false;
        setIsLava(currentMap.getFloodOptions());
        for (CraftBossBar bar : this.bars) {
            bar.hide();
            bar.removeAll();
        }
        this.bars.clear();
        BarFlag[] flags = new BarFlag[0];
        addBar(new CraftBossBar(ChatColor.GOLD + "Gamemode: " + (LAVA ? ChatColor.RED : ChatColor.AQUA) + getType(), LAVA ? BarColor.RED : BarColor.BLUE, BarStyle.SEGMENTED_6, flags));
        addBar(new CraftBossBar(ChatColor.GOLD + "Reward is " + (isRewardDoubled() ? "double" : "normal"), BarColor.WHITE, BarStyle.SEGMENTED_20, flags));
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
        double multiplier = currentMap.getTimeOptions().getMultiplier();
        if (currentMap.getTimeOptions().isEnabled()) {
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
        final UserManager um = Lavasurvival.INSTANCE.getUserManager();
        if (giveRewards) {
            CmdHide hide = Lavasurvival.INSTANCE.getHide();
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
            final HashMap<Player, Integer> winners = new HashMap<>();
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
                Rank rank = Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(p.getUniqueId()).getRank();
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
                winners.put(p, blockCount);
                Lavasurvival.INSTANCE.depositPlayer(p, reward);
                p.getPlayer().sendMessage(ChatColor.GREEN + "+ " + ChatColor.GOLD + "You won " + ChatColor.BOLD + reward + ChatColor.RESET + "" + ChatColor.GOLD + " GGs!");
                IChatBaseComponent titleJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"You won!\"}");
                IChatBaseComponent subtitleJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"§6§l" + reward + "§6 GGs!\"}");
                PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, 0, 60, 0);
                PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(titlePacket);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitlePacket);
            }

            if (Necessities.isTracking()) {
                for (Rank rank : avgs.keySet()) {
                    Double[] array = avgs.get(rank);
                    array[0] = array[0] / array[2];
                    array[1] = array[1] / array[2];
                    Necessities.trackActionWithValue("AverageReward", rank.getName(), array[1]);
                    Necessities.trackActionWithValue("AverageAir", rank.getName(), array[0]);
                }
            }
            avgs.clear();
            //calculateGlicko(winners, um);
            ArrayList<Player> losers = new ArrayList<>();
            for (UUID id : dead) {
                Player p = Bukkit.getPlayer(id);
                if (p != null)
                    losers.add(p);
            }
            recordMatch(winners, losers, um);
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
                this.nextGame.map = LavaMap.load(files[RANDOM.nextInt(files.length)]);
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

    private void recordMatch(HashMap<Player, Integer> winners, ArrayList<Player> losers, UserManager um) {
        if ((winners == null || winners.isEmpty()) && (losers == null || losers.isEmpty()))
            return; //Do not record a match that no one is in
        String mode = this.getType();
        String winnerList = "{";
        String scoreList = "{";
        String loserList = "{";
        if (winners != null)
            for (Player player : winners.keySet()) {
                winnerList += player.getUniqueId().toString() + ",";
                scoreList += winners.get(player).toString() + ",";
            }
        for (Player player : losers)
            loserList += player.getUniqueId().toString() + ",";
        winnerList = winnerList.substring(0, winnerList.length() - 1) + "}";
        scoreList = scoreList.substring(0, scoreList.length() - 1) + "}";
        loserList = loserList.substring(0, loserList.length() - 1) + "}";
        if (winnerList.length() == 1)
            winnerList = "{}";
        if (scoreList.length() == 1)
            scoreList = "{}";
        if (loserList.length() == 1)
            loserList = "{}";
        YamlConfiguration config = Necessities.getInstance().getConfig();
        String url = "jdbc:mariadb://" + config.getString("Lavasurvival.DBHost") + "/" + config.getString("Lavasurvival.DBTable"), user = config.getString("Lavasurvival.DBUser"),
                pass = config.getString("Lavasurvival.DBPassword");
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("INSERT INTO matches (gamemode, winners, scores, losers) VALUES (\"" + mode + "\", \"" + winnerList + "\", \"" + scoreList + "\", \"" + loserList + "\")");
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ignored) {
        }
        for (Player winner : winners.keySet()) {
            //Rating calculated off of avg blocks around, NOT reward since this is based on rank too
            int blockCount = countAirBlocksAround(winner, 10);
            UserInfo user = um.getUser(winner.getUniqueId());
            user.getRanking().addMatch(blockCount, winner.getUniqueId().toString());
        }
        for (Player loser : losers) {
            UserInfo loserUser = um.getUser(loser.getUniqueId());
            loserUser.getRanking().addMatch(0, loser.getUniqueId().toString());
        }
    }

    /*private void calculateGlicko(HashMap<Player, Integer> winners, UserManager um) {
        for (Player player : winners.keySet()) {
            int reward = winners.get(player);
            UserInfo info = um.getUser(player.getUniqueId());
            for (Player other : winners.keySet()) {
                if (player.equals(other))
                    continue;
                UserInfo otherInfo = um.getUser(other.getUniqueId());
                int otherReward = winners.get(other);
                double result;
                if (reward > otherReward)
                    result = 1; //They won
                else if (reward < otherReward)
                    result = 0; //They lost
                else
                    result = 0.5; //They tied
                info.getRanking().addResult(otherInfo, result);
            }
            for (UUID id : dead) {
                Player p = Bukkit.getPlayer(id);
                if (p == null)
                    continue;
                UserInfo otherInfo = um.getUser(id);
                info.getRanking().addResult(otherInfo, 1.0);
                otherInfo.getRanking().addResult(info, 0.0);
            }
        }
    }*/

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
            Necessities.trackAction(player, this.nextMaps[number].getName());
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
            FancyMessage message = new FancyMessage("");
            for (int i = 0; i < this.nextMaps.length; i++) {
                this.votes[i] = 0; //reset votes
                boolean found;
                String next;
                do {
                    found = false;
                    next = files[RANDOM.nextInt(files.length)];
                    File possibleNext = new File(next);
                    if (currentMap.getFile().equals(possibleNext)) {
                        found = true;
                        continue;
                    }
                    for (LavaMap nextMap : this.nextMaps) {
                        if (nextMap != null && nextMap.getFile().equals(possibleNext)) {
                            found = true;
                            break;
                        }
                    }
                } while (found);
                try {
                    this.nextMaps[i] = LavaMap.load(next);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                message.then((i + 1) + ". " + this.nextMaps[i].getName()).style(ChatColor.UNDERLINE).command("/lvote " + (i + 1)).tooltip("Vote for " + this.nextMaps[i].getName()).then(" ");
            }
            this.voted.clear();
            globalMessage(ChatColor.GREEN + "It's time to vote for the next map!");
            voting = true;
            globalMessage(ChatColor.BOLD + "No talking will be allowed during the vote.");
            globalMessageNoPrefix(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Click the map you want to vote for:");
            globalMessageNoPrefix(" ");
            globalRawMessage(message);
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
                this.nextGame.map = LavaMap.load(files[RANDOM.nextInt(files.length)]);
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
            LAVA = RANDOM.nextInt(100) < 75; //Have water/lava check be in here instead of as argument
        else
            LAVA = option.isLavaEnabled() || !option.isWaterEnabled() && RANDOM.nextInt(100) < 75;
    }

    public void forceEnd() {
        end();
    }

    private void end() {
        this.tickTask.cancel();
        //Bukkit.getScheduler().cancelTasks(Lavasurvival.INSTANCE);
        globalMessage(ChatColor.GREEN + "The round has ended!");
        this.isEnding = false;
        this.hasEnded = true;
    }

    private Gamemode pickRandomGame(LavaMap map) {
        if (map == null) {
            Class<?> nextGameClass = GAMES[RANDOM.nextInt(GAMES.length)];
            try {
                return (Gamemode) nextGameClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Class<? extends Gamemode>[] games = map.getEnabledGames();
            Class<? extends Gamemode> nextGameClass = games[RANDOM.nextInt(games.length)];
            try {
                return nextGameClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void tryNextGame() {
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
        Rank rank = Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(player.getUniqueId()).getRank();
        double bonusAdd = (5 + Lavasurvival.INSTANCE.getRankManager().getOrder().indexOf(rank)) / 2.0;
        //int blockCount = countAirBlocksAround(onlinePlayer, 20);
        //System.out.println(onlinePlayer.getName() + " had " + blockCount + " blocks around them!");
        return base + (bonusAdd * blockCount);
    }

    protected void setNextGame(Gamemode game) {
        this.nextGame = game;
    }

    protected void setNextMap(LavaMap map) {
        if (this.nextGame != null)
            this.nextGame.map = map;
    }

    public void playerJoin(Player player) {
        setAlive(player);
        player.teleport(new Location(getCurrentWorld(), getCurrentMap().getMapSpawn().getX(), getCurrentMap().getMapSpawn().getY(), getCurrentMap().getMapSpawn().getZ()));
        player.setGameMode(GameMode.SURVIVAL);
        player.setMaxHealth(getHealth(Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(player.getUniqueId()).getRank()));
        player.setHealth(player.getMaxHealth());
        player.setGlowing(true);
        UserManager um = Lavasurvival.INSTANCE.getUserManager();
        UserInfo u = um.getUser(player.getUniqueId());
        u.resetGenerosity();
        Inventory inv = player.getInventory();
        for (Material DEFAULT_BLOCK : DEFAULT_BLOCKS) {
            ItemStack toGive = new ItemStack(DEFAULT_BLOCK, 1);
            if (BukkitUtils.hasItem(player.getInventory(), toGive) || u.isInBank(new MaterialData(DEFAULT_BLOCK)))
                continue;
            ItemMeta im = toGive.getItemMeta();
            im.setLore(Arrays.asList("Lava MeltTime: " + PhysicsListener.getLavaMeltTimeAsString(toGive.getData()), "Water MeltTime: " + PhysicsListener.getWaterMeltTimeAsString(toGive.getData())));
            toGive.setItemMeta(im);
            player.getInventory().addItem(toGive);
        }
        if (!player.getInventory().contains(Material.WRITTEN_BOOK))
            player.getInventory().addItem(Lavasurvival.INSTANCE.getRules());
        ShopFactory.validateInventory(inv);
        u.giveBoughtBlocks();
        if (!getCurrentMap().getCreator().equals("")) {
            IChatBaseComponent titleJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"§6Map created by " + getCurrentMap().getCreator() + "\"}");
            PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, 0, 60, 0);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
        }
    }

    public void addBars(Player p) {
        this.bars.forEach(bar -> bar.addPlayer(p));
    }

    public void removeBars(Player p) {
        this.bars.forEach(bar -> bar.removePlayer(p));
    }

    private double getHealth(Rank r) {
        if (r == null)
            return 1;
        switch (Lavasurvival.INSTANCE.getRankManager().getOrder().indexOf(r)) {
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
        Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(uuid).setStatus("alive");
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
        Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(uuid).setStatus("dead");
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
        getCurrentWorld().getPlayers().forEach(p -> p.sendMessage(ChatColor.RED + "[Lavasurvival] " + ChatColor.RESET + message));
    }

    private void globalMessageNoPrefix(String message) {
        getCurrentWorld().getPlayers().forEach(p -> p.sendMessage(message));
    }

    private void globalRawMessage(FancyMessage rawMessage) {
        getCurrentWorld().getPlayers().forEach(rawMessage::send);
    }

    protected Material getMat() {
        return LAVA ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER;
    }

    private boolean isInSpawn(Player player) {
        return player != null && (getCurrentMap().isInSafeZone(player.getLocation()) || getCurrentMap().isInSafeZone(player.getEyeLocation()));
    }

    public boolean allDead() {
        CmdHide hide = Lavasurvival.INSTANCE.getHide();
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

    public abstract void addToBonus(double takeOut);

    protected abstract boolean isRewardDoubled();

    public static void restartNextGame(String serverToJoin) {
        restart = true;
        restartServer = serverToJoin;
    }
}