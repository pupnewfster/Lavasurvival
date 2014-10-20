package me.eddiep.system;

import me.eddiep.Lavasurvival;
import me.eddiep.commands.CmdHide;
import me.eddiep.game.Gamemode;
import me.eddiep.game.shop.ShopFactory;
import me.eddiep.ggbot.GGBotModeration;
import me.eddiep.ranks.UUIDs;
import me.eddiep.ranks.UserInfo;
import me.eddiep.ranks.UserManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerListener implements Listener {
    public final ArrayList<Material> invalidBlocks = new ArrayList<Material>(Arrays.asList(new Material[]{
            Material.OBSIDIAN,
            Material.IRON_DOOR,
            Material.IRON_DOOR_BLOCK,
            Material.STONE_PLATE,
            Material.GOLD_PLATE,
            Material.IRON_PLATE,
            Material.WOOD_PLATE,
            Material.BEDROCK
    }));
    public boolean survival = false;
    public ArrayList<OfflinePlayer> voted = new ArrayList<OfflinePlayer>();
    UserManager um = Lavasurvival.INSTANCE.getUserManager();
    UUIDs get = Lavasurvival.INSTANCE.getUUIDs();
    GGBotModeration bot = Lavasurvival.INSTANCE.getGGBotModeration();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled())
            return;
        Player player = event.getPlayer();
        UserInfo u = um.getUser(player.getUniqueId());
        String message = event.getMessage();
        if(message.endsWith(">") && ! message.equals(">")) {
            String appended = u.getAppended() + " " + message.substring(0, message.length() - 1);
            u.setAppended(appended.trim());
            player.sendMessage(ChatColor.GREEN + "Message appended.");
            event.setCancelled(true);
            return;
        } else if (!u.getAppended().equals("")) {
            event.setMessage(u.getAppended() + " " + message);
            u.setAppended("");
        }
        if (u.getRank() != null)
            event.setFormat(ChatColor.translateAlternateColorCodes('&', u.getRank().getTitle()) + " " + player.getName() + ": " +
                    bot.logChat(player.getUniqueId(), event.getMessage()));
        if (Gamemode.getCurrentGame() != null && Gamemode.voting) {
            event.setCancelled(true);
            if (voted.contains(player)) {
                if (player.hasPermission("lavasurvival.voteSpeak"))
                    event.setCancelled(false);
                else
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
                for (int i = 0; i < Gamemode.nextMaps.length; i++)
                    if (map.equalsIgnoreCase(Gamemode.nextMaps[i].getName())) {
                        voted.add(player);
                        Gamemode.votes[i]++;
                        player.sendMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + "" + ChatColor.BOLD + "You voted for " + Gamemode.nextMaps[i].getName() + "!");
                        return;
                    }
            }

            if (!player.hasPermission("lavasurvival.voteSpeak"))
                player.sendMessage(ChatColor.RED + "No talking during the vote!");
            else
                event.setCancelled(false);

            if (!event.isCancelled() && u.opChat()) {
                event.setFormat(ChatColor.GOLD + "To Ops - " + ChatColor.WHITE + event.getFormat());
                ArrayList<Player> toRem = new ArrayList<Player>();
                for (Player recip : event.getRecipients())
                    if (u.opChat() && !recip.hasPermission("lavasurvival.opchat"))
                        toRem.add(recip);
                for (Player recip : toRem)
                    event.getRecipients().remove(recip);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) {
        if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))//Allows players in creative to edit maps
            event.setCancelled(true);
        Material material = event.getBlock().getType();
        if (invalidBlocks.contains(material) || (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer())))
            return;
        if (Gamemode.getCurrentGame() != null) {
            String state = Gamemode.getCurrentGame().isDead(event.getPlayer()) ? ChatColor.RED + "DEAD" : ChatColor.GRAY + "SPECTATING";
            event.getPlayer().sendMessage("You are " + state + ChatColor.RESET + ". You cannot delete or place blocks!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player && !survival && Gamemode.getCurrentGame() != null &&
                Gamemode.getCurrentGame().isAlive((Player) event.getEntity()) && (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)))
            event.setCancelled(true);
        else if (event.getEntity() instanceof Player && !survival && Gamemode.getCurrentGame() != null &&
                Gamemode.getCurrentGame().isAlive((Player) event.getEntity()) && event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, 1.5);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract(PlayerInteractEvent event) {
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                    event.getClickedBlock().getState() instanceof Sign &&
                    ((Sign) event.getClickedBlock().getState()).getLine(0).contains("Right click")) {
                Gamemode.getCurrentGame().playerJoin(event.getPlayer());
            }
        } else if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isDead(event.getPlayer())) {
            event.setCancelled(true);
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                    event.getClickedBlock().getState() instanceof Sign &&
                    ((Sign) event.getClickedBlock().getState()).getLine(0).contains("Right click")) {
                event.getPlayer().sendMessage("Sorry you can't join, you're dead :/");
            }
        } else if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer())) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                if (invalidBlocks.contains(block.getType()))
                    return;
                if (block.getLocation().getBlockY() >= Gamemode.getCurrentMap().getLavaY()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are building to high!");
                    return;
                }
                if (survival) {
                    Inventory inventory = event.getPlayer().getInventory();
                    int index = inventory.first(block.getType());
                    if (index == -1)
                        index = inventory.firstEmpty();
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
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking() &&
                (event.getClickedBlock() instanceof InventoryHolder || event.getClickedBlock().getType().equals(Material.WORKBENCH)))
            event.setCancelled(true);//Disable opening block's with inventories
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemCraft(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void dropItem(PlayerDropItemEvent event) {
        if(!survival)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlace(final BlockPlaceEvent event) {
        if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))//Allows players in creative to edit maps
            event.setCancelled(true);
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer()) &&
                event.getPlayer().getInventory().contains(event.getBlockPlaced().getType())) {
            if (event.getBlockPlaced().getLocation().getBlockY() >= Gamemode.getCurrentMap().getLavaY()) {
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are building to high!");
                return;
            }
            event.setCancelled(false);
            if (!survival) {
                final int index = event.getPlayer().getInventory().first(event.getBlockPlaced().getType());
                final ItemStack itm = event.getPlayer().getInventory().getItem(index);
                final int amount = itm.getAmount();
                final Material material = itm.getType();
                final short data = itm.getDurability();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                    @Override
                    public void run() {
                        ItemStack itm = event.getPlayer().getInventory().getItem(index);
                        if (itm == null) {
                            itm = new ItemStack(material, amount, data);
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent event) {
        CmdHide hide = Lavasurvival.INSTANCE.getHide();
        if (hide.isHidden(event.getPlayer())) {
            Bukkit.broadcast(ChatColor.GOLD + "To Ops - " + event.getQuitMessage(), "lavasurvival.opchat");
            event.setQuitMessage(null);
        }
        hide.removeP(event.getPlayer().getUniqueId());
        hide.playerLeft(event.getPlayer());
        um.getUser(event.getPlayer().getUniqueId()).logOut();
        Lavasurvival.INSTANCE.getGGBotModeration().removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(PlayerJoinEvent event) {
        um.addUser(event.getPlayer());
        um.parseUser(event.getPlayer());
        if (!get.hasJoined(event.getPlayer().getUniqueId())) {
            get.addUUID(event.getPlayer().getUniqueId());
            event.getPlayer().getInventory().addItem(Lavasurvival.INSTANCE.getRules());
        }

        Lavasurvival.INSTANCE.getHide().playerJoined(event.getPlayer());

        if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(event.getPlayer()))
            Lavasurvival.INSTANCE.getEconomy().createPlayerAccount(event.getPlayer());

        if (Gamemode.getCurrentGame() != null)
            event.getPlayer().teleport(Gamemode.getCurrentWorld().getSpawnLocation());

        Inventory inv = event.getPlayer().getInventory();
        if (BukkitUtils.isInventoryEmpty(inv)) {
            ItemStack[] items = new ItemStack[Gamemode.DEFAULT_BLOCKS.length];
            for (int i = 0; i < Gamemode.DEFAULT_BLOCKS.length; i++)
                items[i] = new ItemStack(Gamemode.DEFAULT_BLOCKS[i], 1);
            inv.addItem(items);
            UserInfo u = um.getUser(event.getPlayer().getUniqueId());
            u.giveBoughtBlocks();
        }

        ShopFactory.validateInventory(inv);

        if (Gamemode.getCurrentGame() != null && !Gamemode.getCurrentGame().isDead(event.getPlayer()))
            Gamemode.getCurrentGame().setSpectator(event.getPlayer());
        if (Gamemode.getCurrentGame() != null)
            event.getPlayer().setScoreboard(Gamemode.getScoreboard());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void foodLevelChange(FoodLevelChangeEvent event) {
        if(!survival)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void healthRegen(EntityRegainHealthEvent event) {
        if(event.getEntity() instanceof Player && !survival)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerMove(PlayerMoveEvent event) {
        boolean locationChanged = event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ();
        if (Gamemode.getCurrentGame() != null && Gamemode.WATER_DAMAGE != 0 && Gamemode.getCurrentGame().isAlive(event.getPlayer()) && locationChanged) {
            UserInfo u = um.getUser(event.getPlayer().getUniqueId());
            if(event.getTo().getBlock().getType().equals(Material.WATER) || event.getTo().getBlock().getType().equals(Material.STATIONARY_WATER) ||
                    event.getTo().getBlock().getRelative(BlockFace.UP).getType().equals(Material.WATER) ||
                    event.getTo().getBlock().getRelative(BlockFace.UP).getType().equals(Material.STATIONARY_WATER)) {
                if(!u.isInWater()) {
                    event.getPlayer().damage(Gamemode.WATER_DAMAGE);
                    u.setInWater(true);
                }
            } else if(u.isInWater())
                u.setInWater(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDeath(PlayerDeathEvent event) {
        if (Gamemode.getCurrentGame() != null)
            Gamemode.getCurrentGame().setDead(event.getEntity());
        event.getEntity().getInventory().clear();

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if (Gamemode.getCurrentGame() != null)
            event.setRespawnLocation(Gamemode.getCurrentWorld().getSpawnLocation());
    }

    public void cleanup() {
        HandlerList.unregisterAll(this);
    }
}