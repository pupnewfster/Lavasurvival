package com.crossge.necessities.Janet;

import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.Economy.BalChecks;
import com.crossge.necessities.Economy.Formatter;
import com.crossge.necessities.GetUUID;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class JanetSlack {
    private static File configFile = new File("plugins/Necessities", "config.yml");
    private static HashMap<String, String> userMap = new HashMap<>();
    private static boolean justLoaded = true, isConnected = false;
    private static String token, channel, channelID, hook, latest;
    private static URL hookURL;
    private static BukkitRunnable historyReader;
    Variables var = new Variables();
    RankManager rm = new RankManager();
    UserManager um = new UserManager();
    Formatter form = new Formatter();
    BalChecks balc = new BalChecks();
    CmdHide hide = new CmdHide();
    GetUUID get = new GetUUID();

    public void init() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        token = config.contains("Necessities.SlackToken") ? config.getString("Necessities.SlackToken") : "token";
        channel = config.contains("Necessities.SlackChanel") ? config.getString("Necessities.SlackChanel") : "channel";
        channelID = config.contains("Necessities.ChannelID") ? config.getString("Necessities.ChannelID") : "channelID";
        hook = config.contains("Necessities.WebHook") ? config.getString("Necessities.WebHook") : "webHook";
        if (token.equals("token") || channel.equals("channel") || channelID.equals("channelID") || hook.equals("webHook"))
            return;
        try {
            hookURL = new URL(hook);
        } catch (Exception e) {
            return;
        }
        connect();
    }

    public void disable() {
        if (!isConnected)
            return;
        historyReader.cancel();
        userMap.clear();
        sendMessage("Disconnected.");
        sendPost("https://slack.com/api/users.setPresence?token=" + token + "&presence=away&pretty=1");
        isConnected = false;
    }

    public void sendMessage(String message) {
        sendViaHook(message);
        //sendPost("https://slack.com/api/chat.postMessage?token=" + token + "&channel=%23" + channel + "&text=" + ChatColor.stripColor(message.replaceAll(" ", "%20")) + "&as_user=true&pretty=1");
    }

    private void sendViaHook(String message) {
        message = ChatColor.stripColor(message);
        if (message.endsWith("\n"))
            message = message.substring(0, message.length() - 1);
        JSONObject json = new JSONObject();
        json.put("text", message);
        try {
            HttpsURLConnection con = (HttpsURLConnection) hookURL.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json;");
            con.setRequestProperty("Accept", "application/json,text/plain");
            con.setRequestMethod("POST");
            OutputStream os = con.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();
            con.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getHistory() {
        try {
            URL url = new URL("https://slack.com/api/channels.history?token=" + token + "&channel=" + channelID + "&oldest=" + latest + "&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject) jsonParser.parse(response.toString());
            JSONArray messages = (JSONArray) json.get("messages");
            for (int i = messages.size() - 1; i >= 0; i--) {
                JSONObject message = (JSONObject) messages.get(i);
                if (!message.containsKey("subtype") && !justLoaded) {
                    String name = getUserInfo((String) message.get("user"));
                    if (!name.contains("janet")) {
                        String text = (String) message.get("text");
                        while (text.contains("<") && text.contains(">"))
                            text = text.split("<@")[0] + "@" + getUserInfo(text.split("<@")[1].split(">:")[0]) + ":" + text.split("<@")[1].split(">:")[1];
                        sendSlackChat(name, text);
                    }
                }
                if (i == 0)
                    latest = (String) message.get("ts");
            }
        } catch (Exception e) { }
        justLoaded = false;
    }

    private String corTime(String time) {
        return time.length() == 1 ? "0" + time : time;
    }

    private void sendSlackChat(String name, String message) {
        if (message.startsWith("!")) {
            String m = "";
            if (message.startsWith("!help")) {
                m += "!help ~ View the different commands.\n";
                m += "!whois <name> ~ View information about <name>.\n";
                m += "!who ~ View the online players.\n";
                m += "!baltop <page> ~ View <page> of baltop.\n";
                m += "!bal <name> ~ View <name>'s balance.\n";
            } else if (message.startsWith("!whois ")) {
                if (message.split("!whois ").length == 1) {
                    sendMessage("Error: You must enter a player to view info of.");
                    return;
                }
                String target = message.split("!whois ")[1];
                UUID uuid = get.getID(target);
                if (uuid == null) {
                    uuid = get.getOfflineID(target);
                    if (uuid == null) {
                        sendMessage("Error: That player has not joined the server. If the player is offline, please use the full and most recent name.");
                        return;
                    }
                }
                User u = um.getUser(uuid);
                YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                m += "===== WhoIs: " + u.getName() + " =====\n";
                if (u.getPlayer() != null)
                    m += " - Nick: " + u.getPlayer().getDisplayName() + "\n";
                else {
                    if (u.getNick() == null)
                        m += " - Name: " + u.getName() + "\n";
                    else
                        m += " - Nick: " + u.getNick() + "\n";
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(Bukkit.getOfflinePlayer(uuid).getLastPlayed());
                    String second = Integer.toString(c.get(Calendar.SECOND));
                    String minute = Integer.toString(c.get(Calendar.MINUTE));
                    String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
                    String day = Integer.toString(c.get(Calendar.DATE));
                    String month = Integer.toString(c.get(Calendar.MONTH) + 1);
                    String year = Integer.toString(c.get(Calendar.YEAR));
                    String date = month + "/" + day + "/" + year;
                    hour = corTime(hour);
                    minute = corTime(minute);
                    second = corTime(second);
                    String time = hour + ":" + minute + " and " + second + " second";
                    if (Integer.parseInt(second) > 1)
                        time = hour + ":" + minute + " and " + second + " seconds";
                    m += " - Seen last on " + date + " at " + time + "\n";
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
                    m += " - Exp: " + form.addCommas(p.getTotalExperience()) + " (Level " + p.getLevel() + ")\n";
                    m += " - Location: (" + p.getWorld().getName() + ", " + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + ")\n";
                }
                if (config.contains("Necessities.Economy") && config.getBoolean("Necessities.Economy"))
                    m += " - Money: " + "$" + form.addCommas(balc.bal(uuid)) + "\n";
                if (u.getPlayer() != null) {
                    Player p = u.getPlayer();
                    m += " - IP Adress: " + p.getAddress().toString().split("/")[1].split(":")[0] + "\n";
                    String gamemode = "Survival";
                    if (p.getGameMode() == GameMode.ADVENTURE)
                        gamemode = "Adventure";
                    else if (p.getGameMode() == GameMode.CREATIVE)
                        gamemode = "Creative";
                    m += " - Gamemode: " + gamemode + "\n";
                }
                m += " - God mode: " + (u.godmode() ? "true" : "false") + "\n";
                if (u.getPlayer() != null) {
                    Player p = u.getPlayer();
                    m += " - Banned: " + (p.isBanned() ? "true" : "false") + "\n";
                    m += " - Visible: " + (hide.isHidden(p) ? "false" : "true") +"\n";
                } else {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(u.getUUID());
                    m += " - Banned: " + (p.isBanned() ? "true" : "false") + "\n";
                }
            } else if (message.startsWith("!who")) {
                int numbOnline = Bukkit.getOnlinePlayers().size() + 1;
                HashMap<Rank, String> online = new HashMap<>();
                if (!rm.getOrder().isEmpty())
                    online.put(rm.getRank(rm.getOrder().size() - 1), rm.getRank(rm.getOrder().size() - 1).getColor() + "Janet, ");
                if (!um.getUsers().isEmpty())
                    for (User u : um.getUsers().values())
                        if (u.isAfk() && hide.isHidden(u.getPlayer())) {
                            if (online.containsKey(u.getRank()))
                                online.put(u.getRank(), online.get(u.getRank()) + "[AFK][HIDDEN]" + u.getPlayer().getDisplayName() + ", ");
                            else
                                online.put(u.getRank(), "[AFK][HIDDEN]" + u.getPlayer().getDisplayName() + ", ");
                        } else if (u.isAfk()) {
                            if (online.containsKey(u.getRank()))
                                online.put(u.getRank(), online.get(u.getRank()) + "[AFK]" + u.getPlayer().getDisplayName() + ", ");
                            else
                                online.put(u.getRank(), "[AFK]" + u.getPlayer().getDisplayName() + ", ");
                        } else if (hide.isHidden(u.getPlayer())) {
                            if (online.containsKey(u.getRank()))
                                online.put(u.getRank(), online.get(u.getRank()) + "[HIDDEN]" + u.getPlayer().getDisplayName() + ", ");
                            else
                                online.put(u.getRank(), "[HIDDEN]" + u.getPlayer().getDisplayName() + ", ");
                        } else {
                            if (online.containsKey(u.getRank()))
                                online.put(u.getRank(), online.get(u.getRank()) + u.getPlayer().getDisplayName() + ", ");
                            else
                                online.put(u.getRank(), u.getPlayer().getDisplayName() + ", ");
                        }
                m += "There " + (numbOnline == 1 ? "is " : "are ") + numbOnline + " out of a maximum " + Bukkit.getMaxPlayers() + " players online.\n";
                for (int i = rm.getOrder().size() - 1; i >= 0; i--) {
                    Rank r = rm.getRank(i);
                    if (online.containsKey(r))
                        m += r.getName() + "s: " + online.get(r).trim().substring(0, online.get(r).length() - 2) + "\n";
                }
            } else if (message.startsWith("!baltop")) {
                int page = 0;
                if (message.split("!baltop ").length > 1) {
                    if (!form.isLegal(message.split("!baltop ")[1])) {
                        sendMessage("Error: You must enter a valid baltop page.");
                        return;
                    }
                    page = Integer.parseInt(message.split("!baltop ")[1]);
                }
                if (message.split("!baltop ").length == 0 || page == 0)
                    page = 1;
                int time = 0;
                String bal;
                int totalpages = balc.baltopPages();
                if (page > totalpages) {
                    sendMessage("Error: Input a number from 1 to " + Integer.toString(totalpages));
                    return;
                }
                m += "Balance Top Page [" + Integer.toString(page) + "/" + Integer.toString(totalpages) + "]\n";
                page = page - 1;
                bal = balc.balTop(page, time);
                while (bal != null) {
                    bal = ChatColor.GOLD + Integer.toString((page * 10) + time + 1) + ". " + var.getCatalog() +
                            bal.split(" ")[0] + " has: " + var.getMoney() + "$" + form.addCommas(bal.split(" ")[1]);
                    m += bal + "\n";
                    time++;
                    bal = balc.balTop(page, time);
                }
            } else if (message.startsWith("!bal ")) {
                if (message.split("!bal ").length == 1) {
                    sendMessage("Error: You must enter a player to view info of.");
                    return;
                }
                String target = message.split("!bal ")[1];
                String playersname;
                UUID uuid = get.getID(target);
                if (uuid == null) {
                    uuid = get.getOfflineID(target);
                    if (uuid == null) {
                        sendMessage("Error: That player has not joined the server. If the player is offline, please use the full and most recent name.");
                        return;
                    }
                    playersname = Bukkit.getOfflinePlayer(uuid).getName();
                } else
                    playersname = Bukkit.getPlayer(uuid).getName();
                String balance = balc.bal(uuid);
                if (balance == null) {
                    sendMessage("Error: That player is not in my records. If the player is offline, please use the full and most recent name.");
                    return;
                }
                m += ownerShip(playersname) + " balance is: " + "$" + form.addCommas(balance);
            } else {
                Bukkit.broadcast(var.getMessages() + "To Slack - " + ChatColor.WHITE + name + ": " + message, "Necessities.slack");
                return;
            }
            sendMessage(m);
        } else
            Bukkit.broadcast(var.getMessages() + "To Slack - " + ChatColor.WHITE + name + ": " + message, "Necessities.slack");
    }

    private String ownerShip(String name) {
        return (name.endsWith("s") || name.endsWith("S")) ? name + "'" : name + "'s";
    }

    private String getUserInfo(String id) {
        if (userMap.containsKey(id))
            return userMap.get(id);
        //Almost never should get past this point as it maps the users when it connects unless a new user gets invited
        String output = "";
        try {
            URL url = new URL("https://slack.com/api/users.info?token=" + token + "&user=" + id + "&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject) jsonParser.parse(response.toString());
            JSONObject user = (JSONObject) json.get("user");
            output = (String) user.get("name");
        } catch (Exception e) {}
        userMap.put(id, output);
        return output;
    }

    private void sendPost(String url) {
        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.getInputStream();
        } catch (Exception e) {}
    }

    private void connect() {
        if (isConnected)
            return;
        try {
            URL url = new URL("https://slack.com/api/rtm.start?token=" + token + "&simple_latest=true&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject) jsonParser.parse(response.toString());
            //Get a more recent timestamp than zero so first history pass is more efficient
            latest = (String) json.get("latest_event_ts");
            //Map users
            JSONArray users = (JSONArray) json.get("users");
            for (int i = users.size() - 1; i >= 0; i--) {
                JSONObject user = (JSONObject) users.get(i);
                String id = (String) user.get("id");
                if (!userMap.containsKey(id))
                    userMap.put(id, (String) user.get("name"));
            }
        } catch (Exception e) {}
        sendPost("https://slack.com/api/users.setPresence?token=" + token + "&presence=active&pretty=1");
        historyReader = new BukkitRunnable() {
            @Override
            public void run() {
                getHistory();
            }
        };
        historyReader.runTaskTimerAsynchronously(Necessities.getInstance(), 0, 20);
        sendMessage("Connected.");
        isConnected = true;
    }
}