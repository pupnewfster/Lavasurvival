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
    private static BukkitRunnable historyReader;
    private static String token, latest = "0";
    private Variables var = new Variables();

    public void init() {
        File configFile = new File("plugins/Necessities", "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        token = config.contains("Necessities.SlackToken") ? config.getString("Necessities.SlackToken") : "token";
        if (token.equals("token"))
            return;
        sendPost("https://slack.com/api/" + "rtm.start" + "?token=" + token + "&pretty=1");
        sendPost("https://slack.com/api/" + "users.setPresence" + "?token=" + token + "&presence=auto&pretty=1");
        historyReader = new BukkitRunnable() {
            @Override
            public void run() {
                getHistory();
            }
        };
        historyReader.runTaskTimerAsynchronously(Necessities.getInstance(), 0, 20);
        sendMessage("Connected.");
    }

    public void disable() {
        sendMessage("Disconnected.");
        historyReader.cancel();
        userMap.clear();
    }

    public void sendMessage(String message) {
        sendPost("https://slack.com/api/" + "chat.postMessage" + "?token=" + token + "&channel=%23ls&text=" + message.replaceAll(" ", "%20") + "&as_user=true&pretty=1");
    }

    public void getHistory() {
        boolean wasZero = latest.equals("0");
        try {
            URL obj = new URL("https://slack.com/api/" + "channels.history" + "?token=" + token + "&channel=C0HHYBTUZ&oldest=" + latest + "&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            con.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject) jsonParser.parse(response.toString());
            JSONArray messages = (JSONArray) json.get("messages");
            for (int i = messages.size() - 1; i >= 0; i--) {
                JSONObject message = (JSONObject) messages.get(i);
                if (!message.containsKey("subtype") && !wasZero) {
                    String name = getUserInfo((String) message.get("user"));
                    if (!name.contains("janet")) {
                        String text = (String) message.get("text");
                        while (text.contains("<") && text.contains(">"))
                            text = text.split("<@")[0] + "@" + getUserInfo(text.split("<@")[1].split(">:")[0]) + ":" + text.split("<@")[1].split(">:")[1];
                        sendOps(name + ": " + text);
                    }
                }
                if (i == 0)
                    latest = (String) message.get("ts");
            }
        } catch (Exception e) { }
    }

    private void sendOps(String message) {
        Bukkit.broadcast(var.getMessages() + "To Ops - " + ChatColor.WHITE + message, "Necessities.opBroadcast");
    }

    private String getUserInfo(String id) {
        if (userMap.containsKey(id))
            return userMap.get(id);
        String output = "";
        try {
            URL obj = new URL("https://slack.com/api/" + "users.info" + "?token=" + token + "&user=" + id + "&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            con.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

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

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
        } catch (Exception e) {}
    }
}