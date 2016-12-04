package me.eddiep.minecraft.ls.system.setup;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.LavaMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

@SuppressWarnings("unused")
public class SetupMap implements Listener {
    private Player setupPlayer;
    private LavaMap map;
    private int step;

    public SetupMap(Player setupPlayer, Plugin plugin) {
        this.setupPlayer = setupPlayer;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.map = new LavaMap();
    }

    public void start() {
        sendMessage("Welcome to the map setup wizard!");
        sendMessage("The first thing we need to do is figure out where the lava will pour.");
        instruction("Place a block where the lava will spawn.");
        this.map.setWorldName(this.setupPlayer.getWorld().getName());
        this.step = 0;
    }

    private void step2() {
        sendMessage("Ok.");
        sendMessage("Next, I need to know the height of the map, relative to the lava spawn.");
        instruction("Find a spot on the map where there are no blocks blocking a straight path to the floor of the map and say 'ready' in chat.");
        this.step = 1;
    }

    private void step3() {
        sendMessage("Ok.");
        sendMessage("Next, I need to know the safe zone bounds and spawn.");
        instruction("Stand where you want the safe zone spawn to be, then say 'ready' in chat.");
        this.step = 2;
    }

    private void step4() {
        sendMessage("Got it!");
        instruction("Lastly, what's the name of this map? Say it in the chat.");
        this.step = 3;
    }

    private void finish() {
        sendMessage("Saving..");
        try {
            this.map.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMessage("All set!");
        Lavasurvival.INSTANCE.removeFromSetup(this.setupPlayer.getUniqueId());
        end();
    }

    public void end() {
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        BlockPlaceEvent.getHandlerList().unregister(this);
        this.setupPlayer = null;
        this.step = 0;
        this.map = null;
    }

    public void sendMessage(String message) {
        this.setupPlayer.sendMessage(ChatColor.YELLOW + "[Map Setup] " + ChatColor.RESET + message);
    }

    private void instruction(String message) {
        this.setupPlayer.sendMessage(ChatColor.YELLOW + "[Map Setup] " + ChatColor.GREEN + message);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().equals(this.setupPlayer) && event.getMessage().equalsIgnoreCase("ready")) {
            switch (step) {
                case 1:
                    event.setCancelled(true);
                    sendMessage("Please wait..");
                    int x = this.setupPlayer.getLocation().getBlockX();
                    int z = this.setupPlayer.getLocation().getBlockZ();
                    int startY = this.setupPlayer.getLocation().getBlockY();
                    while (!this.setupPlayer.getWorld().getBlockAt(x, startY, z).getType().isSolid() && startY > 0)
                        startY--;
                    int dif = this.map.getLavaY() - startY;
                    this.map.setHeight(dif);
                    step3();
                    break;
                case 2:
                    event.setCancelled(true);
                    sendMessage("Please wait..");
                    this.map.setMapSpawn(event.getPlayer().getLocation());
                    World world = event.getPlayer().getWorld();
                    double minX, maxX;
                    double minY, maxY;
                    double minZ, maxZ;

                    minX = maxX = event.getPlayer().getLocation().getX();
                    minY = maxY = event.getPlayer().getLocation().getY();
                    minZ = maxZ = event.getPlayer().getLocation().getZ();

                    Location temp = new Location(world, minX, minY, minZ);
                    while (world.isChunkLoaded(temp.getChunk()) && !world.getBlockAt(temp).getType().isSolid())
                        temp.setX(temp.getX() - 1);
                    temp.setX(temp.getX() + 1);
                    while (!world.getBlockAt(temp).getType().isSolid() && temp.getY() > 0)
                        temp.setY(temp.getY() - 1);
                    temp.setY(temp.getY() + 1);
                    while (world.isChunkLoaded(temp.getChunk()) && !world.getBlockAt(temp).getType().isSolid())
                        temp.setZ(temp.getZ() - 1);
                    temp.setY(temp.getY() - 1);
                    temp.setX(temp.getX() - 1);

                    Location temp2 = new Location(world, maxX, maxY, maxZ);
                    while (!world.isChunkLoaded(temp2.getChunk()) && world.getBlockAt(temp2).getType().isSolid())
                        temp2.setX(temp2.getX() + 1);
                    temp2.setX(temp2.getX() - 1);
                    while (!world.getBlockAt(temp2).getType().isSolid() && temp2.getY() < world.getMaxHeight() - 1)
                        temp2.setY(temp2.getY() + 1);
                    temp2.setY(temp2.getY() - 1);
                    while (world.isChunkLoaded(temp2.getChunk()) && !world.getBlockAt(temp2).getType().isSolid())
                        temp2.setZ(temp2.getZ() + 1);
                    temp2.setY(temp2.getY() + 1);
                    temp2.setX(temp2.getX() + 1);

                    this.map.setSafeZoneBounds(temp, temp2);

                    step4();
                    break;
            }
        } else if (event.getPlayer().equals(this.setupPlayer) && this.step == 3) {
            event.setCancelled(true);
            this.map.setName(event.getMessage());
            sendMessage(this.map.getName() + "? Got it.");
            finish();
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getPlayer().equals(this.setupPlayer) && this.step == 0) {
            event.setCancelled(true);
            this.map.setLavaSpawn(event.getBlock().getLocation());
            step2();
        }
    }
}