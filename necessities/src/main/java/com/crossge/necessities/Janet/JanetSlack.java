package com.crossge.necessities.Janet;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

public class JanetSlack {
    private static HashMap<String, String> userMap = new HashMap<>();
    private static boolean justLoaded = true, isConnected = false;
    private static String token, channel, channelID, latest;
    private static BukkitRunnable historyReader;
    private Variables var = new Variables();

    public void init() {
        File configFile = new File("plugins/Necessities", "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        token = config.contains("Necessities.SlackToken") ? config.getString("Necessities.SlackToken") : "token";
        channel = config.contains("Necessities.SlackChanel") ? config.getString("Necessities.SlackChanel") : "channel";
        channelID = config.contains("Necessities.ChannelID") ? config.getString("Necessities.ChannelID") : "channelID";
        if (token.equals("token") || channel.equals("channel") || channelID.equals("channelID"))
            return;
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
        sendPost("https://slack.com/api/chat.postMessage?token=" + token + "&channel=%23" + channel + "&text=" + message.replaceAll(" ", "%20") + "&as_user=true&pretty=1");
    }

    public void getHistory() {
        try {
            URL obj = new URL("https://slack.com/api/channels.history?token=" + token + "&channel=" + channelID + "&oldest=" + latest + "&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
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
                        sendSlackChat(name + ": " + text);
                    }
                }
                if (i == 0)
                    latest = (String) message.get("ts");
            }
        } catch (Exception e) { }
        justLoaded = false;
    }

    private void sendSlackChat(String message) {
        Bukkit.broadcast(var.getMessages() + "To Slack - " + ChatColor.WHITE + message, "Necessities.slack");
    }

    private String getUserInfo(String id) {
        if (userMap.containsKey(id))
            return userMap.get(id);
        //Almost never should get past this point as it maps the users when it connects unless a new user gets invited
        String output = "";
        try {
            URL obj = new URL("https://slack.com/api/users.info?token=" + token + "&user=" + id + "&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
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
            URL obj = new URL("https://slack.com/api/rtm.start?token=" + token + "&simple_latest=true&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
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