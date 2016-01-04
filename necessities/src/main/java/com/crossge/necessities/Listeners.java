package com.crossge.necessities;

import com.crossge.necessities.Commands.CmdCommandSpy;
import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.Hats.Hat;
import com.crossge.necessities.Janet.Janet;
import com.crossge.necessities.Janet.JanetAI;
import com.crossge.necessities.Janet.JanetSlack;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.WorldManager.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.UUID;

public class Listeners implements Listener {
    private File configFileLogOut = new File("plugins/Necessities", "logoutmessages.yml"), configFileLogIn = new File("plugins/Necessities", "loginmessages.yml"),
            configFileTitles = new File("plugins/Necessities", "titles.yml"), configFile = new File("plugins/Necessities", "config.yml");
    CmdCommandSpy spy = new CmdCommandSpy();
    PortalManager pm = new PortalManager();
    JanetSlack slack = new JanetSlack();
    UserManager um = new UserManager();
    Console console = new Console();
    Variables var = new Variables();
    Teleports tps = new Teleports();
    CmdHide hide = new CmdHide();
    JanetAI ai = new JanetAI();
    Janet bot = new Janet();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        um.addUser(p);
        um.forceParseUser(p);
        final User u = um.getUser(p.getUniqueId());
        if (u.getNick() != null)
            p.setDisplayName(u.getNick());
        UUID uuid = p.getUniqueId();
        YamlConfiguration configLogIn = YamlConfiguration.loadConfiguration(configFileLogIn);
        if (!configLogIn.contains(uuid.toString())) {
            configLogIn.set(uuid.toString(), "{RANK} {NAME}&r joined the game.");
            try {
                configLogIn.save(configFileLogIn);
            } catch (Exception er) {
                er.printStackTrace();
            }
        }
        YamlConfiguration configLogOut = YamlConfiguration.loadConfiguration(configFileLogOut);
        if (!configLogOut.contains(uuid.toString())) {
            configLogOut.set(uuid.toString(), "{RANK} {NAME}&r Disconnected.");
            try {
                configLogOut.save(configFileLogOut);
            } catch (Exception er) {
                er.printStackTrace();
            }
        }
        e.setJoinMessage((ChatColor.GREEN + " + " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                configLogIn.getString(uuid.toString()).replaceAll("\\{NAME\\}", p.getDisplayName()).replaceAll("\\{RANK\\}",
                        um.getUser(p.getUniqueId()).getRank().getTitle()))).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
        bot.logIn(uuid);
        hide.playerJoined(p);
        if (hide.isHidden(e.getPlayer())) {
            Bukkit.broadcast(var.getMessages() + "To Ops -" + e.getJoinMessage(), "Necessities.opBroadcast");
            e.setJoinMessage(null);
            hide.hidePlayer(e.getPlayer());
        }
        if (!Necessities.getInstance().isProtocolLibLoaded())
            for (User m : um.getUsers().values())
                m.updateListName();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), new Runnable() {
            @Override
            public void run() {
                Necessities.getInstance().addHeader(p);
                Necessities.getInstance().addJanet(p);
                Necessities.getInstance().refreshJanet(p);
                Necessities.getInstance().updateAll(p);
                u.updateListName();
                File f = new File("plugins/Necessities/motd.txt");
                if (f.exists())
                    try {
                        BufferedReader read = new BufferedReader(new FileReader(f));
                        String line;
                        while ((line = read.readLine()) != null)
                            if (!line.equals(""))
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        read.close();
                    } catch (Exception er) {
                        er.printStackTrace();
                    }
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        YamlConfiguration configLogOut = YamlConfiguration.loadConfiguration(configFileLogOut);
        e.setQuitMessage((ChatColor.RED + " - " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                configLogOut.getString(uuid.toString()).replaceAll("\\{NAME\\}", e.getPlayer().getDisplayName()).replaceAll("\\{RANK\\}",
                        um.getUser(e.getPlayer().getUniqueId()).getRank().getTitle()))).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
        if (hide.isHidden(e.getPlayer())) {
            Bukkit.broadcast(var.getMessages() + "To Ops -" + e.getQuitMessage(), "Necessities.opBroadcast");
            e.setQuitMessage(null);
        }
        User u = um.getUser(e.getPlayer().getUniqueId());
        u.logOut();
        bot.logOut(uuid);
        um.removeUser(uuid);
        hide.playerLeft(e.getPlayer());
        tps.removeRequests(uuid);
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent e) {
        if (e.isCancelled())
            return;
        User u = um.getUser(e.getPlayer().getUniqueId());
        Location from = e.getFrom();
        Location to = e.getTo();
        Hat h = u.getHat();
        if (h != null)
            h.move(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ(), to.getYaw() - from.getYaw(), to.getPitch() - from.getPitch());
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        boolean locationChanged = Math.abs(from.getX() - to.getX()) > 0.1 || Math.abs(from.getY() - to.getY()) > 0.1 || Math.abs(from.getZ() - to.getZ()) > 0.1;
        if (config.contains("Necessities.WorldManager") && config.getBoolean("Necessities.WorldManager") && locationChanged) {
            Location destination = pm.portalDestination(to);
            if (destination != null)
                e.getPlayer().teleport(destination);
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        User u = um.getUser(e.getPlayer().getUniqueId());
        if (hide.isHidden(u.getPlayer()) && e.getAction().equals(Action.PHYSICAL))//cancel crop breaking when hidden
            e.setCancelled(true);
        if (e.getAction() == Action.LEFT_CLICK_BLOCK)
            u.setLeft(e.getClickedBlock().getLocation());
        else if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
            u.setRight(e.getClickedBlock().getLocation());
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent e) {
    	if (e.getBlock().getState() instanceof CommandBlock) {
    		CommandBlock b = (CommandBlock) e.getBlock().getState();
    		b.setCommand(ChatColor.translateAlternateColorCodes('&', b.getCommand()));
    		b.update(true);
    	}
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled())
            return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        User u = um.getUser(e.getPlayer().getUniqueId());
        Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        String status = u.getStatus();
        if (status.equals("dead"))
            status = ChatColor.RED + "[Dead] " + ChatColor.RESET;
        else if (status.equals("alive"))
            status = ChatColor.GREEN + "[Alive] " + ChatColor.RESET;
        String m = e.getMessage();
        if(m.endsWith(">") && ! m.equals(">")) {
            String appended = u.getAppended() + " " + m.substring(0, m.length() - 1);
            u.setAppended(appended.trim());
            player.sendMessage(ChatColor.GREEN + "Message appended.");
            e.setCancelled(true);
            return;
        } else if (!u.getAppended().equals("")) {
            e.setMessage(u.getAppended() + " " + m);
            u.setAppended("");
        }
        YamlConfiguration configTitles = YamlConfiguration.loadConfiguration(configFileTitles);
        e.setFormat(ChatColor.translateAlternateColorCodes('&', config.getString("Necessities.ChatFormat")));
        boolean isop = u.opChat();
        if (isop) {
            e.setFormat(var.getMessages() + "To Ops - " + ChatColor.WHITE + e.getFormat());
            e.setFormat(e.getFormat().replaceAll("\\{TITLE\\} ", ""));
        } else if (player.hasPermission("Necessities.opBroadcast") && e.getMessage().startsWith("#")) {
            isop = true;
            e.setFormat(var.getMessages() + "To Ops - " + ChatColor.WHITE + e.getFormat());
            e.setFormat(e.getFormat().replaceAll("\\{TITLE\\} ", ""));
            e.setMessage(e.getMessage().replaceFirst("#", ""));
        } else if (u.slackChat()) {
            e.setFormat(var.getMessages() + "To Slack - " + ChatColor.WHITE + e.getFormat());
            e.setFormat(e.getFormat().replaceAll("\\{TITLE\\} ", ""));
        }
        if (hide.isHidden(player) || isop || u.slackChat())
            status = "";
        String fullTitle = "";
        if (configTitles.contains(player.getUniqueId() + ".title")) {
            ChatColor brackets = ChatColor.getByChar(configTitles.getString(player.getUniqueId() + ".color"));
            String title = configTitles.getString(player.getUniqueId() + ".title");
            title = ChatColor.translateAlternateColorCodes('&', title);
            fullTitle = ChatColor.RESET + "" + brackets + "[" + ChatColor.RESET + title + ChatColor.RESET + "" + brackets + "] " + ChatColor.RESET;
        }
        e.setFormat(e.getFormat().replaceAll("\\{TITLE\\} ", fullTitle));
        e.setFormat(e.getFormat().replaceAll("\\{NAME\\}", player.getDisplayName()));
        String rank = ChatColor.translateAlternateColorCodes('&', um.getUser(uuid).getRank().getTitle());
        e.setFormat(e.getFormat().replaceAll("\\{RANK\\}", rank));
        final String message = bot.logChat(uuid, e.getMessage());
        e.setMessage(message);//Why did it not previously setMessage?
        if (player.hasPermission("Necessities.colorchat")) {
            if (player.hasPermission("Necessities.magicchat"))
                e.setMessage(ChatColor.translateAlternateColorCodes('&', message));
            else
                e.setMessage(ChatColor.translateAlternateColorCodes('&', message.replaceAll("&k", "")));
        }
        if (u.isMuted())
            player.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are muted.");
        else {
            if (!e.getRecipients().isEmpty()) {
                ArrayList<Player> toRem = new ArrayList<>();
                for (Player recip : e.getRecipients())
                    if (um.getUser(recip.getUniqueId()).isIgnoring(player.getUniqueId()) || (isop && !recip.hasPermission("Necessities.opBroadcast")) ||
                        (u.slackChat() && !recip.hasPermission("Necessities.slack")))
                        toRem.add(recip);
                for (Player recip : toRem)
                    e.getRecipients().remove(recip);
            }
            if (!e.getRecipients().isEmpty())
                for (Player recip : e.getRecipients())
                    recip.sendMessage(status + e.getFormat().replaceAll("\\{MESSAGE\\}", "") + e.getMessage());
            Bukkit.getConsoleSender().sendMessage(status + e.getFormat().replaceAll("\\{MESSAGE\\}", "") + e.getMessage());
            if (u.slackChat())
                slack.sendMessage(status + e.getFormat().replaceAll("\\{MESSAGE\\}", "") + e.getMessage());
        }
        e.setCancelled(true);
        if (config.contains("Necessities.AI") && config.getBoolean("Necessities.AI") && (!isop || message.startsWith("!")))
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ai.parseMessage(uuid, message);
                }
            });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity().getPlayer();
        if (player != null)
            bot.logDeath(player.getUniqueId(), e.getDeathMessage());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled())
            return;
        Player player = e.getPlayer();
        if (!e.getMessage().contains("login") && !e.getMessage().contains("register")) {
            spy.broadcast(player.getName(), e.getMessage());
            String message = bot.logCom(player.getUniqueId(), e.getMessage());
            e.setMessage(message);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            if (config.contains("Necessities.customDeny") && config.getBoolean("Necessities.customDeny")) {
                PluginCommand pc = null;
                try {
                    pc = Bukkit.getPluginCommand(e.getMessage().split(" ")[0].replaceFirst("/", ""));
                } catch (Exception er) {}//Invalid command
                if (pc != null && !pc.testPermissionSilent(player)) {
                    player.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You do not have permission to perform this command.");
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCommand(ServerCommandEvent e) {
        if (console.chatToggled() && !e.getCommand().equalsIgnoreCase("togglechat") && !e.getCommand().equalsIgnoreCase("tc"))
            e.setCommand("say " + e.getCommand());
        e.setCommand(ChatColor.translateAlternateColorCodes('&', e.getCommand()));
        spy.broadcast(console.getName().replaceAll(":", "") + ChatColor.AQUA, e.getCommand());
        bot.logConsole(e.getCommand());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.isCancelled())
            return;
        final User u = um.getUser(e.getPlayer().getUniqueId());
        Hat h = u.getHat();
        if (h != null) {
            if (!e.getFrom().getWorld().equals(e.getTo().getWorld())) {
                try {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            u.respawnHat();
                        }
                    });
                } catch (Exception er) {
                }
            } else {
                Location from = e.getFrom();
                Location to = e.getTo();
                h.move(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ(), to.getYaw() - from.getYaw(), to.getPitch() - from.getPitch());
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isCancelled())
            return;
        User u = um.getUser(e.getPlayer().getUniqueId());
        Hat h = u.getHat();
        if (h != null) {
            if (e.isSneaking())
                h.move(0, -0.25, 0, 0, 0);
            else
                h.move(0, 0.25, 0, 0, 0);
        }
    }

    @EventHandler
     public void onPickupItem(PlayerPickupItemEvent e) {
        if (hide.isHidden(e.getPlayer()))
            e.setCancelled(true);
    }
}