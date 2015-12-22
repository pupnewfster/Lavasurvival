package me.eddiep.minecraft.ls.system;

import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import me.eddiep.handles.ClassicBlockPlaceEvent;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.LavaMap;
import me.eddiep.minecraft.ls.ranks.UserInfo;
import me.eddiep.minecraft.ls.ranks.UserManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {
    public final ArrayList<Material> invalidBlocks = new ArrayList<>(Arrays.asList(new Material[]{
            Material.OBSIDIAN,
            Material.IRON_DOOR,
            Material.IRON_DOOR_BLOCK,
            Material.STONE_PLATE,
            Material.GOLD_PLATE,
            Material.IRON_PLATE,
            Material.WOOD_PLATE,
            Material.BEDROCK,
            Material.BARRIER
    }));
    private com.crossge.necessities.RankManager.UserManager userManager = Lavasurvival.INSTANCE.getNecessitiesUserManager();
    private RankManager rm = Lavasurvival.INSTANCE.getRankManager();
    private final UserManager um = Lavasurvival.INSTANCE.getUserManager();
    public boolean survival = false;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        Gamemode game = Gamemode.getCurrentGame();

        if (game != null && game.isVoting()) {
            event.setCancelled(true);
            if (game.hasVoted(player)) {
                if (player.hasPermission("lavasurvival.voteSpeak"))
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

            if (!player.hasPermission("lavasurvival.voteSpeak"))
                player.sendMessage(ChatColor.RED + "No talking during the vote!");
            else
                event.setCancelled(false);
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
        if (Gamemode.getCurrentGame() != null)
            event.getPlayer().sendMessage("You are " + ChatColor.RED + "DEAD" + ChatColor.RESET + ". You cannot delete or place blocks!");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player && !survival && Gamemode.getCurrentGame() != null &&
                Gamemode.getCurrentGame().isAlive((Player) event.getEntity()) && (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.FALL) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || event.getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK) ||
                event.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)))
            event.setCancelled(true);
        else if (event.getEntity() instanceof Player && !survival && Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive((Player) event.getEntity()) &&
                event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
            ((Player) event.getEntity()).damage(Gamemode.DAMAGE);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract(PlayerInteractEvent event) {
        if (Lavasurvival.INSTANCE.getSetups().containsKey(event.getPlayer().getUniqueId()))
            return;
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getItem() != null && event.getItem().getType().equals(Material.WRITTEN_BOOK))
            return;//Allow players to read the rule book
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isDead(event.getPlayer()))
            event.setCancelled(true);
        else if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer())) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                if (invalidBlocks.contains(block.getType()))
                    return;
                if (block.getLocation().getBlockY() >= Gamemode.getCurrentMap().getLavaY()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are building to high!");
                    return;
                }
                if (Gamemode.getCurrentMap().isInSafeZone(block.getLocation())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are not allowed to build in spawn!");
                    return;
                }
                UserInfo u = um.getUser(event.getPlayer().getUniqueId());
                if (System.currentTimeMillis() - u.getLastBreak() <= 100)//So that two blocks don't break instantly, may need to be adjusted
                    return;
                if (block.hasMetadata("player_placed")) {
                    FixedMetadataValue meta = (FixedMetadataValue) block.getMetadata("player_placed");
                    UUID uuid = (UUID) meta.value();
                    if (!event.getPlayer().getUniqueId().equals(uuid)) {
                        userManager.getUser(uuid);
                        User user = userManager.getUser(event.getPlayer().getUniqueId());
                        if (rm.getOrder().indexOf(user.getRank()) - rm.getOrder().indexOf(userManager.getUser(uuid).getRank()) < 0) {
                            event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are not allowed to break a higher ranks blocks!");
                            return;
                        }
                    }
                }
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
                if (block.hasMetadata("player_placed"))
                    block.removeMetadata("player_placed", Lavasurvival.INSTANCE);
                PhysicsListener.cancelLocation(block.getLocation());
                Bukkit.getPluginManager().callEvent(new BlockBreakEvent(block, event.getPlayer()));
            }
        }
        if (event.getClickedBlock() == null)
            return;
        Material type = event.getClickedBlock().getType();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking() && (event.getClickedBlock() instanceof InventoryHolder || type.equals(Material.WORKBENCH) ||
                type.equals(Material.ANVIL) || type.equals(Material.ENCHANTMENT_TABLE) || type.equals(Material.ENDER_CHEST) || type.equals(Material.BEACON)) || type.equals(Material.BED_BLOCK))
            event.setCancelled(true);//Disable opening block's with inventories
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
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
    public void blockPlace(BlockPlaceEvent event) {
        if (Lavasurvival.INSTANCE.getSetups().containsKey(event.getPlayer().getUniqueId()))
            return;
        if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))//Allows players in creative to edit maps
            event.setCancelled(true);
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer()) && (event.getPlayer().getInventory().contains(event.getBlock().getType()) ||
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
            event.getBlock().setMetadata("player_placed", new FixedMetadataValue(Lavasurvival.INSTANCE, event.getPlayer().getUniqueId()));
            if (!survival) {
                int index = event.getPlayer().getInventory().first(event.getItemInHand());
                event.getPlayer().getInventory().setItem(index, event.getPlayer().getInventory().getItem(index).clone());
            }
        }
        UserInfo u = um.getUser(event.getPlayer().getUniqueId());
        u.incrimentBlockCount();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent event) {
        um.getUser(event.getPlayer().getUniqueId()).logOut();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(final PlayerJoinEvent event) {
        um.addUser(event.getPlayer());
        um.forceParseUser(event.getPlayer());

        if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(event.getPlayer()))
            Lavasurvival.INSTANCE.getEconomy().createPlayerAccount(event.getPlayer());

        if (Gamemode.getCurrentGame() != null) {
            event.getPlayer().teleport(Gamemode.getCurrentWorld().getSpawnLocation().clone());
            if (!Gamemode.getCurrentGame().isInGame(event.getPlayer()))
                Gamemode.getCurrentGame().playerJoin(event.getPlayer());
        }

        if (Gamemode.getCurrentGame() != null)
            event.getPlayer().setScoreboard(Gamemode.getScoreboard());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void foodLevelChange(FoodLevelChangeEvent event) {
        if (!survival)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void healthRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player && !survival)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerMove(PlayerMoveEvent event) {
        Location from = event.getFrom(), to = event.getTo();
        boolean locationChanged = Math.abs(from.getX() - to.getX()) > 0.1 || Math.abs(from.getY() - to.getY()) > 0.1 || Math.abs(from.getZ() - to.getZ()) > 0.1;
        if (locationChanged && Gamemode.getCurrentGame() != null && Gamemode.DAMAGE != 0 && Gamemode.getCurrentGame().isAlive(event.getPlayer())) {
            UserInfo u = um.getUser(event.getPlayer().getUniqueId());
            if(((to.getBlock().getType().equals(Material.WATER) || to.getBlock().getType().equals(Material.STATIONARY_WATER)) && to.getBlock().hasMetadata("classic_block")) ||
                ((to.getBlock().getRelative(BlockFace.UP).getType().equals(Material.WATER) || to.getBlock().getRelative(BlockFace.UP).getType().equals(Material.STATIONARY_WATER)) &&
                to.getBlock().getRelative(BlockFace.UP).hasMetadata("classic_block"))) {
                if (!u.isInWater()) {
                    event.getPlayer().damage(Gamemode.DAMAGE);
                    u.setInWater(true);
                }
            } else if(u.isInWater())
                u.setInWater(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void classicBlockPlace(ClassicBlockPlaceEvent event) {
        Material type = event.getLocation().getBlock().getType();
        if (Gamemode.getCurrentGame() != null && Gamemode.DAMAGE != 0 && !type.equals(Material.WATER) && !type.equals(Material.STATIONARY_WATER))
            return;
        Location loc = event.getLocation().getBlock().getLocation();
        for (Player p : Bukkit.getOnlinePlayers())
            if (Gamemode.getCurrentGame().isAlive(p) && (p.getLocation().getBlock().getLocation().equals(loc) || p.getLocation().getBlock().getRelative(BlockFace.UP).getLocation().equals(loc))) {
                UserInfo u = um.getUser(p.getUniqueId());
                if (!u.isInWater()) {
                    p.damage(Gamemode.DAMAGE);
                    u.setInWater(true);
                }
            }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDeath(PlayerDeathEvent event) {
        if (Gamemode.getCurrentGame() != null) {
            Gamemode.getCurrentGame().setDead(event.getEntity());
            UserInfo u = um.getUser(event.getEntity().getUniqueId());
            u.setInWater(false);
        }
        event.getEntity().getInventory().clear();
        event.getDrops().clear();
        event.setDroppedExp(0);
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