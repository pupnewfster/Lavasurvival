package com.crossge.necessities.Janet;

import info.debatty.java.stringsimilarity.KShingling;
import info.debatty.java.stringsimilarity.MetricLCS;
import info.debatty.java.stringsimilarity.StringProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unused")
public class JanetNet {
    private final KShingling ks = new KShingling(2);
    private final MetricLCS l = new MetricLCS();
    private final HashMap<StringProfile, String> profiles = new HashMap<>();
    private final HashSet<String> words = new HashSet<>();

    public JanetNet() {
        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/englishWords.txt")));
            String line;
            while ((line = read.readLine()) != null) {
                String lline = line.toLowerCase();
                this.profiles.put(this.ks.getProfile(lline), lline);
                this.words.add(lline);
            }
            read.close();
        } catch (Exception ignored) {
        }
    }

    public void readCustom() {
        try (BufferedReader read = new BufferedReader(new FileReader(new File("plugins/Necessities/customWords.txt")))) {
            String line;
            while ((line = read.readLine()) != null) {
                String lline = line.toLowerCase();
                if (this.words.contains(lline))
                    continue;
                this.profiles.put(this.ks.getProfile(lline), lline);
                this.words.add(lline);
            }
        } catch (Exception ignored) {
        }
    }

    String bestGuess(String message) {
        String result = "";
        for (String m : message.split(" ")) {
            if (m.length() < 2) {
                result += m;
                continue;
            }
            WordScore ws = getBestWordScore(m);
            if (isNick(m, ws))
                result += m + " ";
            else if (ws.getWord() == null) {
                result += m + " "; //TODO possibly figure out it should somehow be noted that it wasn't found
            } else
                result += ws.getWord() + " ";
        }
        return result.trim();
    }

    private boolean isNick(String name, WordScore ws) { //Best match if it is not a nickname
        StringProfile np = this.ks.getProfile(name);
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

    private double similarity(String word, StringProfile profile, String other) {
        return similarity(profile, this.ks.getProfile(other), word, other);
    }

    public double similarity(String word, String other) {
        return similarity(this.ks.getProfile(word), this.ks.getProfile(other), word, other);
    }

    private double similarity(StringProfile profile, StringProfile other, String word, String oword) {
        double score;
        try {
            score = profile.cosineSimilarity(other) - this.l.distance(word, oword);
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
        StringProfile cprofile = this.ks.getProfile(word);
        double best = 0.0;
        String bestWord = null;
        for (StringProfile profile : this.profiles.keySet()) {
            String curWord = this.profiles.get(profile);
            double cur = similarity(profile, cprofile, curWord, word);
            if (cur > best) {
                best = cur;
                bestWord = curWord;
            }
        }
        return new WordScore(best, bestWord);
    }

    private class WordScore {
        private double score = 0;
        private String word = null;

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