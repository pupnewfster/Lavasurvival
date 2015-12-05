package me.eddiep.minecraft.ls.system;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.commands.CmdHide;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.LavaMap;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import me.eddiep.minecraft.ls.ggbot.GGBotModeration;
import me.eddiep.minecraft.ls.ranks.UUIDs;
import me.eddiep.minecraft.ls.ranks.UserInfo;
import me.eddiep.minecraft.ls.ranks.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
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
import java.util.List;

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

    private final UserManager um = Lavasurvival.INSTANCE.getUserManager();
    private final UUIDs get = Lavasurvival.INSTANCE.getUUIDs();
    private final GGBotModeration bot = Lavasurvival.INSTANCE.getGGBotModeration();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled())
            return;
        Player player = event.getPlayer();
        UserInfo u = um.getUser(player.getUniqueId());
        String message = event.getMessage();
        Gamemode game = Gamemode.getCurrentGame();


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


        if (game != null && game.isVoting()) {
            event.setCancelled(true);
            if (game.hasVoted(player)) {
                if (player.hasPermission("ls.voteSpeak"))
                    event.setCancelled(false);
                else
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You already voted!");
                return;
            }
            try {
                int number = Integer.parseInt(event.getMessage());
                game.voteFor(number - 1, player);
                return;
            } catch (Throwable t) {
                String map = event.getMessage();
                List<LavaMap> maps = game.getMapsInVote();
                for (int i = 0; i < maps.size(); i++)
                    if (map.equalsIgnoreCase(maps.get(i).getName())) {
                        game.voteFor(i, player);
                        return;
                    }
            }

            if (!player.hasPermission("ls.voteSpeak"))
                player.sendMessage(ChatColor.RED + "No talking during the vote!");
            else
                event.setCancelled(false);
        }


        if (!event.isCancelled() && (u.isInOpChat() || (event.getMessage().startsWith("#") && player.hasPermission("ls.opchat")))) {
            if (event.getMessage().startsWith("#"))
                event.setMessage(event.getMessage().substring(1)); //Remove #

            event.setFormat(ChatColor.GOLD + "To Ops - " + ChatColor.WHITE + event.getFormat());
            ArrayList<Player> toRem = new ArrayList<Player>();
            for (Player recip : event.getRecipients())
                if (u.isInOpChat() && !recip.hasPermission("ls.opchat"))
                    toRem.add(recip);
            for (Player recip : toRem)
                event.getRecipients().remove(recip);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) {
        if (Lavasurvival.INSTANCE.getSetups().containsKey(event.getPlayer().getUniqueId()))
            return;
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
                event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.FALL) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)))
            event.setCancelled(true);
        else if (event.getEntity() instanceof Player && !survival && Gamemode.getCurrentGame() != null &&
                Gamemode.getCurrentGame().isAlive((Player) event.getEntity()) && event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, 1.5);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract(PlayerInteractEvent event) {
        if (Lavasurvival.INSTANCE.getSetups().containsKey(event.getPlayer().getUniqueId()))
            return;
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getItem() != null && event.getItem().getType().equals(Material.WRITTEN_BOOK))
            return;//Allow players to read the rule book
        if (event.getAction() == Action.PHYSICAL)
            return;//Allow pressure plates to work
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
                UserInfo u = um.getUser(event.getPlayer().getUniqueId());
                if (System.currentTimeMillis() - u.getLastBreak() <= 200)//So that two blocks don't break instantly, may need to be adjusted
                    return;
                u.setLastBreak(System.currentTimeMillis());
                u.incrimentBlockCount();
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
                Bukkit.getPluginManager().callEvent(new BlockBreakEvent(block, event.getPlayer()));
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking() &&
                (event.getClickedBlock() instanceof InventoryHolder || event.getClickedBlock().getType().equals(Material.WORKBENCH) ||
                event.getClickedBlock().getType().equals(Material.ANVIL) || event.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE) ||
                event.getClickedBlock().getType().equals(Material.ENDER_CHEST) || event.getClickedBlock().getType().equals(Material.BEACON) ||
                event.getClickedBlock().getType().equals(Material.ITEM_FRAME)))
            event.setCancelled(true);//Disable opening block's with inventories
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent event) {
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer())) {
            Block block = event.getBlock();
            if (invalidBlocks.contains(block.getType()))
                return;
            if (block.getLocation().getBlockY() >= Gamemode.getCurrentMap().getLavaY()) {
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are building to high!");
                return;
            }
            UserInfo u = um.getUser(event.getPlayer().getUniqueId());
            if (System.currentTimeMillis() - u.getLastBreak() <= 200)//So that two blocks don't break instantly, may need to be adjusted
                return;
            u.setLastBreak(System.currentTimeMillis());
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
            Bukkit.getPluginManager().callEvent(new BlockBreakEvent(block, event.getPlayer()));
        }
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
        if (Lavasurvival.INSTANCE.getSetups().containsKey(event.getPlayer().getUniqueId()))
            return;
        if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))//Allows players in creative to edit maps
            event.setCancelled(true);
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer()) &&
                (event.getPlayer().getInventory().contains(event.getBlock().getType()) ||
                        event.getPlayer().getInventory().contains(Material.getMaterial(event.getBlock().getType().toString() + "_ITEM")) ||
                        event.getPlayer().getInventory().contains(Material.getMaterial(event.getBlock().getType().toString().replaceAll("DOOR_BLOCK", "DOOR"))))) {
            if (event.getBlock().getLocation().getBlockY() >= Gamemode.getCurrentMap().getLavaY()) {
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are building to high!");
                return;
            }
            if (Gamemode.getCurrentMap().isInSafeZone(event.getBlock().getLocation())) {
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are not allowed to build in spawn!");
                return;
            }
            event.setCancelled(false);
            if (!survival) {
                final int index = event.getPlayer().getInventory().first(event.getBlock().getType());
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
        UserInfo u = um.getUser(event.getPlayer().getUniqueId());
        u.incrimentBlockCount();

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent event) {
        CmdHide hide = Lavasurvival.INSTANCE.getHide();
        if (hide.isHidden(event.getPlayer())) {
            Bukkit.broadcast(ChatColor.GOLD + "To Ops - " + event.getQuitMessage(), "ls.opchat");
            event.setQuitMessage(null);
        }
        hide.removeP(event.getPlayer().getUniqueId());
        hide.playerLeft(event.getPlayer());
        um.getUser(event.getPlayer().getUniqueId()).logOut();
        Lavasurvival.INSTANCE.getGGBotModeration().removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) //Ignore if event has already been canceled
    public void fireSpread(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(final PlayerJoinEvent event) {
        um.addUser(event.getPlayer());
        um.parseUser(event.getPlayer());

        Lavasurvival.INSTANCE.getHide().playerJoined(event.getPlayer());

        if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(event.getPlayer()))
            Lavasurvival.INSTANCE.getEconomy().createPlayerAccount(event.getPlayer());


        if (Gamemode.getCurrentGame() != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                @Override
                public void run() {
                    event.getPlayer().teleport(Gamemode.getCurrentWorld().getSpawnLocation());

                    Inventory inv = event.getPlayer().getInventory();
                    UserInfo u = um.getUser(event.getPlayer().getUniqueId());
                    u.giveBoughtBlocks(); //Always do this
                    Player p = event.getPlayer();

                    for (int i = 0; i < Gamemode.DEFAULT_BLOCKS.length; i++) {
                        ItemStack toGive = new ItemStack(Gamemode.DEFAULT_BLOCKS[i], 1);
                        if (BukkitUtils.hasItem(p.getInventory(), toGive))
                            continue;
                        inv.addItem(toGive);
                    }

                    if (!get.hasJoined(event.getPlayer().getUniqueId())) {
                        get.addUUID(event.getPlayer().getUniqueId());
                        event.getPlayer().getInventory().addItem(Lavasurvival.INSTANCE.getRules());
                    }

                    ShopFactory.validateInventory(inv);
                }
            }, 7);
        }

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
/*        if (!ClassicPhysics.INSTANCE.getPhysicsHandler().isClassicBlock(event.getTo().getBlock()))
            return;
            This check is no longer possible, as blocks are simply added to a queue
            */

        boolean locationChanged = event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ();
        if (locationChanged && Gamemode.getCurrentGame() != null && Gamemode.WATER_DAMAGE != 0 && Gamemode.getCurrentGame().isAlive(event.getPlayer())) {
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
