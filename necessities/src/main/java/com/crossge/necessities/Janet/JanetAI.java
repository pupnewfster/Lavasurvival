package com.crossge.necessities.Janet;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class JanetAI {//TODO: Move to JanetNet and add understanding logic
    private static String JanetName = "";
    private JanetSlack slack;
    private JanetNet net;
    private Variables var;

    public void parseMessage(String name, String message, Source s, boolean isPM, JanetSlack.SlackUser user) {
        message = ChatColor.stripColor(message);
        if (s.equals(Source.Slack) && message.contains("Janet")) {
            String result = JanetName + net.bestGuess(message);
            sendMessage(result, s, isPM, user);
        }
    }

    private void sendMessage(String message, Source s, boolean isPM, JanetSlack.SlackUser user) {
        if (s.equals(Source.Server))
            Bukkit.broadcastMessage(message);
        else if (s.equals(Source.Slack)) {
            if (!isPM)
                Bukkit.broadcast(var.getMessages() + "To Slack - " + ChatColor.WHITE + message, "Necessities.slack");
            this.slack.sendMessage(ChatColor.stripColor(message), isPM, user);
        }
    }

    public void initiate() {
        RankManager rm = Necessities.getInstance().getRM();
        JanetName = (!rm.getOrder().isEmpty() ? ChatColor.translateAlternateColorCodes('&', rm.getRank(rm.getOrder().size() - 1).getTitle() + " ") : "") + "Janet" + ChatColor.DARK_RED + ": " + ChatColor.WHITE;
        this.slack = Necessities.getInstance().getSlack();
        this.var = Necessities.getInstance().getVar();
        //this.get = Necessities.getInstance().getUUID();
        this.net = Necessities.getInstance().getNet();
    }

    public enum Source {
        Server,
        Slack
    }
}