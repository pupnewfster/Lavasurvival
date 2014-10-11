package me.eddiep.game;

import me.eddiep.Lavasurvival;
import me.eddiep.game.impl.LavaFlood;
import me.eddiep.ranks.Rank;
import me.eddiep.system.FileUtils;
import me.eddiep.system.PhysicsListener;
import me.eddiep.system.PlayerListener;
import mkremins.fanciful.FancyMessage;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;

public abstract class Gamemode {
    public static final Material[] DEFAULT_BLOCKS = new Material[] {
            Material.COBBLESTONE,
            Material.DIRT,
            Material.GRASS,
            Material.WOOD,
            Material.SAND
    };
    public static final ItemStack SHOP_OPENER = new ItemStack(Material.EMERALD, 1);
    public static final Class[] GAMES = new Class[] {
            LavaFlood.class
    };

    public static final int VOTE_COUNT = 3;
    public static LavaMap[] nextMaps = new LavaMap[VOTE_COUNT];
    public static int[] votes = new int[VOTE_COUNT];
    public static boolean voting = false;

    private static LavaMap lastMap;
    private static LavaMap currentmap;
    public static final Random RANDOM = new Random();
    private static Team alive;
    private static Team dead;
    private static Team spec;
    private static Scoreboard scoreboard;
    private static PlayerListener listener;
    private static PhysicsListener physicsListener;
    private static Gamemode currentgame;

    private int tickTask;
    private Gamemode nextGame;
    private LavaMap map;
    protected boolean poured;

    public static PlayerListener getPlayerListener() {
        return listener;
    }

    public void prepare() {
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        if (alive == null) {
            alive = scoreboard.registerNewTeam("Alive");
            alive.setDisplayName("Alive");
            alive.setPrefix(ChatColor.GREEN + "[Alive]");
        }
        if (dead == null) {
            dead = scoreboard.registerNewTeam("Dead");
            dead.setDisplayName("Dead");
            dead.setPrefix(ChatColor.RED + "[Dead]");
        }
        if (spec == null) {
            spec = scoreboard.registerNewTeam("Spectator");
            spec.setDisplayName("Spectator");
            spec.setPrefix(ChatColor.GRAY + "[Spec]");
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
        } else {
            currentmap = map;
        }

        currentmap.prepare();
    }

    public void start() {
        Lavasurvival.log("New game on " + getCurrentWorld().getName());

        clearTeam(alive);
        clearTeam(dead);
        clearTeam(spec);

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player p : players) {
            p.teleport(getCurrentWorld().getSpawnLocation());

            spec.addPlayer(p);
        }

        currentmap.getJoinSign().setLine(0, ChatColor.BOLD + "Right click");
        currentmap.getJoinSign().setLine(1, ChatColor.BOLD + "to join!");
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

        tickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Lavasurvival.INSTANCE, new Runnable() {
            @Override
            public void run() {
                onTick();
            }
        }, 0, 1);
    }

    private void restoreBackup(World world) {
        Lavasurvival.log("Restoring backup of " + world.getName());
        try {
            FileUtils.copyDirectory(new File(Lavasurvival.INSTANCE.getDataFolder(), world.getName()), world.getWorldFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onTick();

    protected abstract double calculateReward(OfflinePlayer player);

    public void endRound() {
        end();

        int amount = alive.getSize();
        if (amount == 0) {
            globalMessage("No one survived..");
        }
        else if (amount <= 45) {
            globalMessage("Congratulations to the survivors!");

            String survivors = "";
            for (OfflinePlayer player : alive.getPlayers()) {
                if (!player.isOnline())
                    continue;

                if (survivors.equals(""))
                    survivors += player.getName();
                else
                    survivors += ", " + player.getName();
            }

            globalMessage(survivors);
        } else {
            globalMessage("Congratulations to all " + amount + " survivors!");
        }

        for (OfflinePlayer player : alive.getPlayers()) {
            if (player.isOnline()) {
                double reward = calculateReward(player);
                Lavasurvival.INSTANCE.getEconomy().depositPlayer(player, reward);



                player.getPlayer().sendMessage(ChatColor.GREEN + "+ " + ChatColor.GOLD + "You won " + ChatColor.BOLD + reward + ChatColor.RESET + "" + ChatColor.GOLD + " GGs!");
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "";
                String[] files = LavaMap.getPossibleMaps();
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

                        for (int z = 0; z < i; z++) {
                            if (nextMaps[z] != null && nextMaps[z].getFilePath().equals(next)) {
                                found = true;
                                break;
                            }
                        }
                    } while(found);

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

                listener.voted.clear();
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
                    if (next == null) {
                        next = nextMaps[i];
                    } else if (votes[i] > highest) {
                        highest = votes[i];
                        next = nextMaps[i];
                    }
                }

                globalMessage(ChatColor.BOLD + next.getName() + ChatColor.RESET + " won the vote!");
                if (nextGame == null)
                    nextGame = pickRandomGame();
                nextGame.map = next;

                Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                    @Override
                    public void run() {
                        lastMap = getCurrentMap();
                        tryNextGame();
                    }
                }, 20);
            }
        }).start();
    }

    public void end() {
        Bukkit.getScheduler().cancelTask(tickTask);
        physicsListener.cancelAllTasks();
        globalMessage(ChatColor.GREEN + "The round has ended!");
    }

    protected Gamemode pickRandomGame() {
        Class<?> nextGameClass = GAMES[RANDOM.nextInt(GAMES.length)];
        try {
            return (Gamemode) nextGameClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
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
        Rank rank = Lavasurvival.INSTANCE.getUserManager().getUser(player.getUniqueId()).getRank();
        double bonusAdd = 8.0 * (1 + Lavasurvival.INSTANCE.getRankManager().getOrder().indexOf(rank));

        Location loc = onlinePlayer.getLocation();

        return base + (bonusAdd * Math.min(recursiveFill(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 0), 100));
    }



    private boolean isLiquid(Material material) {
        return material == Material.WATER || material == Material.STATIONARY_WATER ||
                material == Material.LAVA || material == Material.STATIONARY_LAVA;
    }

    private int recursiveFill(World world, int x, int y, int z, int curBlockCount) {
        if (curBlockCount >= 100) return curBlockCount;

        for (int xadd = -1; xadd <= 1; xadd++) {
            for (int yadd = -1; yadd <= 1; yadd++) {
                for (int zadd = -1; zadd <= 1; zadd++) {
                    if (!world.getBlockAt(x+ xadd, y + yadd, z + zadd).getType().isSolid() && !isLiquid(world.getBlockAt(x + xadd, y + yadd, z + zadd).getType())) {
                        curBlockCount = Math.min(curBlockCount + 1, 100);
                        curBlockCount = recursiveFill(world, x + xadd, y + yadd, z + zadd, curBlockCount);

                        if (curBlockCount >= 100) return curBlockCount;
                    }
                }
            }
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
        globalMessageNoPrefix(ChatColor.GREEN + "+ " + player.getDisplayName() + ChatColor.RESET + " has joined the game!");
    }

    public void setAlive(Player player) {
        if (dead.hasPlayer(player))
            dead.removePlayer(player);
        if (spec.hasPlayer(player))
            spec.removePlayer(player);

        alive.addPlayer(player);
        Lavasurvival.log(player.getName() + " has joined the alive team.");
    }

    public void setDead(Player player) {
        if (alive.hasPlayer(player))
            alive.removePlayer(player);
        if (spec.hasPlayer(player))
            spec.removePlayer(player);

        dead.addPlayer(player);
        Lavasurvival.log(player.getName() + " has joined the dead team.");
    }

    public void setSpectator(Player player) {
        if (dead.hasPlayer(player))
            dead.removePlayer(player);
        if (alive.hasPlayer(player))
            alive.removePlayer(player);

        spec.addPlayer(player);
        Lavasurvival.log(player.getName() + " has joined the spec team.");

    }

    public boolean isAlive(Player player) {
        return alive.hasPlayer(player);
    }

    public boolean isDead(Player player) {
        return dead.hasPlayer(player);
    }

    public boolean isSpectator(Player player) {
        return spec.hasPlayer(player);
    }

    public static void clearTeam(Team team) {
        for (OfflinePlayer p : team.getPlayers()) {
            team.removePlayer(p);
        }
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

    public void globalMessage(String message) {
        for (Player p : getCurrentWorld().getPlayers()) {
            p.sendMessage(ChatColor.RED + "[Lavasurvival] " + ChatColor.RESET + message);
        }
    }

    public void globalMessageNoPrefix(String message) {
        for (Player p : getCurrentWorld().getPlayers()) {
            p.sendMessage(message);
        }
    }

    public void globalRawMessage(FancyMessage rawMessage) {
        for (Player p : getCurrentWorld().getPlayers()) {
            rawMessage.send(p);
        }
    }

    public static void cleanup() {
        clearTeam(alive);
        clearTeam(dead);
        clearTeam(spec);

        listener.cleanup();
        physicsListener.cleanup();
    }
}
