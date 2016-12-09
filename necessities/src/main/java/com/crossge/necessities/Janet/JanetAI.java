package com.crossge.necessities.Janet;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class JanetAI {//TODO: Move to JanetNet and add understanding logic
    private String janetName = "";

    public void parseMessage(String message, Source s, boolean isPM, JanetSlack.SlackUser user) {
        message = ChatColor.stripColor(message);
        if (s.equals(Source.Slack) && message.contains("Janet")) {
            String result = this.janetName + Necessities.getNet().bestGuess(message);
            sendMessage(result, s, isPM, user);
        }
    }

    private void sendMessage(String message, Source s, boolean isPM, JanetSlack.SlackUser user) {
        if (s.equals(Source.Server))
            Bukkit.broadcastMessage(message);
        else if (s.equals(Source.Slack)) {
            if (!isPM)
                Bukkit.broadcast(Necessities.getVar().getMessages() + "To Slack - " + ChatColor.WHITE + message, "Necessities.slack");
            Necessities.getSlack().sendMessage(ChatColor.stripColor(message), isPM, user);
        }
    }

    public void initiate() {
        RankManager rm = Necessities.getRM();
        this.janetName = (!rm.getOrder().isEmpty() ? ChatColor.translateAlternateColorCodes('&', rm.getRank(rm.getOrder().size() - 1).getTitle() + " ") : "") + "Janet" + ChatColor.DARK_RED + ": " + ChatColor.WHITE;
    }

    public enum Source {
        Server,
        Slack
    }
}