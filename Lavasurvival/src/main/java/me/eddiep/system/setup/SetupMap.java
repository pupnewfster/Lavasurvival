package me.eddiep.system.setup;

import me.eddiep.Lavasurvival;
import me.eddiep.game.LavaMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class SetupMap implements Listener {
    private Player setupPlayer;
    private int step;
    private LavaMap map;

    public SetupMap(Player setupPlayer, Plugin plugin) {
        this.setupPlayer = setupPlayer;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        map = new LavaMap();
    }

    public void start() {
        sendMessage("Welcome to the map setup wizard!");
        sendMessage("The first thing we need to do is figure out where the lava will pour.");
        instruction("Place a block where the lava will spawn.");
        map.setWorldName(setupPlayer.getWorld().getName());
        step = 0;
    }

    private void step2() {
        sendMessage("Ok.");
        sendMessage("Next, I need to know the height of the map, relative to the lava spawn.");
        instruction("Find a spot on the map where there are no blocks blocking a straight path to the floor of the map and say 'ready' in chat.");
        step = 1;
    }

    private void step3() {
        sendMessage("Ok.");
        sendMessage("Next, I need to know the safe zone bounds and spawn.");
        instruction("Stand where you want the safe zone spawn to be, then say 'ready' in chat.");
        step = 2;
    }

    private void step4() {
        sendMessage("Got it!");
        instruction("What's the name of this map? Say it in the chat.");
        step = 3;
    }

    private void step5() {
        instruction("Lastly, break the sign players will be using to join!");
        step = 4;
    }

    private void finish() {
        sendMessage("Saving..");

        try {
            map.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMessage("All set!");
        Lavasurvival.INSTANCE.removeFromSetup(setupPlayer.getUniqueId());
        end();
    }

    public void end() {
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        BlockPlaceEvent.getHandlerList().unregister(this);
        setupPlayer = null;
        step = 0;
        map = null;
    }

    public void sendMessage(String message) {
        setupPlayer.sendMessage(ChatColor.YELLOW + "[Map Setup] " + ChatColor.RESET + message);
    }

    public void instruction(String message) {
        setupPlayer.sendMessage(ChatColor.YELLOW + "[Map Setup] " + ChatColor.GREEN + message);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().equals(setupPlayer) && event.getMessage().equalsIgnoreCase("ready")) {
            switch (step) {
                case 1:
                    event.setCancelled(true);

                    sendMessage("Please wait..");
                    int x = setupPlayer.getLocation().getBlockX();
                    int z = setupPlayer.getLocation().getBlockZ();
                    int startY = setupPlayer.getLocation().getBlockY();
                    while (!setupPlayer.getWorld().getBlockAt(x, startY, z).getType().isSolid()) {
                        startY--;
                    }

                    int dif = map.getLavaY() - startY;
                    map.setHeight(dif);

                    step3();
                    break;
                case 2:
                    event.setCancelled(true);
                    sendMessage("Please wait..");

                    map.setMapSpawn(event.getPlayer().getLocation());
                    World world = event.getPlayer().getWorld();
                    double minx, maxx;
                    double miny, maxy;
                    double minz, maxz;

                    minx = maxx = event.getPlayer().getLocation().getX();
                    miny = maxy = event.getPlayer().getLocation().getY();
                    minz = maxz = event.getPlayer().getLocation().getZ();

                    Location temp = new Location(world, minx, miny, minz);
                    while (!world.getBlockAt(temp).getType().isSolid()) {
                        temp.setX(temp.getX() - 1);
                    }
                    temp.setX(temp.getX() + 1);
                    while (!world.getBlockAt(temp).getType().isSolid()) {
                        temp.setY(temp.getY() - 1);
                    }
                    temp.setY(temp.getY() + 1);
                    while (!world.getBlockAt(temp).getType().isSolid()) {
                        temp.setZ(temp.getZ() - 1);
                    }
                    temp.setY(temp.getY() - 1);
                    temp.setX(temp.getX() - 1);


                    Location temp2 = new Location(world, maxx, maxy, maxz);
                    while (!world.getBlockAt(temp2).getType().isSolid()) {
                        temp2.setX(temp2.getX() + 1);
                    }
                    temp2.setX(temp2.getX() - 1);
                    while (!world.getBlockAt(temp2).getType().isSolid()) {
                        temp2.setY(temp2.getY() + 1);
                    }
                    temp2.setY(temp2.getY() - 1);
                    while (!world.getBlockAt(temp2).getType().isSolid()) {
                        temp2.setZ(temp2.getZ() + 1);
                    }
                    temp2.setY(temp2.getY() + 1);
                    temp2.setX(temp2.getX() + 1);

                    map.setSafeZoneBounds(temp, temp2);

                    step4();
                    break;
            }
        } else if (event.getPlayer().equals(setupPlayer) && step == 3) {
            event.setCancelled(true);
            map.setName(event.getMessage());
            sendMessage(map.getName() + "? Got it.");

            step5();
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().equals(setupPlayer) && step == 4) {
            event.setCancelled(true);
            if (event.getBlock().getState() instanceof Sign) {
                map.setSignLocation(event.getBlock().getLocation());

                finish();
            } else {
                sendMessage("That's not a sign..");
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getPlayer().equals(setupPlayer) && step == 0) {
            event.setCancelled(true);
            map.setLavaSpawn(event.getBlock().getLocation());
            step2();
        }
    }
}
