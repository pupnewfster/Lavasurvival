package me.eddiep.system;

import me.eddiep.Lavasurvival;
import me.eddiep.game.Gamemode;
import me.eddiep.game.shop.ShopFactory;
import me.eddiep.ranks.UUIDs;
import me.eddiep.ranks.UserInfo;
import me.eddiep.ranks.UserManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerListener implements Listener {
    public boolean survival = false;
    public ArrayList<OfflinePlayer> voted = new ArrayList<OfflinePlayer>();
    public final ArrayList<Material> invalidBlocks = new ArrayList<Material>(Arrays.asList(new Material[] {
            Material.OBSIDIAN,
            Material.IRON_DOOR,
            Material.IRON_DOOR_BLOCK,
            Material.STONE_PLATE,
            Material.GOLD_PLATE,
            Material.IRON_PLATE,
            Material.WOOD_PLATE,
            Material.BEDROCK
    }));
    UserManager um = Lavasurvival.INSTANCE.getUserManager();
    UUIDs get = Lavasurvival.INSTANCE.getUUIDs();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UserInfo u = um.getUser(player.getUniqueId());
        if(u.getRank() != null)
            event.setFormat(ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle()) + " " + player.getName() + ": " + event.getMessage());
        if (Gamemode.getCurrentGame() != null && Gamemode.voting) {
            event.setCancelled(true);
            if (voted.contains(player)) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You already voted!");
                return;
            }
            try {
                int number = Integer.parseInt(event.getMessage());
                number--;
                if (number >= Gamemode.nextMaps.length) {
                    player.sendMessage(ChatColor.DARK_RED + "Invalid number! Please choose a number between (1 - " + Gamemode.nextMaps.length + ").");
                    return;
                }
                voted.add(player);
                Gamemode.votes[number]++;
                player.sendMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + "" + ChatColor.BOLD + "You voted for " + Gamemode.nextMaps[number].getName() + "!");
                return;
            } catch (Throwable t) {
                String map = event.getMessage();
                for (int i = 0; i < Gamemode.nextMaps.length; i++) {
                    if (map.equalsIgnoreCase(Gamemode.nextMaps[i].getName())) {
                        voted.add(player);
                        Gamemode.votes[i]++;
                        player.sendMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + "" + ChatColor.BOLD + "You voted for " + Gamemode.nextMaps[i].getName() + "!");
                        return;
                    }
                }
            }

            if (!player.isOp())
                player.sendMessage(ChatColor.RED + "No talking during the vote!");
            else
                event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
        Material material = event.getBlock().getType();
        if (invalidBlocks.contains(material) ||
                (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer())))
            return;
        if(Gamemode.getCurrentGame() != null) {
            String state = Gamemode.getCurrentGame().isDead(event.getPlayer()) ? ChatColor.RED + "DEAD" : ChatColor.GRAY + "SPECTATING";
            event.getPlayer().sendMessage("You are " + state + ChatColor.RESET + ". You cannot delete or place blocks!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract(PlayerInteractEvent event) {
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                    event.getClickedBlock().getState() instanceof Sign &&
                    ((Sign)event.getClickedBlock().getState()).getLine(0).contains("Right click")) {
                Gamemode.getCurrentGame().playerJoin(event.getPlayer());
            }
        } else if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isDead(event.getPlayer())) {
            event.setCancelled(true);
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                    event.getClickedBlock().getState() instanceof Sign &&
                    ((Sign)event.getClickedBlock().getState()).getLine(0).contains("Right click")) {
                event.getPlayer().sendMessage("Sorry you can't join, you're dead :/");
            }
        } else if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer())) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                if (invalidBlocks.contains(block.getType()))
                    return;
                if (block.getLocation().getBlockY() >= Gamemode.getCurrentMap().getLavaY()) {
                    event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "You are building to high!");
                    return;
                }
                if (survival) {
                    Inventory inventory = event.getPlayer().getInventory();
                    int index = inventory.first(block.getType());
                    if (index == -1) {
                        index = inventory.firstEmpty();
                    }
                    if (index != -1) {
                        ItemStack stack = inventory.getItem(index);
                        if (stack == null) {
                            stack = new ItemStack(block.getType(), 1);
                            inventory.setItem(index, stack);
                            block.setType(Material.AIR);
                            return;
                        }
                        stack.setType(block.getType());
                        stack.setAmount(stack.getAmount() + 1);
                    }
                }
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlace(final BlockPlaceEvent event) {
        event.setCancelled(true);
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer())) {
            if (event.getPlayer().getInventory().contains(event.getBlockPlaced().getType())) {

                if (event.getBlockPlaced().getLocation().getBlockY() >= Gamemode.getCurrentMap().getLavaY()) {
                    event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "You are building to high!");
                    return;
                }

                event.setCancelled(false);
                if (!survival) {
                    final int index = event.getPlayer().getInventory().first(event.getBlockPlaced().getType());
                    final ItemStack itm = event.getPlayer().getInventory().getItem(index);
                    final int amount = itm.getAmount();
                    final Material material = itm.getType();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                        @Override
                        public void run() {
                            ItemStack itm = event.getPlayer().getInventory().getItem(index);
                            if (itm == null) {
                                itm = new ItemStack(material, amount);
                                event.getPlayer().getInventory().setItem(index, itm);
                                return;
                            }
                            itm.setType(material);
                            itm.setAmount(amount);
                        }
                    }, 2);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent event) {
        um.getUser(event.getPlayer().getUniqueId()).logOut();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(PlayerJoinEvent event) {
        um.addUser(event.getPlayer());
        um.parseUser(event.getPlayer());
        if(!get.hasJoined(event.getPlayer().getUniqueId()))
            get.addUUID(event.getPlayer().getUniqueId());

        if(Gamemode.getCurrentGame() != null)
            event.getPlayer().teleport(Gamemode.getCurrentWorld().getSpawnLocation());

        Inventory inv = event.getPlayer().getInventory();
        if (BukkitUtils.isInventoryEmpty(inv)) {
            ItemStack[] items = new ItemStack[Gamemode.DEFAULT_BLOCKS.length];
            for (int i = 0; i < Gamemode.DEFAULT_BLOCKS.length; i++) {
                items[i] = new ItemStack(Gamemode.DEFAULT_BLOCKS[i], 1);
            }
            inv.addItem(items);
        }

        ShopFactory.validateInventory(inv);

        if (Gamemode.getCurrentGame() != null && !Gamemode.getCurrentGame().isDead(event.getPlayer()))
            Gamemode.getCurrentGame().setSpectator(event.getPlayer());
        if(Gamemode.getCurrentGame() != null)
            event.getPlayer().setScoreboard(Gamemode.getScoreboard());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void playerDeath(PlayerDeathEvent event) {
        Gamemode.getCurrentGame().setDead(event.getEntity());
        event.getEntity().getInventory().clear();

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if(Gamemode.getCurrentGame() != null)
            event.setRespawnLocation(Gamemode.getCurrentWorld().getSpawnLocation());
    }

    public void cleanup() {
        HandlerList.unregisterAll(this);
    }
}