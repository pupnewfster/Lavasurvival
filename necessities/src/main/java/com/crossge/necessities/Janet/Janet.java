package com.crossge.necessities.Janet;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Janet {//TODO: Make the logic run async for performance reasons
    private final ArrayList<String> badwords = new ArrayList<>();
    private final ArrayList<String> goodwords = new ArrayList<>();
    private final ArrayList<String> ips = new ArrayList<>();
    private final HashMap<UUID, Long[]> lastChat = new HashMap<>();
    private final HashMap<UUID, Long[]> lastCmd = new HashMap<>();

    public void initiate() {//now has its own function instead of reading them all every time Janet was re-initiated
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Janet initiating...");
        File customConfigFileCensors = new File(Necessities.getInstance().getDataFolder(), "censors.yml");
        YamlConfiguration customConfigCensors = YamlConfiguration.loadConfiguration(customConfigFileCensors);
        this.badwords.addAll(customConfigCensors.getStringList("badwords").stream().filter(word -> !word.equals("")).map(String::toUpperCase).collect(Collectors.toList()));
        this.goodwords.addAll(customConfigCensors.getStringList("goodwords").stream().filter(word -> !word.equals("")).map(String::toUpperCase).collect(Collectors.toList()));
        this.ips.addAll(customConfigCensors.getStringList("ips").stream().filter(ip -> !ip.equals("")).collect(Collectors.toList()));
        RankManager rm = Necessities.getRM();
        String rank = "";
        if (!rm.getOrder().isEmpty())
            rank = rm.getRank(rm.getOrder().size() - 1).getTitle() + " ";
        final String login = ChatColor.translateAlternateColorCodes('&', "&a + " + rank + "Janet&e joined the game.");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> Bukkit.broadcastMessage(login));
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Janet initiated.");
    }

    public void unload() {//possibly empty the lists not sure if needed though
        RankManager rm = Necessities.getRM();
        String rank = "";
        if (!rm.getOrder().isEmpty())
            rank = rm.getRank(rm.getOrder().size() - 1).getTitle() + " ";
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c - " + rank + "Janet&e Disconnected."));
    }

    private void removePlayer(UUID uuid) {//called when player disconnects
        Necessities.getWarns().removePlayer(uuid);
        this.lastChat.remove(uuid);
        this.lastCmd.remove(uuid);
    }

    private boolean checkChatSpam(UUID uuid) {
        Long time = System.currentTimeMillis();
        if (!this.lastChat.containsKey(uuid)) {
            Long[] t = new Long[2];
            t[0] = time;
            this.lastChat.put(uuid, t);
            return false;
        }
        if (!isFull(this.lastChat.get(uuid))) {
            putProp(this.lastChat.get(uuid), time);
            return false;
        }
        Long FirstTime = this.lastChat.get(uuid)[0];
        double chatSpam = 1.5;
        if ((time - FirstTime) / 1000.0 > chatSpam) {
            putProp(this.lastChat.get(uuid), time);
            return false;
        }
        putProp(this.lastChat.get(uuid), time);
        delayedWarn(uuid, "ChatSpam");
        return true;
    }

    private boolean checkCmdSpam(UUID uuid) {
        Long time = System.currentTimeMillis();
        if (!this.lastCmd.containsKey(uuid)) {
            Long[] t = new Long[2];
            t[0] = time;
            this.lastCmd.put(uuid, t);
            return false;
        }
        if (!isFull(this.lastCmd.get(uuid))) {
            putProp(this.lastCmd.get(uuid), time);
            return false;
        }
        Long FirstTime = this.lastCmd.get(uuid)[0];
        double cmdSpam = 1.5;
        if ((time - FirstTime) / 1000.0 > cmdSpam) {
            putProp(this.lastCmd.get(uuid), time);
            return false;
        }
        putProp(this.lastCmd.get(uuid), time);
        delayedWarn(uuid, "CmdSpam");
        return true;
    }

    private void putProp(Long[] l, Long toPut) {
        if (l[1] != null)
            l[0] = l[1];
        l[1] = toPut;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isFull(Long[] l) {
        return !(l[0] == null || l[1] == null);
    }

    private String caps(String message) {
        return internalCaps(message) ? message.toLowerCase() : message;
    }

    private boolean internalCaps(String message) {
        String orig = message.replaceAll("[^A-Z]", "");
        int s = orig.length();
        message = message.replaceAll("[^a-zA-Z]", "");
        int f = message.length();
        return f * 3.0 / 5 <= s && f > 5;
    }

    private String langCheck(UUID uuid, String message, boolean warn) {
        String censored = internalLang(message);
        if (censored.equals(message))
            return message;
        if (warn)
            delayedWarn(uuid, "Language");
        return censored;
    }

    private String internalLang(String message) {
        String[] orig = message.replaceAll("[^a-zA-Z ]", "").toUpperCase().split(" ");
        ArrayList<String> bad = new ArrayList<>();
        for (String badWord : this.badwords) {
            ArrayList<String> s = removeSpaces(orig, badWord);
            bad.addAll(s.stream().filter(w -> w.contains(badWord) && check(w, badWord) && !isGood(w)).collect(Collectors.toList()));
            for (String o : orig) {
                String t = removeConsec(o);
                if ((o.contains(badWord) && check(o, badWord) && !isGood(o)) || (t.contains(badWord) && check(t, badWord) && !isGood(t)))
                    bad.add(o);
            }
        }
        if (bad.isEmpty())
            return message;
        String[] nonCapitalized = message.split(" ");
        StringBuilder censoredBuilder = new StringBuilder();
        for (int i = 0; i < nonCapitalized.length; i++) {
            for (String word : bad)
                if (nonCapitalized[i].replaceAll("[^a-zA-Z]", "").equalsIgnoreCase(word))
                    nonCapitalized[i] = stars(nonCapitalized[i]);
            censoredBuilder.append(nonCapitalized[i]).append(" ");
        }
        String censored = censoredBuilder.toString();
        if (censored.equals(""))
            censored = message;
        return addSpaces(bad, censored);
    }

    private boolean check(String msg, String bad) {
        return msg.length() * bad.length() / (bad.length() + 1.0) - bad.length() > 0.75 || msg.length() * 4.0 / 5 <= bad.length() || msg.replaceAll(bad, "").length() == 0 ||
                msg.replaceAll(bad, "").length() >= msg.length() * 3.0 / 5;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isGood(String msg) {
        return this.goodwords.stream().anyMatch(msg::startsWith);
    }

    private String addSpaces(ArrayList<String> bad, String orig) {
        String censored;
        String temp = orig.toUpperCase().replaceAll("[^a-zA-Z]", "");
        String t = removeConsec(temp);
        HashMap<Integer, Character> stars = new HashMap<>();
        String s = t;
        for (String b : bad) {
            temp = temp.replaceAll(b, stars(b));
            s = s.replaceAll(b, stars(b));
        }
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == '*')
                stars.put(i, t.charAt(i));
        StringBuilder c = new StringBuilder();
        int noSpace = 0;
        for (int i = 0; i < temp.length(); i++) {
            c.append((stars.containsKey(noSpace) && stars.get(noSpace) == temp.charAt(i)) ? "*" : temp.charAt(i));
            if (i + 1 < temp.length() && stars.containsKey(noSpace) && stars.get(noSpace) != temp.charAt(i + 1))
                noSpace++;
        }
        temp = c.toString();
        int loc = 0;
        StringBuilder censoredBuilder = new StringBuilder();
        for (int i = 0; i < orig.length(); i++) {
            if (loc < temp.length() && temp.charAt(loc) == '*')
                censoredBuilder.append(orig.charAt(i) == ' ' ? " " : "*");
            else
                censoredBuilder.append(orig.charAt(i));
            if (Character.isLetter(orig.charAt(i)))
                loc++;
        }
        censored = censoredBuilder.toString();
        return censored.equals("") ? orig : censored;
    }

    private ArrayList<String> removeSpaces(String[] msgs, String word) {
        ArrayList<String> messages = new ArrayList<>();
        String temp = "";
        String t1 = "";
        for (int i = 0; i < msgs.length; i++) {
            for (int j = i; j < msgs.length; j++) {
                if (temp.length() < word.length()) {
                    temp += msgs[j];
                    if (!messages.contains(temp))
                        if (word.length() > 3)
                            messages.add(temp);
                        else if (temp.length() <= word.length())
                            messages.add(temp);
                }
                if (t1.length() < word.length()) {
                    t1 = removeConsec(t1 + msgs[j]);
                    if (!messages.contains(t1))
                        messages.add(t1);
                }
                if (t1.length() >= word.length() && temp.length() >= word.length())
                    break;
            }
            temp = "";
            t1 = "";
        }
        return messages;
    }

    private String removeConsec(String message) {
        if (message.equals(""))
            return "";
        return IntStream.range(1, message.length()).filter(i -> message.charAt(i) != message.charAt(i - 1)).mapToObj(i -> String.valueOf(message.charAt(i))).collect(Collectors.joining("", "" + message.charAt(0), ""));
    }

    private String stars(String toStar) {
        String[] split = toStar.split(" ");
        StringBuilder star = new StringBuilder();
        for (String s : split)
            star.append(starNoSpaces(s)).append(" ");
        return star.toString().trim();
    }

    private String starNoSpaces(String toStar) {
        StringBuilder star = new StringBuilder();
        for (int i = 0; i < toStar.trim().length(); i++)
            star.append("*");
        return star.toString();
    }

    private String starIP(String toStar) {
        String port = "";
        if (toStar.contains(":")) {
            port = stars(toStar.split(":")[1]);
            toStar = toStar.substring(0, toStar.length() - 2 - port.length());
        }
        String[] ipPieces = toStar.trim().split("\\.");
        StringBuilder starBuilder = new StringBuilder();
        for (String i : ipPieces)
            starBuilder.append(stars(i)).append(".");
        String star = starBuilder.toString();
        star = star.substring(0, star.length() - 1);
        return !port.equals("") ? star + ":" + port : star;
    }

    private String adds(UUID uuid, String message, boolean warn) {
        String censored = internalAdds(message);
        if (censored.equals(message))
            return message;
        if (warn)
            delayedWarn(uuid, "Adds");
        return censored;
    }

    private String internalAdds(String message) {
        String[] orig = message.split(" ");
        String temp;
        for (int i = 0; i < orig.length; i++) {
            if (orig[i].split(":").length == 0)
                continue;
            temp = orig[i].split(":")[0];
            if (!whitelistedIP(temp)) {
                if (validateIPAddress(temp))
                    orig[i] = starIP(orig[i]);
                else if (!temp.contains("http://") && (temp.split("\\.").length == 3 || temp.split("\\.").length == 3))
                    try {
                        URLConnection urlCon = new URL("http://" + temp).openConnection();
                        urlCon.connect();
                        InputStream is = urlCon.getInputStream();
                        String u = urlCon.getURL().toString().replaceFirst("http://", "");
                        is.close();
                        if (validateIPAddress(u))
                            orig[i] = starIP(orig[i]);
                    } catch (Exception ignored) {
                    }
            }
        }
        StringBuilder censored = new StringBuilder();
        for (String word : orig)
            censored.append(word).append(" ");
        return censored.toString().trim();
    }

    private boolean whitelistedIP(String ip) {
        return ips.contains(ip.trim()) || Bukkit.getIp().equals(ip);
    }

    private boolean validateIPAddress(String ipAddress) {
        try {
            Pattern ipAdd = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
            return ipAdd.matcher(ipAddress).matches();
        } catch (Exception ignored) {
        }
        return false;
    }

    public String logChat(UUID uuid, String message) {
        Player p = Bukkit.getPlayer(uuid);
        YamlConfiguration config = Necessities.getInstance().getConfig();
        if (config.contains("Necessities.log") && config.getBoolean("Necessities.log"))
            Necessities.getLog().log(p.getName() + ": " + message);
        boolean warn = true;
        String censored = message;
        if (config.getBoolean("Necessities.chatSpam") && !p.hasPermission("Necessities.spamchat"))
            warn = !checkChatSpam(uuid);
        if (config.getBoolean("Necessities.language") && !p.hasPermission("Necessities.language")) {
            censored = langCheck(uuid, censored, warn);
            warn = message.equals(censored);
        }
        if (config.getBoolean("Necessities.advertise") && !p.hasPermission("Necessities.advertise"))
            censored = adds(uuid, censored, warn);
        if (config.getBoolean("Necessities.caps") && !p.hasPermission("Necessities.caps"))
            censored = caps(censored);
        return censored;
    }

    public String logCom(UUID uuid, String message) {
        Player p = Bukkit.getPlayer(uuid);
        String messageOrig = message;
        YamlConfiguration config = Necessities.getInstance().getConfig();
        if (config.contains("Necessities.log") && config.getBoolean("Necessities.log"))
            Necessities.getLog().log(p.getName() + " issued server command: " + message);
        boolean warn = false;
        String censored = message.replaceFirst(message.split(" ")[0], "").trim();
        message = message.replaceFirst(message.split(" ")[0], "").trim();
        if (censored.equals(""))
            return messageOrig;
        if (config.getBoolean("Necessities.cmdSpam") && !p.hasPermission("Necessities.spamcommands"))
            warn = !checkCmdSpam(uuid);
        if (config.getBoolean("Necessities.advertise") && !p.hasPermission("Necessities.advertise")) {
            censored = adds(uuid, censored, warn);
            warn = message.equals(censored);
        }
        if (config.getBoolean("Necessities.language") && !p.hasPermission("Necessities.language"))
            censored = langCheck(uuid, censored, warn);
        if (config.getBoolean("Necessities.caps") && !p.hasPermission("Necessities.caps"))
            censored = caps(censored);
        return messageOrig.split(" ")[0] + " " + censored;
    }

    public void logConsole(String message) {
        YamlConfiguration config = Necessities.getInstance().getConfig();
        if (config.contains("Necessities.log") && config.getBoolean("Necessities.log"))
            Necessities.getLog().log(message.startsWith("say") ? "Console:" + message.replaceFirst("say", "") : "Console issued command: " + message);
    }

    public void logIn(UUID uuid) {
        YamlConfiguration config = Necessities.getInstance().getConfig();
        if (config.contains("Necessities.log") && config.getBoolean("Necessities.log"))
            Necessities.getLog().log(" + " + playerInfo(Bukkit.getPlayer(uuid)) + " joined the game.");
    }

    public void logDeath(UUID uuid, String cause) {
        YamlConfiguration config = Necessities.getInstance().getConfig();
        if (config.contains("Necessities.log") && config.getBoolean("Necessities.log"))
            Necessities.getLog().log(playerInfo(Bukkit.getPlayer(uuid)) + " " + cause);
    }

    public void logOut(UUID uuid) {
        removePlayer(uuid);
        YamlConfiguration config = Necessities.getInstance().getConfig();
        if (config.contains("Necessities.log") && config.getBoolean("Necessities.log"))
            Necessities.getLog().log(" - " + playerInfo(Bukkit.getPlayer(uuid)) + " Disconnected.");
    }

    private String playerInfo(Player p) {
        return p.getName() + " (" + p.getAddress().toString().split("/")[1].split(":")[0] + " [" + p.getWorld().getName() + " " + p.getLocation().getBlockX() + "," + p.getLocation().getBlockY() + "," +
                p.getLocation().getBlockZ() + "])";
    }

    private void delayedWarn(final UUID uuid, final String reason) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Necessities.getInstance(), () -> Necessities.getWarns().warn(uuid, reason, "Janet"));
    }
}