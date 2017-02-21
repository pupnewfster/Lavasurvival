package com.crossge.necessities.Janet;

import info.debatty.java.stringsimilarity.Cosine;
import info.debatty.java.stringsimilarity.MetricLCS;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class JanetNet {
    private final Cosine cosine = new Cosine(2);
    private final MetricLCS l = new MetricLCS();
    private final HashMap<Map<String, Integer>, String> profiles = new HashMap<>();
    private final HashSet<String> words = new HashSet<>();

    public JanetNet() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Initializing JanetNet...");
        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/englishWords.txt")));
            String line;
            while ((line = read.readLine()) != null) {
                String lline = line.toLowerCase();
                this.profiles.put(this.cosine.getProfile(lline), lline);
                this.words.add(lline);
            }
            read.close();
        } catch (Exception ignored) {
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "JanetNet initialized...");
    }

    public void readCustom() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Adding custom words to JanetNet.");
        try (BufferedReader read = new BufferedReader(new FileReader(new File("plugins/Necessities/customWords.txt")))) {
            String line;
            while ((line = read.readLine()) != null) {
                String lline = line.toLowerCase();
                if (this.words.contains(lline))
                    continue;
                this.profiles.put(this.cosine.getProfile(lline), lline);
                this.words.add(lline);
            }
        } catch (Exception ignored) {
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Custom words added.");
    }

    String bestGuess(String message) {
        StringBuilder result = new StringBuilder();
        for (String m : message.split(" ")) {
            if (m.length() < 2) {
                result.append(m);
                continue;
            }
            WordScore ws = getBestWordScore(m);
            if (isNick(m, ws))
                result.append(m).append(" ");
            else if (ws.getWord() == null) {
                result.append(m).append(" "); //TODO possibly figure out it should somehow be noted that it wasn't found
            } else
                result.append(ws.getWord()).append(" ");
        }
        return result.toString().trim();
    }

    private boolean isNick(String name, WordScore ws) { //Best match if it is not a nickname
        Map<String, Integer> np = this.cosine.getProfile(name);
        double best = 0.0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            String cname = ChatColor.stripColor(p.getDisplayName().replaceFirst("~", "")), pname = p.getName();
            double cn = similarity(name, np, cname), pn = similarity(name, np, pname);
            if (cn > best)
                best = cn;
            if (pn > best)
                best = pn;
        }
        return best > ws.getScore() * 0.75;
    }

    private double similarity(String word, Map<String, Integer> profile, String other) {
        return similarity(profile, this.cosine.getProfile(other), word, other);
    }

    private double similarity(String word, String other) {
        return similarity(this.cosine.getProfile(word), this.cosine.getProfile(other), word, other);
    }

    private double similarity(Map<String, Integer> profile, Map<String, Integer> other, String word, String oword) {
        double score;
        try {
            score = this.cosine.similarity(profile, other) - this.l.distance(word, oword);
            //score = profile.cosineSimilarity(other) - this.l.distance(word, oword);
            //TODO Compare performance with potentially improved accuracy of below way
            //heallo checking
            //       hello    heal
            //above  94359    11077
            //below  216615   43077
            //NGram l = new NGram(2);
            //score = profile.cosineSimilarity(other) * this.l.distance("|" + word + "|", "|" + oword + "|");
        } catch (Exception e) {
            return 0.0;
        }
        return score;
    }

    private String getBestWord(String word) {
        return getBestWordScore(word).getWord();
    }

    private WordScore getBestWordScore(String word) {
        word = word.toLowerCase();
        if (this.words.contains(word))
            return new WordScore(1.0, word);
        Map<String, Integer> cprofile = this.cosine.getProfile(word);
        double best = 0.0;
        String bestWord = null;
        for (Map.Entry<Map<String, Integer>, String> entry : this.profiles.entrySet()) {
            String curWord = this.profiles.get(entry.getKey());
            double cur = similarity(entry.getKey(), cprofile, curWord, word);
            if (cur > best) {
                best = cur;
                bestWord = curWord;
            }
        }
        return new WordScore(best, bestWord);
    }

    private class WordScore {
        private double score;
        private String word;

        private WordScore(double score, String word) {
            this.score = score;
            this.word = word;
        }

        private String getWord() {
            return this.word;
        }

        private double getScore() {
            return this.score;
        }
    }
}