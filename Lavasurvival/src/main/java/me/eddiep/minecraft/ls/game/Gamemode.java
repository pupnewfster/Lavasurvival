package me.eddiep.minecraft.ls.game;

import com.crossge.necessities.GetUUID;
import com.crossge.necessities.RankManager.Rank;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.impl.Flood;
import me.eddiep.minecraft.ls.game.impl.Rise;
import me.eddiep.minecraft.ls.ranks.UserInfo;
import me.eddiep.minecraft.ls.ranks.UserManager;
import me.eddiep.minecraft.ls.system.FileUtils;
import me.eddiep.minecraft.ls.system.PhysicsListener;
import me.eddiep.minecraft.ls.system.PlayerListener;
import mkremins.fanciful.FancyMessage;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class Gamemode {
    public static final Material[] DEFAULT_BLOCKS = new Material[]{
            Material.TORCH,
            Material.COBBLESTONE,
            Material.DIRT,
            Material.GRASS,
            Material.WOOD,
            Material.SAND
    };
    public static final Class[] GAMES = new Class[] {
            Rise.class,
            Flood.class
    };

    public static final int VOTE_COUNT;
    static {
        if (LavaMap.getPossibleMaps().length == 0)
            VOTE_COUNT = 0;
        else
            VOTE_COUNT = LavaMap.getPossibleMaps().length <= 3 ? LavaMap.getPossibleMaps().length - 1 : 3;
    }

    public static final Random RANDOM = new Random();
    public static double WATER_DAMAGE = 0;
    public static double DAMAGE_FREQUENCY = 3;
    public static boolean LAVA = true;

    private static boolean voting = false;
    private static LavaMap[] nextMaps = new LavaMap[VOTE_COUNT];
    private static int[] votes = new int[VOTE_COUNT];
    private static LavaMap lastMap;
    private static LavaMap currentmap;
    private static Team alive;
    private static Team dead;
    private static Team spec;
    private static Scoreboard scoreboard;
    private static PlayerListener listener;
    private static PhysicsListener physicsListener;
    private static Gamemode currentgame = null;
    protected boolean poured;
    private int tickTask;
    private Gamemode nextGame;
    private LavaMap map;

    public static PlayerListener getPlayerListener() {
        return listener;
    }

    public static void clearTeam(Team team) {
        for (String p : team.getEntries())
            team.removeEntry(p);
    }

    public static LavaMap getCurrentMap() {
        return currentmap;
    }

    public static World getCurrentWorld() {
        return currentmap.getWorld();
    }

    public static Gamemode getCurrentGame() {
        return currentgame;
    }

    public static Scoreboard getScoreboard() {
        return scoreboard;
    }

    public static void cleanup() {
        clearTeam(alive);
        clearTeam(dead);
        clearTeam(spec);

        listener.cleanup();
        physicsListener.cleanup();
    }

    public void prepare() {
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();//.getNewScoreboard();
        }
        if (alive == null) {
            if (scoreboard.getTeam("Alive") == null)
                alive = scoreboard.registerNewTeam("Alive");
            else
                alive = scoreboard.getTeam("Alive");
            alive.setDisplayName("Alive");
            alive.setPrefix(ChatColor.GREEN + "[Alive] ");
        }
        if (dead == null) {
            if (scoreboard.getTeam("Dead") == null)
                dead = scoreboard.registerNewTeam("Dead");
            else
                dead = scoreboard.getTeam("Dead");
            dead.setDisplayName("Dead");
            dead.setPrefix(ChatColor.RED + "[Dead] ");
        }
        if (spec == null) {
            if (scoreboard.getTeam("Spectator") == null)
                spec = scoreboard.registerNewTeam("Spectator");
            else
                spec = scoreboard.getTeam("Spectator");
            spec.setDisplayName("Spectator");
            spec.setPrefix(ChatColor.GRAY + "[Spec] ");
        }
        if (listener == null) {
            listener = new PlayerListener();
            Lavasurvival.INSTANCE.getServer().getPluginManager().registerEvents(listener, Lavasurvival.INSTANCE);
        }
        if (physicsListener == null) {
            physicsListener = new PhysicsListener();
            Lavasurvival.INSTANCE.getServer().getPluginManager().registerEvents(physicsListener, Lavasurvival.INSTANCE);
        }

        if (map == null) {
            String[] files = LavaMap.getPossibleMaps();
            lastMap = currentmap;

            do {
                String next = files[RANDOM.nextInt(files.length)];
                try {
                    currentmap = LavaMap.load(next);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (true);
        } else
            currentmap = map;

        currentmap.prepare();
    }

    private long lastMoneyCheck = System.currentTimeMillis();
    public void start() {
        Lavasurvival.log("New game on " + getCurrentWorld().getName());
        isEnding = false;
        hasEnded = false;

        LAVA = RANDOM.nextInt(100) < 75; //Have water/lava check be in here instead of as arguement
        WATER_DAMAGE = LAVA ? 0 : 2;

        clearTeam(alive);
        clearTeam(dead);
        clearTeam(spec);

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player p : players) {
            playerJoin(p);
            //p.teleport(getCurrentWorld().getSpawnLocation());

            //spec.addEntry(p.getName());
        }

        currentmap.getJoinSign().setLine(0, ChatColor.BOLD + "Right click");
        currentmap.getJoinSign().setLine(1, ChatColor.BOLD + "to join!");
        currentmap.getJoinSign().setLine(2, "");
        currentmap.getJoinSign().setLine(3, "..or use /join");
        currentmap.getJoinSign().update();
        currentgame = this;

        if (lastMap != null) {
            Lavasurvival.log("Unloading " + lastMap.getWorld().getName() + "..");
            boolean success = Bukkit.unloadWorld(lastMap.getWorld(), false);
            if (!success)
                Lavasurvival.log("Failed to unload last map! A manual unload may be required..");
            else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        restoreBackup(lastMap.getWorld());
                    }
                }).start();
            }
        }

        Lavasurvival.INSTANCE.MONEY_VIEWER.run();
        tickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Lavasurvival.INSTANCE, new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastMoneyCheck >= 20000) {
                    Lavasurvival.INSTANCE.MONEY_VIEWER.run();
                    lastMoneyCheck = System.currentTimeMillis();
                }
                onTick();
            }
        }, 0, 1);
    }

    private void restoreBackup(World world) {
        Lavasurvival.log("Restoring backup of " + world.getName());
        try {
            FileUtils.copyDirectory(new File(Lavasurvival.INSTANCE.getDataFolder(), world.getName()), world.getWorldFolder());
            new File(Lavasurvival.INSTANCE.getDataFolder(), world.getName()).delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onTick();

    protected abstract double calculateReward(Player player);

    protected boolean isEnding;
    protected boolean hasEnded;
    public void endRoundIn(long seconds) {
        if (isEnding)
            return;

        isEnding = true;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
            @Override
            public void run() {
                endRound();
            }
        }, seconds * 20L);
    }

    public boolean hasEnded() {
        return hasEnded;
    }

    public void endRound() {
        if (hasEnded)
            return;

        end();
        UserManager um = Lavasurvival.INSTANCE.getUserManager();
        GetUUID get = Lavasurvival.INSTANCE.getUUIDs();
        for (UserInfo u : um.getUsers().values())
            u.clearBlocks();

        int amount = alive.getSize();
        if (amount == 0) {
            globalMessage("No one survived..");
        } else if (amount <= 45) {
            globalMessage("Congratulations to the survivors!");
            String survivors = "";
            for (String name : alive.getEntries()) {
                if (name == null)
                    continue;
                UUID id = get.getID(name);
                if (id == null)
                    id = get.getOfflineID(name);
                if (id == null || Bukkit.getPlayer(id) == null)
                    continue;
                if (survivors.equals(""))
                    survivors += name;
                else
                    survivors += ", " + name;
            }

            globalMessage(survivors);
        } else {
            globalMessage("Congratulations to all " + amount + " survivors!");
        }

        for (String name : alive.getEntries()) {
            if (name == null)
                continue;
            UUID id = get.getID(name);
            if (id == null)
                id = get.getOfflineID(name);
            Player player = Bukkit.getPlayer(id);
            if (id == null || player == null)
                continue;
            double reward = calculateReward(player);
            Lavasurvival.INSTANCE.getEconomy().depositPlayer(player, reward);
            player.getPlayer().sendMessage(ChatColor.GREEN + "+ " + ChatColor.GOLD + "You won " + ChatColor.BOLD + reward + ChatColor.RESET + "" + ChatColor.GOLD + " GGs!");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                startVoting();
            }
        }).start();
    }

    public List<LavaMap> getMapsInVote() {
        return Collections.unmodifiableList(Arrays.asList(nextMaps));
    }

    public boolean hasVoted(Player player) {
        return voted.contains(player);
    }

    public void voteFor(int index) {
        votes[index]++;
    }

    private ArrayList<OfflinePlayer> voted = new ArrayList<OfflinePlayer>();
    public void voteFor(int number, Player player) {
        if (number >= Gamemode.nextMaps.length) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid number! Please choose a number between (1 - " + Gamemode.nextMaps.length + ").");
            return;
        }
        voted.add(player);
        Gamemode.votes[number]++;
        player.sendMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + "" + ChatColor.BOLD + "You voted for " + Gamemode.nextMaps[number].getName() + "!");
    }

    public boolean isVoting() {
        return voting;
    }

    public void startVoting() {
        if (voting)
            return;

        String[] files = LavaMap.getPossibleMaps();
        if (files.length > 1) {
            FancyMessage message = new FancyMessage("");
            for (int i = 0; i < nextMaps.length; i++) {
                votes[i] = 0; //reset votes
                boolean found;
                String next;

                do {
                    found = false;
                    next = files[RANDOM.nextInt(files.length)];
                    if (currentmap.getFilePath().equals(next))
                        continue;

                    for (int z = 0; z < i; z++)
                        if (nextMaps[z] != null && nextMaps[z].getFilePath().equals(next)) {
                            found = true;
                            break;
                        }
                } while (found);

                try {
                    nextMaps[i] = LavaMap.load(next);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                message.then((i + 1) + ". " + nextMaps[i].getName())
                        .style(ChatColor.UNDERLINE)
                        .command("/lvote " + (i + 1))
                        .tooltip("Vote for " + nextMaps[i].getName())
                        .then(" ");

            }

            voted.clear();
            globalMessage(ChatColor.GREEN + "It's time to vote for the next map!");
            voting = true;
            globalMessage(ChatColor.BOLD + "No talking will be allowed during the vote.");

            globalMessageNoPrefix(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Click the map you want to vote for:");
            globalMessageNoPrefix(" ");
            globalRawMessage(message);
            globalMessageNoPrefix(" ");

            try {
                Thread.sleep(50000);
            } catch (InterruptedException ignored) {
            }

            voting = false;
            LavaMap next = null;
            int highest = 0;

            for (int i = 0; i < nextMaps.length; i++) {
                if (next == null)
                    next = nextMaps[i];
                else if (votes[i] > highest) {
                    highest = votes[i];
                    next = nextMaps[i];
                }
            }

            if(next != null)
                globalMessage(ChatColor.BOLD + next.getName() + ChatColor.RESET + " won the vote!");
            if (nextGame == null)
                nextGame = pickRandomGame();
            nextGame.map = next;
        } else {
            try {
                if (nextGame == null)
                    nextGame = pickRandomGame();
                nextGame.map = LavaMap.load(files[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
            @Override
            public void run() {
                lastMap = getCurrentMap();
                tryNextGame();
            }
        }, 20);
    }

    public void forceEnd() {
        end();
    }

    private void end() {
        Bukkit.getScheduler().cancelTasks(Lavasurvival.INSTANCE);
        globalMessage(ChatColor.GREEN + "The round has ended!");
        isEnding = false;
        hasEnded = true;
    }

    protected Gamemode pickRandomGame() {
        Class<?> nextGameClass = GAMES[RANDOM.nextInt(GAMES.length)];
        try {
            return (Gamemode) nextGameClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void tryNextGame() {
        if (nextGame != null) {
            globalMessage("Preparing next game..");
            nextGame.prepare();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                @Override
                public void run() {
                    nextGame.start();
                }
            }, 40); //2 seconds
        }
    }

    protected double getDefaultReward(OfflinePlayer player) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null)
            return 0.0;

        double base = 100.0;
        Rank rank = Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(player.getUniqueId()).getRank();
        double bonusAdd = 8.0 * (1 + Lavasurvival.INSTANCE.getRankManager().getOrder().indexOf(rank));

        Location loc = onlinePlayer.getLocation();

        return base + (bonusAdd * Math.min(recursiveFill(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 0), 100));
    }

    private int recursiveFill(World world, int x, int y, int z, int curBlockCount) {
        if (curBlockCount >= 100)
            return curBlockCount;

        for (int xadd = -1; xadd <= 1; xadd++)
            for (int yadd = -1; yadd <= 1; yadd++)
                for (int zadd = -1; zadd <= 1; zadd++)
                    if (!world.getBlockAt(x + xadd, y + yadd, z + zadd).getType().isSolid() && !world.getBlockAt(x + xadd, y + yadd, z + zadd).isLiquid()) {
                        curBlockCount = Math.min(curBlockCount + 1, 100);
                        curBlockCount = recursiveFill(world, x + xadd, y + yadd, z + zadd, curBlockCount);

                        if (curBlockCount >= 100)
                            return curBlockCount;
                    }

        return curBlockCount;
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
        player.setHealth(player.getMaxHealth());
        globalMessageNoPrefix(ChatColor.GREEN + "+ " + player.getDisplayName() + ChatColor.RESET + " has joined the game!");

        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getUniqueId());
        u.giveBoughtBlocks(); //Always do this
    }

    public void setAlive(Player player) {
        if (player == null)
            return;
        String name = player.getName();
        if (dead.hasEntry(name))
            dead.removeEntry(name);
        if (spec.hasEntry(name))
            spec.removeEntry(name);

        alive.addEntry(name);
        Lavasurvival.log(name + " has joined the alive team.");
    }

    public void setDead(Player player) {
        if (player == null)
            return;
        String name = player.getName();
        if (alive.hasEntry(name))
            alive.removeEntry(name);
        if (spec.hasEntry(name))
            spec.removeEntry(name);

        dead.addEntry(name);
        Lavasurvival.log(name + " has joined the dead team.");
    }

    public void setSpectator(Player player) {
        if (player == null)
            return;
        String name = player.getName();
        if (dead.hasEntry(name))
            dead.removeEntry(name);
        if (alive.hasEntry(name))
            alive.removeEntry(name);

        spec.addEntry(name);
        Lavasurvival.log(name + " has joined the spec team.");

    }

    public boolean isAlive(Player player) {
        return player != null && alive.hasEntry(player.getName());
    }

    public boolean isDead(Player player) {
        return player != null && dead.hasEntry(player.getName());
    }

    public boolean isSpectator(Player player) {
        return player != null && spec.hasEntry(player.getName());
    }

    public void globalMessage(String message) {
        for (Player p : getCurrentWorld().getPlayers())
            p.sendMessage(ChatColor.RED + "[Lavasurvival] " + ChatColor.RESET + message);
    }

    public void globalMessageNoPrefix(String message) {
        for (Player p : getCurrentWorld().getPlayers())
            p.sendMessage(message);
    }

    public void globalRawMessage(FancyMessage rawMessage) {
        for (Player p : getCurrentWorld().getPlayers())
            rawMessage.send(p);
    }

    protected Material getMat() {
        return LAVA ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER;
    }
}