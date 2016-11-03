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
import org.bukkit.*;
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
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

class Listeners implements Listener {
    private File configFileLogOut = new File("plugins/Necessities", "logoutmessages.yml"), configFileLogIn = new File("plugins/Necessities", "loginmessages.yml"),
            configFileTitles = new File("plugins/Necessities", "titles.yml"), configFile = new File("plugins/Necessities", "config.yml");
    private CmdCommandSpy spy = Necessities.getInstance().getSpy();
    private PortalManager pm = Necessities.getInstance().getPM();
    private JanetSlack slack = Necessities.getInstance().getSlack();
    private UserManager um = Necessities.getInstance().getUM();
    private Console console = Necessities.getInstance().getConsole();
    private Variables var = Necessities.getInstance().getVar();
    private Teleports tps = Necessities.getInstance().getTPs();
    private CmdHide hide = Necessities.getInstance().getHide();
    private JanetAI ai = Necessities.getInstance().getAI();
    private Janet bot = Necessities.getInstance().getBot();

    private String corTime(String time) {
        return time.length() == 1 ? "0" + time : time;
    }

    private String getDateAndTime(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        String second = Integer.toString(c.get(Calendar.SECOND));
        String minute = Integer.toString(c.get(Calendar.MINUTE));
        String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        String day = Integer.toString(c.get(Calendar.DATE));
        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        String year = Integer.toString(c.get(Calendar.YEAR));
        hour = corTime(hour);
        minute = corTime(minute);
        second = corTime(second);
        return month + "/" + day + "/" + year + " at " + hour + ":" + minute + ":" + second;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (e.getResult().equals(PlayerLoginEvent.Result.KICK_BANNED)) {
            BanEntry banEntry = null;
            if (Bukkit.getBanList(BanList.Type.NAME).isBanned(e.getPlayer().getName()))
                banEntry = Bukkit.getBanList(BanList.Type.NAME).getBanEntry(e.getPlayer().getName());
            else if (Bukkit.getBanList(BanList.Type.IP).isBanned(e.getAddress().toString().split("/")[1].split(":")[0]))
                banEntry = Bukkit.getBanList(BanList.Type.IP).getBanEntry(e.getAddress().toString().split("/")[1].split(":")[0]);
            if (banEntry == null)
                return;
            e.setKickMessage("You were " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANNED" + ChatColor.RESET + " on " + getDateAndTime(banEntry.getCreated()) + " by " +
                    banEntry.getSource() + ChatColor.RESET + ".\n" + (banEntry.getExpiration() == null ? "" : "Your ban will expire on " + getDateAndTime(banEntry.getExpiration()) + ".\n") +
                    ChatColor.RESET + "Reason: " + banEntry.getReason() + ChatColor.RESET + "\nAppeal at smp.gamezgalaxy.com/ban");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        this.um.addUser(p);
        this.um.forceParseUser(p);
        final User u = this.um.getUser(p.getUniqueId());
        if (u.getNick() != null)
            p.setDisplayName(u.getNick());
        UUID uuid = p.getUniqueId();
        YamlConfiguration configLogIn = YamlConfiguration.loadConfiguration(this.configFileLogIn);
        if (!configLogIn.contains(uuid.toString())) {
            configLogIn.set(uuid.toString(), "{RANK} {NAME}&r joined the game.");
            try {
                configLogIn.save(this.configFileLogIn);
            } catch (Exception ignored) {
            }
        }
        YamlConfiguration configLogOut = YamlConfiguration.loadConfiguration(this.configFileLogOut);
        if (!configLogOut.contains(uuid.toString())) {
            configLogOut.set(uuid.toString(), "{RANK} {NAME}&r Disconnected.");
            try {
                configLogOut.save(this.configFileLogOut);
            } catch (Exception ignored) {
            }
        }
        e.setJoinMessage((ChatColor.GREEN + " + " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                configLogIn.getString(uuid.toString()).replaceAll("\\{NAME\\}", p.getDisplayName()).replaceAll("\\{RANK\\}",
                        this.um.getUser(p.getUniqueId()).getRank().getTitle()))).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
        this.bot.logIn(uuid);
        this.hide.playerJoined(p);
        if (this.hide.isHidden(e.getPlayer())) {
            Bukkit.broadcast(var.getMessages() + "To Ops -" + e.getJoinMessage(), "Necessities.opBroadcast");
            e.setJoinMessage(null);
            this.hide.hidePlayer(e.getPlayer());
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> {
            Necessities.getInstance().addHeader(p);
            Necessities.getInstance().addJanet(p);
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
                } catch (Exception ignored) {
                }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        YamlConfiguration configLogOut = YamlConfiguration.loadConfiguration(this.configFileLogOut);
        e.setQuitMessage((ChatColor.RED + " - " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                configLogOut.getString(uuid.toString()).replaceAll("\\{NAME\\}", e.getPlayer().getDisplayName()).replaceAll("\\{RANK\\}",
                        this.um.getUser(e.getPlayer().getUniqueId()).getRank().getTitle()))).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
        if (this.hide.isHidden(e.getPlayer())) {
            Bukkit.broadcast(var.getMessages() + "To Ops -" + e.getQuitMessage(), "Necessities.opBroadcast");
            e.setQuitMessage(null);
        }
        User u = this.um.getUser(e.getPlayer().getUniqueId());
        u.logOut();
        this.bot.logOut(uuid);
        this.um.removeUser(uuid);
        this.hide.playerLeft(e.getPlayer());
        this.tps.removeRequests(uuid);
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent e) {
        if (e.isCancelled())
            return;
        User u = this.um.getUser(e.getPlayer().getUniqueId());
        Location from = e.getFrom();
        Location to = e.getTo();
        Hat h = u.getHat();
        if (h != null)
            h.move(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ(), to.getYaw() - from.getYaw(), to.getPitch() - from.getPitch());
        YamlConfiguration config = YamlConfiguration.loadConfiguration(this.configFile);
        boolean locationChanged = Math.abs(from.getX() - to.getX()) > 0.1 || Math.abs(from.getY() - to.getY()) > 0.1 || Math.abs(from.getZ() - to.getZ()) > 0.1;
        if (config.contains("Necessities.WorldManager") && config.getBoolean("Necessities.WorldManager") && locationChanged) {
            Location destination = this.pm.portalDestination(to);
            if (destination != null)
                e.getPlayer().teleport(destination);
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        User u = this.um.getUser(e.getPlayer().getUniqueId());
        if (this.hide.isHidden(u.getPlayer()) && e.getAction().equals(Action.PHYSICAL))//cancel crop breaking when hidden
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
        YamlConfiguration config = YamlConfiguration.loadConfiguration(this.configFile);
        User u = this.um.getUser(e.getPlayer().getUniqueId());
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        String status = u.getStatus();
        if (status.equals("dead"))
            status = ChatColor.RED + "[Dead] " + ChatColor.RESET;
        else if (status.equals("alive"))
            status = ChatColor.GREEN + "[Alive] " + ChatColor.RESET;
        String m = e.getMessage();
        if (m.endsWith(">") && !m.equals(">")) {
            String appended = u.getAppended() + " " + m.substring(0, m.length() - 1);
            u.setAppended(appended.trim());
            player.sendMessage(ChatColor.GREEN + "Message appended.");
            e.setCancelled(true);
            return;
        } else if (!u.getAppended().equals("")) {
            e.setMessage(u.getAppended() + " " + m);
            u.setAppended("");
        }
        YamlConfiguration configTitles = YamlConfiguration.loadConfiguration(this.configFileTitles);
        e.setFormat(ChatColor.translateAlternateColorCodes('&', config.getString("Necessities.ChatFormat")));
        boolean isop = false;
        if (u.slackChat()) {
            e.setFormat(this.var.getMessages() + "To Slack - " + ChatColor.WHITE + e.getFormat());
            e.setFormat(e.getFormat().replaceAll("\\{TITLE\\} ", ""));
        } else if (u.opChat()) {
            e.setFormat(this.var.getMessages() + "To Ops - " + ChatColor.WHITE + e.getFormat());
            e.setFormat(e.getFormat().replaceAll("\\{TITLE\\} ", ""));
        } else if (player.hasPermission("Necessities.opBroadcast") && e.getMessage().startsWith("#")) {
            isop = true;
            e.setFormat(this.var.getMessages() + "To Ops - " + ChatColor.WHITE + e.getFormat());
            e.setFormat(e.getFormat().replaceAll("\\{TITLE\\} ", ""));
            e.setMessage(e.getMessage().replaceFirst("#", ""));
        }
        if (this.hide.isHidden(player) || isop || u.slackChat())
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
        String rank = ChatColor.translateAlternateColorCodes('&', this.um.getUser(uuid).getRank().getTitle());
        e.setFormat(e.getFormat().replaceAll("\\{RANK\\}", rank));
        final String message = this.bot.logChat(uuid, e.getMessage());
        e.setMessage(message);//Why did it not previously setMessage?
        if (player.hasPermission("Necessities.colorchat"))
            e.setMessage(ChatColor.translateAlternateColorCodes('&', (player.hasPermission("Necessities.magicchat") ? message : message.replaceAll("&k", ""))));
        if (u.isMuted())
            player.sendMessage(this.var.getEr() + "Error: " + this.var.getErMsg() + "You are muted.");
        else {
            if (!e.getRecipients().isEmpty()) {
                ArrayList<Player> toRem = new ArrayList<>();
                for (Player recip : e.getRecipients())
                    if (this.um.getUser(recip.getUniqueId()).isIgnoring(player.getUniqueId()) || (isop && !recip.hasPermission("Necessities.opBroadcast")) ||
                            (u.slackChat() && !recip.hasPermission("Necessities.slack")))
                        toRem.add(recip);
                toRem.forEach(recip -> e.getRecipients().remove(recip));
            }
            if (!e.getRecipients().isEmpty()) {
                String finalStatus = status;
                e.getRecipients().forEach(recip -> recip.sendMessage(finalStatus + e.getFormat().replaceAll("\\{MESSAGE\\}", "") + e.getMessage()));
            }
            Bukkit.getConsoleSender().sendMessage(status + e.getFormat().replaceAll("\\{MESSAGE\\}", "") + e.getMessage());
            if (u.slackChat())
                this.slack.sendMessage((status + e.getFormat().replaceAll("\\{MESSAGE\\}", "") + e.getMessage()).replaceFirst("To Slack - ", ""));
            else
                this.slack.handleIngameChat(status + e.getFormat().replaceAll("\\{MESSAGE\\}", "") + e.getMessage());
        }
        e.setCancelled(true);
        if (config.contains("Necessities.AI") && config.getBoolean("Necessities.AI") && (!isop || message.startsWith("!")))
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> ai.parseMessage(player.getName(), message, JanetAI.Source.Server, false, null));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity().getPlayer();
        if (player != null)
            this.bot.logDeath(player.getUniqueId(), e.getDeathMessage());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled())
            return;
        Player player = e.getPlayer();
        if (!e.getMessage().contains("login") && !e.getMessage().contains("register")) {
            this.spy.broadcast(player.getName(), e.getMessage());
            String message = this.bot.logCom(player.getUniqueId(), e.getMessage());
            e.setMessage(message);
            if (e.getMessage().startsWith("/tps"))
                e.setMessage(e.getMessage().replaceFirst("tps", "necessities:tps"));
            YamlConfiguration config = YamlConfiguration.loadConfiguration(this.configFile);
            if (config.contains("Necessities.customDeny") && config.getBoolean("Necessities.customDeny")) {
                PluginCommand pc = null;
                try {
                    pc = Bukkit.getPluginCommand(e.getMessage().split(" ")[0].replaceFirst("/", ""));
                } catch (Exception ignored) {
                }//Invalid command
                if (pc != null && !pc.testPermissionSilent(player)) {
                    player.sendMessage(this.var.getEr() + "Error: " + this.var.getErMsg() + "You do not have permission to perform this command.");
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCommand(ServerCommandEvent e) {
        if (this.console.chatToggled() && !e.getCommand().equalsIgnoreCase("togglechat") && !e.getCommand().equalsIgnoreCase("tc"))
            e.setCommand("say " + e.getCommand());
        e.setCommand(ChatColor.translateAlternateColorCodes('&', e.getCommand()));
        this.spy.broadcast(this.console.getName().replaceAll(":", "") + ChatColor.AQUA, e.getCommand());
        this.bot.logConsole(e.getCommand());
        if (e.getCommand().startsWith("tps"))
            e.setCommand("necessities:" + e.getCommand());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.isCancelled())
            return;
        final User u = this.um.getUser(e.getPlayer().getUniqueId());
        Hat h = u.getHat();
        if (h != null) {
            if (!e.getFrom().getWorld().equals(e.getTo().getWorld())) {
                try {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), u::respawnHat);
                } catch (Exception ignored) {
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
        User u = this.um.getUser(e.getPlayer().getUniqueId());
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
        if (this.hide.isHidden(e.getPlayer()))
            e.setCancelled(true);
    }
}