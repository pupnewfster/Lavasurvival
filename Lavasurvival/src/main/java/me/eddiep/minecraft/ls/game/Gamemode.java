package me.eddiep.minecraft.ls.game;

import com.crossge.necessities.GetUUID;
import com.crossge.necessities.RankManager.Rank;
import me.eddiep.handles.ClassicPhysicsEvent;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.impl.Flood;
import me.eddiep.minecraft.ls.game.impl.Rise;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import me.eddiep.minecraft.ls.ranks.UserInfo;
import me.eddiep.minecraft.ls.ranks.UserManager;
import me.eddiep.minecraft.ls.system.BukkitUtils;
import me.eddiep.minecraft.ls.system.FileUtils;
import me.eddiep.minecraft.ls.system.PhysicsListener;
import me.eddiep.minecraft.ls.system.PlayerListener;
import mkremins.fanciful.FancyMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class Gamemode {
    public static final Material[] DEFAULT_BLOCKS = new Material[] {
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
    public static double DAMAGE = 3, DAMAGE_FREQUENCY = 0.5;
    public static boolean LAVA = true, voting = false;
    private LavaMap[] nextMaps = new LavaMap[VOTE_COUNT];
    private int[] votes = new int[VOTE_COUNT];
    private int voteCount;
    private static LavaMap lastMap, currentMap;
    private static Team alive, dead;
    private static Scoreboard scoreboard;
    private static PlayerListener listener;
    private static PhysicsListener physicsListener;
    private static Gamemode currentGame = null;
    protected boolean poured;
    private BukkitRunnable tickTask;
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
        clearTeam(alive);
        clearTeam(dead);
        if (listener != null)
            listener.cleanup();
        if (physicsListener != null)
            physicsListener.cleanup();
    }

    public void prepare() {
        if (scoreboard == null)
            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
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
        if (listener == null) {
            listener = new PlayerListener();
            Lavasurvival.INSTANCE.getServer().getPluginManager().registerEvents(listener, Lavasurvival.INSTANCE);
        }
        if (physicsListener == null) {
            physicsListener = new PhysicsListener();
            Lavasurvival.INSTANCE.getServer().getPluginManager().registerEvents(physicsListener, Lavasurvival.INSTANCE);
        }
        physicsListener.prepare();

        if (map == null) {
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
            currentMap = map;

        currentMap.prepare();
    }

    private long lastMoneyCheck = System.currentTimeMillis();
    public void start() {
        Lavasurvival.log("New game on " + getCurrentWorld().getName());
        isEnding = false;
        hasEnded = false;

        LAVA = RANDOM.nextInt(100) < 75; //Have water/lava check be in here instead of as arguement

        clearTeam(alive);
        clearTeam(dead);

        for (Player p : Bukkit.getOnlinePlayers())
            playerJoin(p);

        currentGame = this;

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
        tickTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastMoneyCheck >= 20000) {
                    Lavasurvival.INSTANCE.MONEY_VIEWER.run();
                    lastMoneyCheck = System.currentTimeMillis();
                }
                onTick();
            }
        };
        tickTask.runTaskTimer(Lavasurvival.INSTANCE, 0, 20);//Possibly should make async
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

    protected int countAirBlocksAround(Player player, int limit) {
        return airBlocksAround(player.getLocation(), player.getLocation(), limit, new ArrayList<Block>());
    }

    protected int airBlocksAround(Location original, Location location, int limit, List<Block> alreadyChecked) {
        if (original.toVector().distanceSquared(location.toVector()) >= limit)
            return 1;

        int total = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block check = location.clone().add(x, y, z).getBlock();
                    if (alreadyChecked.contains(check))
                        continue;
                    if (!check.getType().isSolid() && !check.isLiquid()) {
                        alreadyChecked.add(check);
                        total += airBlocksAround(original, check.getLocation(), limit, alreadyChecked) + 1;
                    }
                }
            }
        }

        return total;
    }

    protected boolean isEnding, hasEnded;
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

        Lavasurvival.INSTANCE.MONEY_VIEWER.run();
        lastMoneyCheck = System.currentTimeMillis();

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
        voteCount++;
    }

    private ArrayList<OfflinePlayer> voted = new ArrayList<>();
    public void voteFor(int number, Player player) {
        if (number >= nextMaps.length) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid number! Please choose a number between (1 - " + nextMaps.length + ").");
            return;
        }
        voted.add(player);
        votes[number]++;
        voteCount++;
        player.sendMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + "" + ChatColor.BOLD + "You voted for " + nextMaps[number].getName() + "!");
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

                    File possibleNext = new File(next);

                    if (currentMap.getFile().equals(possibleNext)) {
                        found = true;
                        continue;
                    }

                    for (LavaMap nextMap : nextMaps) {
                        if (nextMap != null && nextMap.getFile().equals(possibleNext)) {
                            found = true;
                            break;
                        }
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

            long start = System.currentTimeMillis();
            while (true) {
                long cur = System.currentTimeMillis();
                if (voteCount >= Bukkit.getServer().getOnlinePlayers().size() || cur - start >= 50000)
                    break;

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            voting = false;
            voteCount = 0;
            LavaMap next = null;
            int highest = 0;

            for (int i = 0; i < nextMaps.length; i++) {
                globalMessage(ChatColor.BOLD + nextMaps[i].getName() + ChatColor.RESET + " - " + votes[i] + " votes");

                if (next == null) {
                    next = nextMaps[i];
                    highest = votes[i];
                }
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
        tickTask.cancel();
        Bukkit.getScheduler().cancelTasks(Lavasurvival.INSTANCE);
        ClassicPhysicsEvent.getHandlerList().unregister(physicsListener);
        physicsListener = null;
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
        double bonusAdd = 5.0 + (1 + Lavasurvival.INSTANCE.getRankManager().getOrder().indexOf(rank));

        int blockCount = countAirBlocksAround(onlinePlayer, 20);
        System.out.println(onlinePlayer.getName() + " had " + blockCount + " blocks around them!");

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
        player.setHealth(player.getMaxHealth());
        globalMessageNoPrefix(ChatColor.GREEN + "+ " + player.getDisplayName() + ChatColor.RESET + " has joined the game!");

        Inventory inv = player.getInventory();
        for (Material DEFAULT_BLOCK : DEFAULT_BLOCKS) {
            ItemStack toGive = new ItemStack(DEFAULT_BLOCK, 1);
            if (BukkitUtils.hasItem(player.getInventory(), toGive))
                continue;
            ItemMeta im = toGive.getItemMeta();
            im.setLore(Arrays.asList("Melt time: " + PhysicsListener.getMeltTimeAsString(new MaterialData(toGive.getType()))));
            toGive.setItemMeta(im);
            player.getInventory().addItem(toGive);
        }
        if (!player.getInventory().containsAtLeast(Lavasurvival.INSTANCE.getRules(), 1))
            player.getInventory().addItem(Lavasurvival.INSTANCE.getRules());
        ShopFactory.validateInventory(inv);
        UserManager um = Lavasurvival.INSTANCE.getUserManager();
        UserInfo u = um.getUser(player.getUniqueId());
        if (u != null)
            u.giveBoughtBlocks();
    }

    public void setAlive(Player player) {
        if (player == null)
            return;
        String name = player.getName();
        if (dead.hasEntry(name))
            dead.removeEntry(name);
        alive.addEntry(name);
        player.setGameMode(GameMode.SURVIVAL);
        Lavasurvival.log(name + " has joined the alive team.");
    }

    public void setDead(Player player) {
        if (player == null)
            return;
        String name = player.getName();
        if (alive.hasEntry(name))
            alive.removeEntry(name);
        dead.addEntry(name);
        player.setGameMode(GameMode.SPECTATOR);
        Lavasurvival.log(name + " has joined the dead team.");
        if (alive.getSize() == 0)
            endRound();
    }

    public boolean isAlive(Player player) {
        return player != null && alive.hasEntry(player.getName());
    }

    public boolean isDead(Player player) {
        return player != null && dead.hasEntry(player.getName());
    }

    public boolean isInGame(Player player) {
         return player != null && (alive.hasEntry(player.getName()) || dead.hasEntry(player.getName()));
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