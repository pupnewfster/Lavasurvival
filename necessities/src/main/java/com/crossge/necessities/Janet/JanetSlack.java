package com.crossge.necessities.Janet;

import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JanetSlack {
    private final HashMap<String, SlackUser> userMap = new HashMap<>();
    private final HashMap<Integer, ArrayList<String>> helpLists = new HashMap<>();
    private boolean isConnected;
    private String token;
    private URL hookURL;
    private WebSocket ws;

    public void init() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Connecting to Slack...");
        YamlConfiguration config = Necessities.getInstance().getConfig();
        this.token = config.contains("Necessities.SlackToken") ? config.getString("Necessities.SlackToken") : "token";
        String hook = config.contains("Necessities.WebHook") ? config.getString("Necessities.WebHook") : "webHook";
        if (this.token.equals("token") || hook.equals("webHook")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + " Failed to connect to Slack.");
            return;
        }
        try {
            this.hookURL = new URL(hook);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + " Failed to connect to Slack.");
            return;
        }
        setHelp();
        connect();
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Connected to Slack.");
    }

    public void disconnect() {
        if (!this.isConnected)
            return;
        this.userMap.clear();
        this.helpLists.clear();
        sendMessage("Disconnected.");
        sendPost("https://slack.com/api/users.setPresence?token=" + this.token + "&presence=away");
        this.isConnected = false;
        if (this.ws != null)
            this.ws.disconnect();
    }

    public void handleInGameChat(String message) {
        this.userMap.values().stream().filter(SlackUser::viewingChat).forEach(u -> u.sendPrivateMessage(message));
    }

    @SuppressWarnings("WeakerAccess")
    public void sendMessage(String message, boolean isPM, SlackUser u) {
        if (isPM)
            u.sendPrivateMessage(message);
        else
            sendMessage(message);
    }

    //sendPost("https://slack.com/api/chat.postMessage?token=" + token + "&channel=%23" + channel + "&text=" + ChatColor.stripColor(message.replaceAll(" ", "%20")) + "&as_user=true");
    public void sendMessage(String message) {
        message = ChatColor.stripColor(message);
        if (message.endsWith("\n"))
            message = message.substring(0, message.length() - 1);
        JsonObject json = new JsonObject();
        json.put("text", message);
        try {
            HttpsURLConnection con = (HttpsURLConnection) this.hookURL.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json;");
            con.setRequestProperty("Accept", "application/json,text/plain");
            con.setRequestMethod("POST");
            OutputStream os = con.getOutputStream();
            os.write(Jsoner.serialize(json).getBytes("UTF-8"));
            os.close();
            InputStream is = con.getInputStream();
            is.close();
            con.disconnect();
        } catch (Exception ignored) {
        }
    }

    private SlackUser getUserInfo(String id) {
        if (this.userMap.containsKey(id))
            return this.userMap.get(id);
        //If no cache of user, get it now.
        try {
            URL url = new URL("https://slack.com/api/users.info?token=" + this.token + "&user=" + id);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            con.disconnect();
            this.userMap.put(id, new SlackUser((JsonObject) Jsoner.deserialize(response.toString(), new JsonObject()).get("user")));
        } catch (Exception ignored) {
        }
        return this.userMap.get(id);
    }

    private void sendPost(String url) {
        try {
            HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");
            InputStream is = con.getInputStream();
            is.close();
            con.disconnect();
        } catch (Exception ignored) {
        }
    }

    private void connect() {
        if (this.isConnected)
            return;
        try {
            URL url = new URL("https://slack.com/api/rtm.connect?token=" + this.token);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            con.disconnect();
            openWebSocket(Jsoner.deserialize(response.toString(), new JsonObject()).getString("url"));
        } catch (Exception ignored) {
        }
        sendPost("https://slack.com/api/users.setPresence?token=" + this.token + "&presence=auto");
        this.isConnected = true;
        sendMessage("Connected.");
    }

    private void openWebSocket(String url) {
        if (url == null)
            return;
        try {
            this.ws = new WebSocketFactory().createSocket(url).addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    JsonObject json = Jsoner.deserialize(message, new JsonObject());
                    if (json.containsKey("type")) {
                        if (json.getString("type").equals("message")) {
                            if (json.containsKey("bot_id")) //For now we are not supporting bots
                                return;
                            String text = json.getString("text");
                            while (text.contains("<") && text.contains(">"))
                                text = text.split("<@")[0] + "@" + getUserInfo(text.split("<@")[1].split(">:")[0]).getName() + ":" + text.split("<@")[1].split(">:")[1];
                            String channel = json.getString("channel");
                            SlackUser info = getUserInfo(json.getString("user"));
                            if (channel.startsWith("D")) { //Direct Message
                                info.setChannel(channel);
                                sendSlackChat(info, text, true);
                            } else if (channel.startsWith("C") || channel.startsWith("G")) //Channel or Group
                                sendSlackChat(info, text, false);
                        }
                    }
                }
            }).connect();
        } catch (Exception ignored) {
        }
    }

    private String corTime(String time) {
        return time.length() == 1 ? "0" + time : time;
    }

    private String getLine(int page, int time, ArrayList<String> helpList) {
        page *= 10;
        if (helpList.size() < time + page + 1 || time == 10)
            return null;
        return helpList.get(page + time);
    }

    @SuppressWarnings("unchecked")
    private void setHelp() {
        ArrayList<String> temp = new ArrayList<>();
        temp.add("!help <page> ~ View the help messages on <page>.");
        temp.add("!rank ~ Shows you what rank you have.");
        temp.add("!bans ~ Shows you the banlist.");
        temp.add("!whois <name> ~ View information about <name>.");
        temp.add("!who ~ View the online players.");
        temp.add("!devs ~ View the devs.");
        temp.add("!worlds ~ View the loaded worlds.");
        this.helpLists.put(0, (ArrayList<String>) temp.clone());//Member
        temp.add("!warn <name> <reason> ~ Warn <name> for <reason>.");
        temp.add("!say <message> ~ Sends the message <message> to the players on the server.");
        temp.add("!kick <name> <reason> ~ Kicks <name> for an optional <reason>.");
        temp.add("!tempban <name> <time> <reason> ~ Tempbans <name> for <time> and an optional <reason>.");
        temp.add("!ban <name> <reason> ~ Bans <name> for an optional <reason>.");
        temp.add("!unban <name> ~ Unbans <name>.");
        temp.add("!banip <ip> <reason> ~ Bans <ip> for an optional <reason>.");
        temp.add("!unbanip <ip> ~ Unbans <ip>.");
        temp.add("!mute <name> ~ Mutes and unmutes <name>.");
        temp.add("!showchat ~ Toggles showing the in game chat. (Only available in private messages)");
        temp.add("!tps ~ Shows the in game ticks per second, and memory usage.");
        temp.add("!restart ~ Restarts the server.");
        temp.add("!consolecmd <command> ~ Perform a command through the console.");
        this.helpLists.put(1, (ArrayList<String>) temp.clone());//Admin
        this.helpLists.put(2, (ArrayList<String>) temp.clone());//Owner
        this.helpLists.put(3, (ArrayList<String>) temp.clone());//Primary owner
        temp.clear();
    }

    private void sendSlackChat(SlackUser info, String message, boolean isPM) {
        if (!info.isMember()) {
            sendMessage("Error: You are restricted or ultra restricted", isPM, info);
            return;
        }
        final String name = info.getName();
        Variables var = Necessities.getVar();
        if (message.startsWith("!")) {
            String m = "";
            if (message.startsWith("!help")) {
                int page = 0;
                if (message.split(" ").length > 1 && !Utils.legalInt(message.split(" ")[1])) {
                    sendMessage("Error: You must enter a valid help page.", isPM, info);
                    return;
                }
                if (message.split(" ").length > 1)
                    page = Integer.parseInt(message.split(" ")[1]);
                if (message.split(" ").length == 1 || page <= 0)
                    page = 1;
                int time = 0;
                int rounder = 0;
                ArrayList<String> helpList = helpLists.get(info.getRank());
                if (helpList.size() % 10 != 0)
                    rounder = 1;
                int totalpages = (helpList.size() / 10) + rounder;
                if (page > totalpages) {
                    sendMessage("Error: Input a number from 1 to " + Integer.toString(totalpages), isPM, info);
                    return;
                }
                m += " ---- Help -- Page " + Integer.toString(page) + "/" + Integer.toString(totalpages) + " ----\n";
                page = page - 1;
                String msg = getLine(page, time, helpList);
                StringBuilder mBuilder = new StringBuilder(m);
                while (msg != null) {
                    mBuilder.append(msg).append("\n");
                    time++;
                    msg = getLine(page, time, helpList);
                }
                m = mBuilder.toString();
                if (page + 1 < totalpages)
                    m += "Type !help " + Integer.toString(page + 2) + " to read the next page.\n";
            } else if (message.startsWith("!rank")) {
                m += info.getRankName() + "\n";
            } else if (message.startsWith("!bans") || message.startsWith("!banlist") || message.startsWith("!bannedplayers") || message.startsWith("!bannedips")) {
                BanList bans = Bukkit.getBanList(BanList.Type.NAME);
                m += "Banned players: ";
                StringBuilder mBuilder = new StringBuilder(m);
                for (BanEntry e : bans.getBanEntries())
                    mBuilder.append(e.getTarget()).append(", ");
                m = mBuilder.toString();
                if (m.endsWith(", "))
                    m = m.substring(0, m.length() - 2).trim();
                m += "\nBanned ips: ";
                bans = Bukkit.getBanList(BanList.Type.IP);
                StringBuilder mBuilder1 = new StringBuilder(m);
                for (BanEntry e : bans.getBanEntries())
                    mBuilder1.append(e.getTarget()).append(", ");
                m = mBuilder1.toString();
                if (m.endsWith(", "))
                    m = m.substring(0, m.length() - 2).trim();
            } else if (message.startsWith("!whois ")) {
                if (message.split(" ").length == 1) {
                    sendMessage("Error: You must enter a player to view info of.", isPM, info);
                    return;
                }
                String target = message.split(" ")[1];
                UUID uuid = Utils.getID(target);
                if (uuid == null) {
                    uuid = Utils.getOfflineID(target);
                    if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                        sendMessage("Error: That player does not exist or has not joined the server. If the player is offline, please use the full and most recent name.", isPM, info);
                        return;
                    }
                }
                User u = Necessities.getUM().getUser(uuid);
                m += "===== WhoIs: " + u.getName() + " =====\n";
                if (u.getPlayer() != null)
                    m += " - Nick: " + u.getPlayer().getDisplayName() + "\n";
                else {
                    m += (u.getNick() == null ? " - Name: " + u.getName() : " - Nick: " + u.getNick()) + "\n";
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(Bukkit.getOfflinePlayer(uuid).getLastPlayed());
                    String second = Integer.toString(c.get(Calendar.SECOND));
                    String minute = Integer.toString(c.get(Calendar.MINUTE));
                    String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
                    String day = Integer.toString(c.get(Calendar.DATE));
                    String month = Integer.toString(c.get(Calendar.MONTH) + 1);
                    String year = Integer.toString(c.get(Calendar.YEAR));
                    hour = corTime(hour);
                    minute = corTime(minute);
                    second = corTime(second);
                    m += " - Seen last on " + month + "/" + day + "/" + year + " at " + hour + ":" + minute + " and " + second + " " + (Integer.parseInt(second) > 1 ? "seconds" : "second") + "\n";
                }
                m += " - Time played: " + u.getTimePlayed() + "\n";
                m += " - Rank: " + u.getRank().getName() + "\n";
                String subranks = u.getSubranks();
                if (subranks != null)
                    m += " - Subranks: " + subranks + "\n";
                String permissions = u.getPermissions();
                if (permissions != null)
                    m += " - Permission nodes: " + permissions + "\n";
                if (u.getPlayer() != null) {
                    Player p = u.getPlayer();
                    m += " - Exp: " + Utils.addCommas(p.getTotalExperience()) + " (Level " + p.getLevel() + ")\n";
                    m += " - Location: (" + p.getWorld().getName() + ", " + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + ")\n";
                }
                if (u.getPlayer() != null) {
                    Player p = u.getPlayer();
                    m += " - IP Address: " + p.getAddress().toString().split("/")[1].split(":")[0] + "\n";
                    m += " - Gamemode: " + Utils.capFirst(p.getGameMode().toString()) + "\n";
                    m += " - Banned: " + (p.isBanned() ? "true" : "false") + "\n";
                    m += " - Visible: " + (Necessities.getHide().isHidden(p) ? "false" : "true") + "\n";
                } else
                    m += " - Banned: " + (Bukkit.getOfflinePlayer(u.getUUID()).isBanned() ? "true" : "false") + "\n";
            } else if (message.startsWith("!who")) {
                int numbOnline = Bukkit.getOnlinePlayers().size() + 1;
                HashMap<Rank, String> online = new HashMap<>();
                RankManager rm = Necessities.getRM();
                if (!rm.getOrder().isEmpty())
                    online.put(rm.getRank(rm.getOrder().size() - 1), rm.getRank(rm.getOrder().size() - 1).getColor() + "Janet, ");
                UserManager um = Necessities.getUM();
                CmdHide hide = Necessities.getHide();
                if (!um.getUsers().isEmpty())
                    for (User u : um.getUsers().values())
                        if (hide.isHidden(u.getPlayer()))
                            online.put(u.getRank(), online.containsKey(u.getRank()) ? online.get(u.getRank()) + "[HIDDEN]" + u.getPlayer().getDisplayName() + ", " : "[HIDDEN]" + u.getPlayer().getDisplayName() + ", ");
                        else
                            online.put(u.getRank(), online.containsKey(u.getRank()) ? online.get(u.getRank()) + u.getPlayer().getDisplayName() + ", " : u.getPlayer().getDisplayName() + ", ");
                m += "There " + (numbOnline == 1 ? "is " : "are ") + numbOnline + " out of a maximum " + Bukkit.getMaxPlayers() + " players online.\n";
                StringBuilder mBuilder = new StringBuilder(m);
                for (int i = rm.getOrder().size() - 1; i >= 0; i--) {
                    Rank r = rm.getRank(i);
                    if (online.containsKey(r))
                        mBuilder.append(r.getName()).append("s: ").append(online.get(r).trim().substring(0, online.get(r).length() - 2)).append("\n");
                }
                m = mBuilder.toString();
            } else if (message.startsWith("!devs")) {
                StringBuilder d = new StringBuilder(var.getMessages() + "The Devs for Necessities are: ");
                List<Necessities.DevInfo> devs = Necessities.getInstance().getDevs();
                for (int i = 0; i < devs.size(); i++)
                    d.append(i + 1 >= devs.size() ? "and " + devs.get(i).getName() + "." : devs.get(i).getName() + ", ");
                m += d + "\n";
            } else if (message.startsWith("!worlds")) {
                ArrayList<String> worlds = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toCollection(ArrayList::new));
                StringBuilder levelsBuilder = new StringBuilder();
                for (int i = 0; i < worlds.size() - 1; i++)
                    levelsBuilder.append(worlds.get(i)).append(", ");
                levelsBuilder.append("and ").append(worlds.get(worlds.size() - 1)).append(".");
                m += "The worlds are: " + levelsBuilder.toString() + "\n";
            } else if (message.startsWith("!warn ") && info.isAdmin()) {
                message = message.replaceFirst("!warn ", "");
                if (message.split(" ").length < 2) {
                    sendMessage("Error: You must enter a player to warn and a reason.", isPM, info);
                    return;
                }
                UUID uuid = Utils.getID(message.split(" ")[0]);
                if (uuid == null) {
                    sendMessage("Error: Invalid player.", isPM, info);
                    return;
                }
                final Player target = Bukkit.getPlayer(uuid);
                if (target.hasPermission("Necessities.antiPWarn")) {
                    sendMessage("Error: You may not warn someone who has Necessities.antiPWarn.", isPM, info);
                    return;
                }
                final String reason = message.replaceFirst(message.split(" ")[0], "").trim();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> Necessities.getWarns().warn(target.getUniqueId(), reason, name));
                m += target.getName() + " was warned by " + name + " for " + reason + ".\n";
            } else if (message.startsWith("!say ") && info.isAdmin()) {
                Bukkit.broadcastMessage(ChatColor.WHITE + name + ": " + message.replaceFirst("!say ", ""));
                return;
            } else if (message.startsWith("!kick ") && info.isAdmin()) {
                message = message.replaceFirst("!kick ", "");
                if (message.split(" ").length == 0) {
                    sendMessage("Error: You must enter a player to kick and a reason.", isPM, info);
                    return;
                }
                UUID uuid = Utils.getID(message.split(" ")[0]);
                if (uuid == null) {
                    sendMessage("Error: Invalid player.", isPM, info);
                    return;
                }
                final Player target = Bukkit.getPlayer(uuid);
                final String reason = ChatColor.translateAlternateColorCodes('&', message.replaceFirst(message.split(" ")[0], "").trim());
                Bukkit.broadcastMessage(var.getMessages() + name + " kicked " + var.getObj() + target.getName() + (reason.equals("") ? "" : var.getMessages() + " for " + var.getObj() + reason));
                m += name + " kicked " + target.getName() + (reason.equals("") ? "" : " for " + reason) + "\n";
                Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> target.kickPlayer(reason));
            } else if (message.startsWith("!ban ") && info.isAdmin()) {
                message = message.replaceFirst("!ban ", "");
                if (message.split(" ").length == 0) {
                    sendMessage("Error: You must enter a player to ban.", isPM, info);
                    return;
                }
                UUID uuid = Utils.getID(message.split(" ")[0]);
                if (uuid == null) {
                    uuid = Utils.getOfflineID(message.split(" ")[0]);
                    if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                        sendMessage("Error: That player does not exist or has not joined the server. If the player is offline, please use the full and most recent name.", isPM, info);
                        return;
                    }
                }
                final OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                if (target.getPlayer() != null && target.getPlayer().hasPermission("Necessities.antiBan") && !info.isOwner()) {
                    sendMessage("Error: You may not ban someone who has Necessities.antiBan.", isPM, info);
                    return;
                }
                final String reason = (message.split(" ").length > 1) ? message.replaceFirst(message.split(" ")[0], "").trim() : "";
                String theirName = target.getName();
                BanList bans = Bukkit.getBanList(BanList.Type.NAME);
                if (target.getPlayer() != null) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> target.getPlayer().kickPlayer(reason));
                }
                bans.addBan(theirName, reason, null, "Slack_" + name);
                Bukkit.broadcastMessage(var.getMessages() + name + " banned " + var.getObj() + theirName + var.getMessages() + (reason.equals("") ? "." : " for " + var.getObj() + reason + var.getMessages() + "."));
                m += name + " banned " + theirName + (reason.equals("") ? "." : " for " + reason + ".") + "\n";
            } else if (message.startsWith("!unban ") && info.isAdmin()) {
                message = message.replaceFirst("!unban ", "");
                if (message.split(" ").length == 0) {
                    sendMessage("Error: You must enter a player to unban.", isPM, info);
                    return;
                }
                UUID uuid = Utils.getID(message.split(" ")[0]);
                if (uuid == null) {
                    uuid = Utils.getOfflineID(message.split(" ")[0]);
                    if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                        sendMessage("Error: That player does not exist or has not joined the server. If the player is offline, please use the full and most recent name.", isPM, info);
                        return;
                    }
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                BanList bans = Bukkit.getBanList(BanList.Type.NAME);
                String theirName = target.getName();
                if (!bans.isBanned(theirName)) {
                    sendMessage("Error: That player is not banned.", isPM, info);
                    return;
                }
                bans.pardon(theirName);
                Bukkit.broadcastMessage(var.getMessages() + name + " unbanned " + theirName + ".");
                m += name + " unbanned " + theirName + ".\n";
            } else if (message.startsWith("!mute ") && info.isAdmin()) {
                message = message.replaceFirst("!mute ", "");
                if (message.split(" ").length == 0) {
                    sendMessage("Error: You must enter a player to mute.", isPM, info);
                    return;
                }
                UUID uuid = Utils.getID(message.split(" ")[0]);
                if (uuid == null) {
                    sendMessage("Error: Invalid player.", isPM, info);
                    return;
                }
                User u = Necessities.getUM().getUser(uuid);
                Bukkit.broadcastMessage(var.getObj() + name + var.getMessages() + (!u.isMuted() ? " muted " : " unmuted ") + var.getObj() + u.getPlayer().getDisplayName() + var.getMessages() + ".");
                u.getPlayer().sendMessage(var.getDemote() + "You have been " + var.getObj() + (!u.isMuted() ? "muted" : "unmuted") + var.getMessages() + ".");
                m += name + (!u.isMuted() ? " muted " : " unmuted ") + u.getPlayer().getDisplayName() + ".\n";
                u.setMuted(!u.isMuted());
            } else if (message.startsWith("!tempban ") && info.isAdmin()) {
                message = message.replaceFirst("!tempban ", "");
                if (message.split(" ").length < 2) {
                    sendMessage("Error: You must enter a player to ban and a duration in minutes.", isPM, info);
                    return;
                }
                UUID uuid = Utils.getID(message.split(" ")[0]);
                if (uuid == null) {
                    uuid = Utils.getOfflineID(message.split(" ")[0]);
                    if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                        sendMessage("Error: That player does not exist or has not joined the server. If the player is offline, please use the full and most recent name.", isPM, info);
                        return;
                    }
                }
                final OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                if (target.getPlayer() != null && target.getPlayer().hasPermission("Necessities.antiBan") && !info.isOwner()) {
                    sendMessage("Error: You may not ban someone who has Necessities.antiBan.", isPM, info);
                    return;
                }
                int minutes;
                try {
                    minutes = Integer.parseInt(message.split(" ")[1]);
                } catch (Exception e) {
                    sendMessage("Error: Invalid time, please enter a time in minutes.", isPM, info);
                    return;
                }
                if (minutes < 0) {
                    sendMessage("Error: Invalid time, please enter a time in minutes.", isPM, info);
                    return;
                }
                final String reason = (message.split(" ").length > 2) ? message.replaceFirst(message.split(" ")[0], "").trim() : "";
                BanList bans = Bukkit.getBanList(BanList.Type.NAME);
                String theirName = target.getName();
                if (target.getPlayer() != null) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> target.getPlayer().kickPlayer(reason));
                }
                Date date = new Date(System.currentTimeMillis() + minutes * 60 * 1000);
                bans.addBan(theirName, reason, date, "Slack_" + name);
                Bukkit.broadcastMessage(var.getMessages() + name + " banned " + var.getObj() + theirName + var.getMessages() + " for " + var.getObj() + minutes + var.getMessages() +
                        " " + (minutes == 1 ? "minute" : "minutes") + (reason.equals("") ? "." : " for the reason " + var.getObj() + reason + var.getMessages() + "."));
                m += name + " banned " + theirName + " for " + minutes + " " + (minutes == 1 ? "minute" : "minutes") + (reason.equals("") ? "." : " for the reason " + reason + ".") + "\n";
            } else if (message.startsWith("!banip ") && info.isAdmin()) {
                message = message.replaceFirst("!banip ", "");
                if (message.split(" ").length == 0) {
                    sendMessage("Error: You must enter an ip to ban.", isPM, info);
                    return;
                }
                UUID uuid = Utils.getID(message.split(" ")[0]);
                if (uuid != null) {
                    final Player target = Bukkit.getPlayer(uuid);
                    if (target.hasPermission("Necessities.antiBan") && !info.isOwner()) {
                        sendMessage("Error: You may not ban someone who has Necessities.antiBan.", isPM, info);
                        return;
                    }
                    final String reason = (message.split(" ").length > 1) ? message.replaceFirst(message.split(" ")[0], "").trim() : "";
                    String theirName = target.getName();
                    BanList bans = Bukkit.getBanList(BanList.Type.IP);
                    String theirIP = target.getAddress().toString().split("/")[1].split(":")[0];
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> target.getPlayer().kickPlayer(reason));
                    bans.addBan(theirIP, reason, null, "Slack_" + name);
                    Bukkit.broadcastMessage(var.getMessages() + name + " banned " + var.getObj() + theirName + var.getMessages() + (reason.equals("") ? "." : " for " + var.getObj() + reason + var.getMessages() + "."));
                    m += name + " banned " + theirName + (reason.equals("") ? "." : " for " + reason + ".") + "\n";
                } else {
                    boolean validIp = false;
                    try {
                        Pattern ipAdd = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
                        validIp = ipAdd.matcher(message.split(" ")[0]).matches();
                    } catch (Exception ignored) {
                    }
                    if (!validIp) {
                        sendMessage("Error: Invalid ip.", isPM, info);
                        return;
                    }
                    final String reason = (message.split(" ").length > 1) ? message.replaceFirst(message.split(" ")[0], "").trim() : "";
                    BanList bans = Bukkit.getBanList(BanList.Type.IP);
                    String theirIP = message.split(" ")[0];
                    Bukkit.getOnlinePlayers().stream().filter(t -> t.getAddress().toString().split("/")[1].split(":")[0].equals(theirIP)).findFirst().ifPresent(t -> Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> t.getPlayer().kickPlayer(reason)));
                    bans.addBan(theirIP, reason, null, "Console");
                    Bukkit.broadcastMessage(var.getMessages() + name + " banned " + var.getObj() + theirIP + var.getMessages() + (reason.equals("") ? "." : " for " + var.getObj() + reason + var.getMessages() + "."));
                    m += name + " banned " + theirIP + (reason.equals("") ? "." : " for " + reason + ".") + "\n";
                }
            } else if (message.startsWith("!unbanip ") && info.isAdmin()) {
                message = message.replaceFirst("!unbanip ", "");
                if (message.split(" ").length == 0) {
                    sendMessage("Error: You must enter an ip to unban.", isPM, info);
                    return;
                }
                boolean validIp = false;
                try {
                    Pattern ipAdd = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
                    validIp = ipAdd.matcher(message.split(" ")[0]).matches();
                } catch (Exception ignored) {
                }
                if (!validIp) {
                    sendMessage("Error: Invalid ip.", isPM, info);
                    return;
                }
                BanList bans = Bukkit.getBanList(BanList.Type.IP);
                String theirIP = message.split(" ")[0];
                if (!bans.isBanned(theirIP)) {
                    sendMessage("Error: That ip is not banned.", isPM, info);
                    return;
                }
                bans.pardon(theirIP);
                Bukkit.broadcastMessage(var.getMessages() + name + " unbanned " + theirIP + ".");
                m += name + " unbanned " + theirIP + ".\n";
            } else if (message.startsWith("!tps") && info.isAdmin()) {
                m += Utils.getTPS() + "\n";
                int mb = 1024 * 1024;
                Runtime runtime = Runtime.getRuntime();
                m += "Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / mb + " mb / " + runtime.maxMemory() / mb + " mb\n";
                StringBuilder mBuilder = new StringBuilder(m);
                for (World w : Bukkit.getWorlds())
                    mBuilder.append("World: ").append(w.getName()).append(", Entities Loaded: ").append(w.getEntities().size()).append(", Chunks Loaded: ").append(w.getLoadedChunks().length).append("\n");
                m = mBuilder.toString();
            } else if (message.startsWith("!restart") && info.isAdmin()) {
                sendMessage("Restarting...", isPM, info);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "endgame");
                Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> Bukkit.spigot().restart(), 20);
                return;
            } else if ((message.startsWith("!consolecmd") || message.startsWith("!ccmd") || message.startsWith("!consolecommand")) && info.isAdmin()) {//TODO Make it show the result to slack
                final String command = message.replaceFirst(message.split(" ")[0], "").trim();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                return;
            } else if ((message.startsWith("!showchat") || message.startsWith("!togglechat") || message.startsWith("!showingamechat") || message.startsWith("!ingamechat")) && info.isAdmin()) {
                if (!isPM)
                    m += "Error: You must pm me to be able to view in game chat.\n";
                else {
                    m += (info.viewingChat() ? "No longer" : "You are now") + " viewing the in game chat.\n";
                    info.toggleViewingChat();
                }
            } else if (!isPM) {
                Bukkit.broadcast(var.getMessages() + "From Slack - " + ChatColor.WHITE + name + ": " + message, "Necessities.slack");
                return;
            }
            sendMessage(m, isPM, info);
        } else if (!isPM)
            Bukkit.broadcast(var.getMessages() + "From Slack - " + ChatColor.WHITE + name + ": " + message, "Necessities.slack");
    }

    @SuppressWarnings("unused")
    class SlackUser {
        private boolean justLoaded = true, viewingChat;
        private final String id;
        private final String name;
        private String latest;
        private String channel;
        private int rank;

        SlackUser(JsonObject json) {
            this.id = json.getString("id");
            this.name = json.getString("name");
            if (json.getBoolean("is_primary_owner"))
                this.rank = 3;
            else if (json.getBoolean("is_owner"))
                this.rank = 2;
            else if (json.getBoolean("is_admin"))
                this.rank = 1;
            else if (json.getBoolean("is_ultra_restricted"))
                this.rank = -2;
            else if (json.getBoolean("is_restricted"))
                this.rank = -1;
            //else leave it at 0 for member
        }

        public String getName() {
            return this.name;
        }

        public String getID() {
            return this.id;
        }

        public int getRank() {
            return this.rank;
        }

        boolean isUltraRestricted() {
            return this.rank >= -2;
        }

        boolean isRestricted() {
            return this.rank >= -1;
        }

        boolean isMember() {
            return this.rank >= 0;
        }

        boolean isAdmin() {
            return this.rank >= 1;
        }

        public boolean isOwner() {
            return this.rank >= 2;
        }

        boolean isPrimaryOwner() {
            return this.rank >= 3;
        }

        String getRankName() {
            if (isPrimaryOwner())
                return "Primary Owner";
            else if (isOwner())
                return "Owner";
            else if (isAdmin())
                return "Admin";
            else if (isMember())
                return "Member";
            else if (isRestricted())
                return "Restricted";
            else if (isUltraRestricted())
                return "Ultra Restricted";
            return "Error";
        }

        boolean viewingChat() {
            return this.viewingChat;
        }

        void toggleViewingChat() {
            this.viewingChat = !this.viewingChat;
        }

        public void setJustLoaded(boolean loaded) {
            this.justLoaded = loaded;
        }

        public boolean getJustLoaded() {
            return this.justLoaded;
        }

        public String getLatest() {
            return this.latest;
        }

        public void setLatest(String latest) {
            this.latest = latest;
        }

        public String getChannel() {
            return this.channel;
        }

        void setChannel(String channel) {
            this.channel = channel;
        }

        void sendPrivateMessage(String message) {
            if (message.endsWith("\n"))
                message = message.substring(0, message.length() - 1);
            JsonObject json = new JsonObject();
            json.put("text", message);
            json.put("channel", this.channel);
            try {
                HttpsURLConnection con = (HttpsURLConnection) hookURL.openConnection();
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json;");
                con.setRequestProperty("Accept", "application/json,text/plain");
                con.setRequestMethod("POST");
                OutputStream os = con.getOutputStream();
                os.write(Jsoner.serialize(json).getBytes("UTF-8"));
                os.close();
                InputStream is = con.getInputStream();
                is.close();
                con.disconnect();
            } catch (Exception ignored) {
            }
        }
    }
}