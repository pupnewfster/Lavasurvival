package com.crossge.necessities;

import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;
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

@SuppressWarnings("unused")
class Listeners implements Listener {
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
                    ChatColor.RESET + "Reason: " + banEntry.getReason() + ChatColor.RESET + "\nAppeal at galaxygaming.gg");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        UserManager um = Necessities.getUM();
        um.addUser(p);
        um.forceParseUser(p);
        final User u = um.getUser(p.getUniqueId());
        if (u.getNick() != null)
            p.setDisplayName(u.getNick());
        UUID uuid = p.getUniqueId();
        File configFileLogIn = new File(Necessities.getInstance().getDataFolder(), "loginmessages.yml");
        YamlConfiguration configLogIn = YamlConfiguration.loadConfiguration(configFileLogIn);
        if (!configLogIn.contains(uuid.toString())) {
            configLogIn.set(uuid.toString(), "{RANK} {NAME}&r joined the game.");
            try {
                configLogIn.save(configFileLogIn);
            } catch (Exception ignored) {
            }
        }
        File configFileLogOut = new File(Necessities.getInstance().getDataFolder(), "logoutmessages.yml");
        YamlConfiguration configLogOut = YamlConfiguration.loadConfiguration(configFileLogOut);
        if (!configLogOut.contains(uuid.toString())) {
            configLogOut.set(uuid.toString(), "{RANK} {NAME}&r Disconnected.");
            try {
                configLogOut.save(configFileLogOut);
            } catch (Exception ignored) {
            }
        }
        e.setJoinMessage((ChatColor.GREEN + " + " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', configLogIn.getString(uuid.toString()).replaceAll("\\{NAME}",
                p.getDisplayName()).replaceAll("\\{RANK}", um.getUser(p.getUniqueId()).getRank().getTitle()))).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
        Necessities.getBot().logIn(uuid);
        CmdHide hide = Necessities.getHide();
        hide.playerJoined(p);
        if (hide.isHidden(e.getPlayer())) {
            Bukkit.broadcast(Necessities.getVar().getMessages() + "To Ops -" + e.getJoinMessage(), "Necessities.opBroadcast");
            e.setJoinMessage(null);
            hide.hidePlayer(e.getPlayer());
        }
        Necessities.getEconomy().loadAccount(uuid);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> {
            Necessities.getInstance().addHeader(p);
            Necessities.getInstance().addJanet(p);
            Necessities.getInstance().updateAll(p);
            u.updateListName();
            File f = new File(Necessities.getInstance().getDataFolder(), "motd.txt");
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
        YamlConfiguration configLogOut = YamlConfiguration.loadConfiguration(new File(Necessities.getInstance().getDataFolder(), "logoutmessages.yml"));
        UserManager um = Necessities.getUM();
        e.setQuitMessage((ChatColor.RED + " - " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', configLogOut.getString(uuid.toString()).replaceAll("\\{NAME}",
                e.getPlayer().getDisplayName()).replaceAll("\\{RANK}", um.getUser(e.getPlayer().getUniqueId()).getRank().getTitle()))).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
        CmdHide hide = Necessities.getHide();
        if (hide.isHidden(e.getPlayer())) {
            Bukkit.broadcast(Necessities.getVar().getMessages() + "To Ops -" + e.getQuitMessage(), "Necessities.opBroadcast");
            e.setQuitMessage(null);
        }
        User u = um.getUser(e.getPlayer().getUniqueId());
        u.logOut();
        Necessities.getBot().logOut(uuid);
        Necessities.getEconomy().unloadAccount(uuid);
        um.removeUser(uuid);
        hide.playerLeft(e.getPlayer());
        Necessities.getTPs().removeRequests(uuid);
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        User u = Necessities.getUM().getUser(e.getPlayer().getUniqueId());
        if (Necessities.getHide().isHidden(u.getPlayer()) && e.getAction().equals(Action.PHYSICAL))//cancel crop breaking when hidden
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
        YamlConfiguration config = Necessities.getInstance().getConfig();
        UserManager um = Necessities.getUM();
        User u = um.getUser(e.getPlayer().getUniqueId());
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
        YamlConfiguration configTitles = YamlConfiguration.loadConfiguration(new File(Necessities.getInstance().getDataFolder(), "titles.yml"));
        e.setFormat(ChatColor.translateAlternateColorCodes('&', config.getString("Necessities.ChatFormat")));
        boolean isOp = false;
        Variables var = Necessities.getVar();
        if (u.slackChat()) {
            e.setFormat(var.getMessages() + "To Slack - " + ChatColor.WHITE + e.getFormat());
            e.setFormat(e.getFormat().replaceAll("\\{TITLE} ", ""));
        } else if (u.opChat()) {
            e.setFormat(var.getMessages() + "To Ops - " + ChatColor.WHITE + e.getFormat());
            e.setFormat(e.getFormat().replaceAll("\\{TITLE} ", ""));
        } else if (player.hasPermission("Necessities.opBroadcast") && e.getMessage().startsWith("#")) {
            isOp = true;
            e.setFormat(var.getMessages() + "To Ops - " + ChatColor.WHITE + e.getFormat());
            e.setFormat(e.getFormat().replaceAll("\\{TITLE} ", ""));
            e.setMessage(e.getMessage().replaceFirst("#", ""));
        }
        if (Necessities.getHide().isHidden(player) || isOp || u.slackChat())
            status = "";
        String fullTitle = "";
        if (configTitles.contains(player.getUniqueId() + ".title")) {
            ChatColor brackets = ChatColor.getByChar(configTitles.getString(player.getUniqueId() + ".color"));
            String title = configTitles.getString(player.getUniqueId() + ".title");
            title = ChatColor.translateAlternateColorCodes('&', title);
            fullTitle = ChatColor.RESET + "" + brackets + "[" + ChatColor.RESET + title + ChatColor.RESET + "" + brackets + "] " + ChatColor.RESET;
        }
        e.setFormat(e.getFormat().replaceAll("\\{TITLE} ", fullTitle));
        e.setFormat(e.getFormat().replaceAll("\\{NAME}", player.getDisplayName()));
        String rank = ChatColor.translateAlternateColorCodes('&', um.getUser(uuid).getRank().getTitle());
        e.setFormat(e.getFormat().replaceAll("\\{RANK}", rank));
        final String message = Necessities.getBot().logChat(uuid, e.getMessage());
        e.setMessage(message);//Why did it not previously setMessage?
        if (player.hasPermission("Necessities.colorchat"))
            e.setMessage(ChatColor.translateAlternateColorCodes('&', (player.hasPermission("Necessities.magicchat") ? message : message.replaceAll("&k", ""))));
        if (u.isMuted())
            player.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are muted.");
        else {
            if (!e.getRecipients().isEmpty()) {
                ArrayList<Player> toRem = new ArrayList<>();
                for (Player recip : e.getRecipients())
                    if (um.getUser(recip.getUniqueId()).isIgnoring(player.getUniqueId()) || (isOp && !recip.hasPermission("Necessities.opBroadcast")) ||
                            (u.slackChat() && !recip.hasPermission("Necessities.slack")))
                        toRem.add(recip);
                toRem.forEach(recip -> e.getRecipients().remove(recip));
            }
            if (!e.getRecipients().isEmpty()) {
                String finalStatus = status;
                e.getRecipients().forEach(recip -> recip.sendMessage(finalStatus + e.getFormat().replaceAll("\\{MESSAGE}", "") + e.getMessage()));
            }
            Bukkit.getConsoleSender().sendMessage(status + e.getFormat().replaceAll("\\{MESSAGE}", "") + e.getMessage());
            if (u.slackChat())
                Necessities.getSlack().sendMessage((status + e.getFormat().replaceAll("\\{MESSAGE}", "") + e.getMessage()).replaceFirst("To Slack - ", ""));
            else
                Necessities.getSlack().handleInGameChat(status + e.getFormat().replaceAll("\\{MESSAGE}", "") + e.getMessage());
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getEntity() != null)
            Necessities.getBot().logDeath(e.getEntity().getUniqueId(), e.getDeathMessage());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled())
            return;
        Player player = e.getPlayer();
        Necessities.getSpy().broadcast(player.getName(), e.getMessage());
        String message = Necessities.getBot().logCom(player.getUniqueId(), e.getMessage());
        if (message.startsWith("/tps"))
            e.setMessage(message.replaceFirst("tps", "necessities:tps"));
        else
            e.setMessage(message);
    }

    @EventHandler
    public void onCommand(ServerCommandEvent e) {
        Console console = Necessities.getConsole();
        if (console.chatToggled() && !e.getCommand().equalsIgnoreCase("togglechat") && !e.getCommand().equalsIgnoreCase("tc"))
            e.setCommand("say " + e.getCommand());
        e.setCommand(ChatColor.translateAlternateColorCodes('&', e.getCommand()));
        Necessities.getSpy().broadcast(console.getName().replaceAll(":", "") + ChatColor.AQUA, e.getCommand());
        Necessities.getBot().logConsole(e.getCommand());
        if (e.getCommand().startsWith("tps"))
            e.setCommand("necessities:" + e.getCommand());
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent e) {
        if (Necessities.getHide().isHidden(e.getPlayer()))
            e.setCancelled(true);
    }
}