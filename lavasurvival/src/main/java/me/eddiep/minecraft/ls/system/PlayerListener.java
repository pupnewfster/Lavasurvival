package me.eddiep.minecraft.ls.system;

import com.crossge.necessities.Necessities;
import me.eddiep.ChunkEdit;
import me.eddiep.ClassicPhysics;
import me.eddiep.handles.ClassicBlockPlaceEvent;
import me.eddiep.handles.ClassicPhysicsHandler;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.LavaMap;
import me.eddiep.minecraft.ls.game.items.LavaItem;
import me.eddiep.minecraft.ls.game.status.PlayerStatusManager;
import me.eddiep.minecraft.ls.ranks.UserInfo;
import me.eddiep.minecraft.ls.ranks.UserManager;
import me.eddiep.minecraft.ls.system.bank.BankInventory;
import me.eddiep.minecraft.ls.system.specialblocks.SpecialInventory;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;

import java.util.*;

@SuppressWarnings("unused")
public class PlayerListener implements Listener {
    private final ArrayList<Material> invalidBlocks = new ArrayList<>(Arrays.asList(Material.OBSIDIAN,
            Material.STONE_PLATE,
            Material.GOLD_PLATE,
            Material.IRON_PLATE,
            Material.WOOD_PLATE,
            Material.BEDROCK,
            Material.BARRIER));
    private final ArrayList<Material> clickOnBlocks = new ArrayList<>(Arrays.asList(//TODO: add other things that torches cannot be placed on but should be able to
            Material.GLASS,
            Material.STAINED_GLASS,
            Material.TNT,
            Material.GLOWSTONE,
            Material.LEAVES,
            Material.LEAVES_2,
            Material.ICE));
    private final Random rand = new Random();
    public boolean survival;

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
            } else {
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
            }
            if (!player.hasPermission("lavasurvival.voteSpeak"))
                player.sendMessage(ChatColor.RED + "No talking during the vote!");
            else
                event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent event) {
        if ((event.getEntity() instanceof Player && PlayerStatusManager.isInvincible((Player) event.getEntity())) || (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().hasEnded())) {
            event.setCancelled(true);
            return;
        }
        if (!this.survival && event.getEntity() instanceof Player && Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive((Player) event.getEntity())) {
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                event.setCancelled(true);
                if (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA))
                    Lavasurvival.INSTANCE.getUserManager().getUser(event.getEntity().getUniqueId()).damagePlayer();
                else if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK))
                    event.getEntity().setFireTicks(0);
            }
        }
    }

    @EventHandler
    public void itemHeldSlot(PlayerItemHeldEvent event) {
        ItemStack is = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (is == null || is.getType().equals(Material.AIR))
            return;
        IChatBaseComponent infoJSON;
        if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
            List<String> loreList = is.getItemMeta().getLore();
            int start = 0;
            String firstLore = loreList.get(start);
            if (firstLore.equals("Special")) {
                if (loreList.size() == 1)
                    return;//error
                start = 1;
                firstLore = loreList.get(start);
            }
            if (!firstLore.contains("MeltTime")) {
                StringBuilder lore = new StringBuilder();
                for (int i = start; i < loreList.size(); i++)
                    lore.append(loreList.get(i)).append(" ");
                infoJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + lore.toString().trim() + "\"}");
            } else {
                String lavaTime = ChatColor.GOLD + "Lava MeltTime" + ChatColor.RESET + ": " + PhysicsListener.getLavaMeltRangeTimeAsString(is.getData()),
                        waterTime = ChatColor.BLUE + "Water MeltTime" + ChatColor.RESET + ": " + PhysicsListener.getWaterMeltRangeTimeAsString(is.getData());
                infoJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + lavaTime + "    " + waterTime + "\"}");
            }
        } else {
            String lavaTime = ChatColor.GOLD + "Lava MeltTime" + ChatColor.RESET + ": " + PhysicsListener.getLavaMeltRangeTimeAsString(is.getData()),
                    waterTime = ChatColor.BLUE + "Water MeltTime" + ChatColor.RESET + ": " + PhysicsListener.getWaterMeltRangeTimeAsString(is.getData());
            infoJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + lavaTime + "    " + waterTime + "\"}");
        }
        PacketPlayOutTitle meltPacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, infoJSON, 0, 60, 0);
        ((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(meltPacket);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (Lavasurvival.INSTANCE.getSetups().containsKey(p.getUniqueId()))
            return;
        if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) && event.getItem() != null && event.getItem().getType().equals(Material.WRITTEN_BOOK))
            return;//Allow players to read the rule book
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isDead(p))
            event.setCancelled(true);
        else if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(p)) {
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                Block block = event.getClickedBlock();
                if (invalidBlocks.contains(block.getType()))
                    return;
                if (block.getLocation().getBlockY() >= Gamemode.getCurrentMap().getLavaY()) {
                    p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are building too high!");
                    return;
                }
                if (Gamemode.getCurrentMap().isInSafeZone(block.getLocation())) {
                    p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are not allowed to build in spawn!");
                    return;
                }
                UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(p.getUniqueId());
                if (System.currentTimeMillis() - u.getLastBreak() <= 100)//So that two blocks don't break instantly, may need to be adjusted
                    return;
                u.setLastBreak(System.currentTimeMillis());
                u.incrementBlockCount();
                if (this.survival) {
                    Inventory inventory = p.getInventory();
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
                if (block.getType().equals(Material.SPONGE))
                    Gamemode.getPhysicsListener().addBrokeSponge(block.getLocation());
                block.setType(Material.AIR);
                ClassicPhysics.INSTANCE.getPhysicsHandler().removePlayerPlaced(block.getLocation().toVector());
                PhysicsListener.cancelLocation(block.getLocation());
                Bukkit.getPluginManager().callEvent(new BlockBreakEvent(block, p));
            } else if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
                List<Entity> entities = p.getNearbyEntities(5, 5, 5);
                ArrayList<Location> visible = new ArrayList<>();
                Iterator<Block> itr = new BlockIterator(p, 5);
                while (itr.hasNext())
                    visible.add(itr.next().getLocation());
                for (Entity e : entities) {
                    if (e.getType().equals(EntityType.FALLING_BLOCK)) {
                        int x = e.getLocation().getBlockX(), y = e.getLocation().getBlockY(), z = e.getLocation().getBlockZ();//should it have a slightly larger check for y if it is not at .0
                        int upY = e.getLocation().getY() >= y + 0.5 && e.getLocation().getY() < y + 0.6 ? y + 1 : y;
                        if (visible.stream().anyMatch(cur -> x == cur.getBlockX() && (y == cur.getBlockY() || upY == cur.getBlockY()) && z == cur.getBlockZ())) {
                            Gamemode.getCurrentGame().interactSpecial(event.getPlayer(), (FallingBlock) e);
                            break;
                        }
                    }
                }
            }
        }
        if (event.getClickedBlock() == null)
            return;
        Material type = event.getClickedBlock().getType();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!p.isSneaking() && (event.getClickedBlock() instanceof InventoryHolder || type.equals(Material.WORKBENCH) ||
                    type.equals(Material.ANVIL) || type.equals(Material.ENCHANTMENT_TABLE) || type.equals(Material.ENDER_CHEST) || type.equals(Material.BEACON)) || type.equals(Material.BED_BLOCK) ||
                    type.equals(Material.CHEST) || type.equals(Material.TRAPPED_CHEST) || type.equals(Material.FURNACE) || type.equals(Material.BEACON) || type.equals(Material.BREWING_STAND) ||
                    type.equals(Material.DISPENSER) || type.equals(Material.DROPPER) || type.equals(Material.HOPPER))
                event.setCancelled(true);//Disable opening block's with inventories
            else if (!event.getBlockFace().equals(BlockFace.DOWN) && clickOnBlocks.contains(type)) { //Do not place blocks below because they may not have something to hang onto
                ItemStack item;
                if (event.getItem() != null)
                    item = event.getItem();
                else {
                    item = event.getHand().equals(EquipmentSlot.OFF_HAND) ? event.getPlayer().getInventory().getItemInOffHand() : event.getPlayer().getInventory().getItemInMainHand();
                    if (item != null && item.getType().equals(Material.AIR))//It is something that cannot normally be placed on it so the other hand got registered
                        item = event.getHand().equals(EquipmentSlot.OFF_HAND) ? event.getPlayer().getInventory().getItemInMainHand() : event.getPlayer().getInventory().getItemInOffHand();
                    else
                        return;
                }
                if (item == null || item.getType().equals(Material.AIR))
                    return;
                BlockFace face = event.getBlockFace();
                Block relative = event.getClickedBlock().getRelative(face);
                if (relative.getLocation().getBlockY() >= Gamemode.getCurrentMap().getLavaY()) {
                    p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are building too high!");
                    return;
                }
                if (Gamemode.getCurrentMap().isInSafeZone(relative.getLocation())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are not allowed to build in spawn!");
                    return;
                }
                boolean sendUpdate = false;
                byte data = 0;
                Material iType = item.getType();
                //TODO: If we add new blocks that can be placed like redstone torches add them here
                if (iType.equals(Material.TORCH)) {
                    if (face.equals(BlockFace.NORTH))
                        data = 4;
                    else if (face.equals(BlockFace.EAST))
                        data = 1;
                    else if (face.equals(BlockFace.SOUTH))
                        data = 3;
                    else if (face.equals(BlockFace.WEST))
                        data = 2;
                    else if (face.equals(BlockFace.UP))
                        data = 5;
                    sendUpdate = true;
                } else if (iType.equals(Material.LADDER)) {
                    if (face.equals(BlockFace.UP)) //Do not place it because it may not have something to place against on its side
                        return;
                    else if (face.equals(BlockFace.NORTH))
                        data = 2;
                    else if (face.equals(BlockFace.EAST))
                        data = 5;
                    else if (face.equals(BlockFace.SOUTH))
                        data = 3;
                    else if (face.equals(BlockFace.WEST))
                        data = 4;
                    sendUpdate = true;
                }
                if (sendUpdate) {
                    event.setCancelled(true);
                    new ChunkEdit(((CraftWorld) p.getWorld()).getHandle()).setBlock(relative.getX(), relative.getY(), relative.getZ(), item.getType(), data);
                    //Send the update packet to the people
                    Packet pack = new PacketPlayOutBlockChange(((CraftWorld) p.getWorld()).getHandle(), new BlockPosition(relative.getX(), relative.getY(), relative.getZ()));
                    Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(pack));
                    //TODO: if we add a survival mode make it remove the item from them
                }
            }
        }
    }

    @EventHandler
    public void onSpecialClick(PlayerInteractEntityEvent event) {
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            event.setCancelled(true);
            if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer()) && event.getRightClicked().getType().equals(EntityType.FALLING_BLOCK))
                Gamemode.getCurrentGame().interactSpecial(event.getPlayer(), (FallingBlock) event.getRightClicked());
        }
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
        if (!this.survival)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pickupItem(PlayerPickupItemEvent event) {//Stops items from being picked up if they somehow drop
        if (!this.survival) {
            event.getItem().remove();//Remove the dropped item
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void itemConsumed(PlayerItemConsumeEvent event) {
        ItemStack itemStack = event.getItem();
        for (LavaItem item : LavaItem.ITEMS)
            if (item.isItem(itemStack)) {
                event.setCancelled(true);
                if (item.consume(event.getPlayer()))
                    event.getPlayer().getInventory().clear(event.getPlayer().getInventory().first(itemStack));
                break;
            }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void inventoryClosed(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        BankInventory view = BankInventory.from(p);
        if (view != null)
            view.end(p);
        else
            SpecialInventory.tryClose(p);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOW)
    public void inventoryClicked(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int clickedSlot = e.getView().convertSlot(e.getRawSlot());
        BankInventory view = BankInventory.from(p);
        ItemStack currentItem = e.getCurrentItem();
        if (currentItem == null)
            return;
        if (view != null) {
            if (view.isNextPageButton(currentItem)) {
                view.nextPage();
                e.setCancelled(true);
            } else if (view.isPreviousPageButton(currentItem)) {
                view.previousPage();
                e.setCancelled(true);
            } else { //Only let blocks that can be placed be stored in the bank
                Material type = currentItem.getType();
                if (type != null && !type.equals(Material.AIR) && (!currentItem.hasItemMeta() || !currentItem.getItemMeta().hasLore() || !currentItem.getItemMeta().getLore().get(0).contains("MeltTime")))
                    e.setCancelled(true);
            }
        } else {
            FallingBlock b = SpecialInventory.from(p);
            if (b != null) {
                Material type = currentItem.getType();
                if (type != null && !type.equals(Material.AIR) && (!currentItem.hasItemMeta() || !currentItem.getItemMeta().hasLore() || !currentItem.getItemMeta().getLore().get(0).equals("Special")))
                    e.setCancelled(true);
                else if (b.getMaterial().equals(Gamemode.getCurrentGame().epic.getItemType()) && b.getBlockData() == Gamemode.getCurrentGame().epic.getData()) {
                    if (type != null && currentItem.hasItemMeta() && currentItem.getItemMeta().hasLore() && currentItem.getItemMeta().getLore().get(0).equals("Special")) {
                        if (!LavaItem.isLavaItem(currentItem)) {
                            UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(p.getUniqueId());
                            if (u.ownsBlock(currentItem.getData())) {
                                e.setCancelled(true);
                                return;
                            }
                            u.addBlock(currentItem.getData(), false);
                        }
                        ItemMeta meta = currentItem.getItemMeta();
                        List<String> lore = meta.getLore();
                        lore.remove(0);
                        meta.setLore(lore);
                        currentItem.setItemMeta(meta);
                    }
                } else if (!LavaItem.isLavaItem(currentItem)) {
                    UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(p.getUniqueId());
                    if (BukkitUtils.hasItem(p.getInventory(), currentItem.getData()) || u.isInBank(currentItem.getData()))
                        e.setCancelled(true);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlace(BlockPlaceEvent event) {
        if (event.getPlayer() == null || Lavasurvival.INSTANCE.getSetups().containsKey(event.getPlayer().getUniqueId()))
            return;
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getPlayer()) && ((event.getBlock().getType().equals(Material.WOODEN_DOOR) &&
                event.getPlayer().getInventory().contains(Material.WOOD_DOOR)) || event.getBlock().getType() != null || event.getPlayer().getInventory().contains(event.getBlock().getType()) ||
                event.getPlayer().getInventory().contains(Material.getMaterial(event.getBlock().getType().toString() + "_ITEM")) ||
                event.getPlayer().getInventory().contains(Material.getMaterial(event.getBlock().getType().toString().replaceAll("DOOR_BLOCK", "DOOR"))))) {
            if (event.getBlock().getLocation().getBlockY() >= Gamemode.getCurrentMap().getLavaY()) {
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are building too high!");
                event.setBuild(false);
                return;
            }
            if (Gamemode.getCurrentMap().isInSafeZone(event.getBlock().getLocation())) {
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are not allowed to build in spawn!");
                event.setBuild(false);
                return;
            }
            event.setCancelled(false);
            ItemStack itemPlacing = event.getPlayer().getInventory().getItemInMainHand();

            if (itemPlacing.getType() == Material.SPONGE) {
                BlockingType blockingType = itemPlacing.getDurability() == 1 ? BlockingType.LAVA : BlockingType.WATER;
                if (itemPlacing.getItemMeta().getLore().contains(LavaItem.EPIC_TEXT))
                    blockingType = BlockingType.BOTH;
                boolean success = Gamemode.getPhysicsListener().placeSponge(event.getBlock().getLocation(), blockingType);

                if (!success) {
                    event.setCancelled(true);
                    event.setBuild(false);
                    event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You can't place a sponge so close to a lava/water spawn!");
                    return;
                }

                ClassicPhysics.INSTANCE.getPhysicsHandler().addPlayerPlaced(event.getBlock().getLocation().toVector());
                Lavasurvival.INSTANCE.getUserManager().getUser(event.getPlayer().getUniqueId()).incrementBlockCount();
                return;
            }

            Block b = event.getBlock();
            ClassicPhysics.INSTANCE.getPhysicsHandler().addPlayerPlaced(b.getLocation().toVector()); //UUID would go here if was hash map
            if (b.getType().toString().contains("DOOR") && b.getRelative(BlockFace.UP).getType().equals(b.getType()))
                ClassicPhysics.INSTANCE.getPhysicsHandler().addPlayerPlaced(b.getRelative(BlockFace.UP).getLocation().toVector());
            if (!this.survival) {
                if (event.getHand().equals(EquipmentSlot.OFF_HAND))
                    event.getPlayer().getInventory().setItemInOffHand(event.getPlayer().getInventory().getItemInOffHand().clone());
                else
                    event.getPlayer().getInventory().setItemInMainHand(event.getPlayer().getInventory().getItemInMainHand().clone());
            }
        }
        Lavasurvival.INSTANCE.getUserManager().getUser(event.getPlayer().getUniqueId()).incrementBlockCount();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent event) {
        Lavasurvival.INSTANCE.getUserManager().getUser(event.getPlayer().getUniqueId()).logOut();
        Lavasurvival.GGBAR.removePlayer(event.getPlayer());
        if (Gamemode.getCurrentGame().allDead() && !Gamemode.getCurrentGame().hasEnded())
            Gamemode.getCurrentGame().endRound();
        Gamemode.getCurrentGame().removeBars(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (Lavasurvival.INSTANCE.updating) {
            player.kickPlayer("This server is updating!");
            return;
        }

        if (Gamemode.getCurrentGame().isSuspended()) {
            Gamemode.getCurrentGame().start(true); //Force start the next game
        }

        UserManager um = Lavasurvival.INSTANCE.getUserManager();
        um.addUser(player);
        um.forceParseUser(player);
        player.setLevel(0);
        player.setExp(0);

        Lavasurvival.GGBAR.addPlayer(player);

        if (Gamemode.getCurrentGame() != null) {
            if (!Gamemode.getCurrentGame().isInGame(player)) {
                player.teleport(Gamemode.getCurrentWorld().getSpawnLocation().clone());
                Gamemode.getCurrentGame().playerJoin(player);
            }
            if (Gamemode.getCurrentGame().isAlive(player)) {
                Necessities.getUM().getUser(player.getUniqueId()).setStatus("alive");
                ClassicPhysicsHandler handler = ClassicPhysics.INSTANCE.getPhysicsHandler();
                if (!handler.isClassicBlock(player.getLocation().toVector()) && !handler.isClassicBlock(player.getEyeLocation().toVector()))
                    player.teleport(Gamemode.getCurrentWorld().getSpawnLocation().clone());
            }
            event.getPlayer().setScoreboard(Gamemode.getScoreboard());
            Gamemode.getCurrentGame().addBars(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void foodLevelChange(FoodLevelChangeEvent event) {
        if (!survival)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void healthRegen(EntityRegainHealthEvent event) {
        if (!survival && event.getEntity() instanceof Player)
            event.setCancelled(true);
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerMove(PlayerMoveEvent event) {
        if (Gamemode.getCurrentGame() != null && Gamemode.DAMAGE != 0 && Gamemode.getCurrentGame().isAlive(event.getPlayer())) {
            Location from = event.getFrom(), to = event.getTo();
            boolean locationChanged = Math.abs(from.getX() - to.getX()) > 0.1 || Math.abs(from.getY() - to.getY()) > 0.1 || Math.abs(from.getZ() - to.getZ()) > 0.1;
            if (!locationChanged)
                return;
            UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(event.getPlayer().getUniqueId());
            Block b = to.getBlock(), above = b.getRelative(BlockFace.UP);
            if (((b.getType().equals(Material.WATER) || b.getType().equals(Material.STATIONARY_WATER)) && ClassicPhysics.INSTANCE.getPhysicsHandler().isClassicBlock(b.getLocation().toVector())) ||
                    ((above.getType().equals(Material.WATER) || above.getType().equals(Material.STATIONARY_WATER)) && ClassicPhysics.INSTANCE.getPhysicsHandler().isClassicBlock(above.getLocation().toVector()))) {
                if (!u.isInWater()) {
                    if (!PlayerStatusManager.isInvincible(event.getPlayer()) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && !event.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
                        u.damagePlayer();
                    u.setInWater(true);
                }
            } else if (u.isInWater())
                u.setInWater(false);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void classicBlockPlace(ClassicBlockPlaceEvent event) {
        if (Gamemode.getCurrentGame() == null || Gamemode.DAMAGE == 0)
            return;
        Material type = event.getLocation().getBlock().getType();
        if (!type.equals(Material.WATER) && !type.equals(Material.STATIONARY_WATER))
            return;
        Location loc = event.getLocation();
        Bukkit.getOnlinePlayers().stream().filter(p -> Gamemode.getCurrentGame().isAlive(p) && (p.getLocation().getBlock().getLocation().equals(loc) || p.getLocation().getBlock().getRelative(BlockFace.UP).getLocation().equals(loc))).forEach(p -> {
            UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(p.getUniqueId());
            if (!u.isInWater()) {
                if (!PlayerStatusManager.isInvincible(p) && !p.getGameMode().equals(GameMode.CREATIVE) && !p.getGameMode().equals(GameMode.SPECTATOR))
                    u.damagePlayer();
                u.setInWater(true);
            }
        });
    }

    private static final String[] deathMessages = {ChatColor.RED + "" + ChatColor.BOLD + "Wasted!", ChatColor.GREEN + "" + ChatColor.BOLD + "Better luck next time!",
            ChatColor.RED + "" + ChatColor.BOLD + "You died!", ChatColor.RED + "" + ChatColor.BOLD + "rip."};

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDeath(PlayerDeathEvent event) {
        if (Gamemode.getCurrentGame() != null && Gamemode.getCurrentGame().isAlive(event.getEntity())) {
            Gamemode.getCurrentGame().setDead(event.getEntity());
            if (event.getDeathMessage().contains("fell out of the world"))
                event.setDeathMessage(event.getDeathMessage().replace("fell out of the world", ChatColor.YELLOW + "died to the elements."));
            UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(event.getEntity().getUniqueId());
            u.setInWater(false);
            event.setKeepInventory(true);
            event.setDroppedExp(0);
            final Player p = event.getEntity();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, () -> {
                ((CraftPlayer) p).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
                p.sendTitle(deathMessages[rand.nextInt(deathMessages.length)], ChatColor.GOLD + "Please wait for the next round to start!", 0, 60, 0);
                p.teleport(Gamemode.getCurrentWorld().getSpawnLocation());
            }, 1);
        }
    }

    public void cleanup() {
        HandlerList.unregisterAll(this);
    }
}