package com.crossge.necessities.Janet;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Calendar;

@SuppressWarnings("unused")
public class JanetAI {//TODO: Upgrade
    private static String JanetName = "";
    private JanetSlack slack;
    private JanetNet net;
    private Variables var;
    //private GetUUID get;

    public void parseMessage(String name, String message, Source s, boolean isPM, JanetSlack.SlackUser user) {
        message = ChatColor.stripColor(message);
        String result = JanetName + net.bestGuess(message);
        /*UUID uuid = get.getID(name); //TODO see about making this async instead of a sync delayed task
        Player p = null;
        if (uuid != null)
            p = Bukkit.getPlayer(uuid);
        message = ChatColor.stripColor(message);
        String result = null;
        if (message.toLowerCase().startsWith("!") && s.equals(Source.Server) && p != null && p.hasPermission("Necessities.janetai")) {
            if (message.toLowerCase().startsWith("!meme ") || message.toLowerCase().startsWith("!memes ") || message.toLowerCase().startsWith("!memenumber ")) {
                int applePie = 0;
                try {
                    applePie = Integer.parseInt(message.split(" ")[1]);
                } catch (Exception ignored) {
                }
                result = JanetName + r.memeRandom(applePie);
            } else if (message.toLowerCase().startsWith("!say "))
                result = JanetName + message.replaceFirst("!say ", "");
            else if (message.toLowerCase().startsWith("!slack "))
                slack.sendMessage(name + ": " + message.replaceFirst("!slack ", ""));
        } else if (message.toLowerCase().contains("what time is it") || message.toLowerCase().contains("what is the time"))
            result = JanetName + "The time is " + time();
        else if (message.toLowerCase().contains("what day is it") || message.toLowerCase().contains("what is the date") ||
                message.toLowerCase().contains("whats the date") || message.toLowerCase().contains("what's the date"))
            result = JanetName + "The date is: " + date();
        else if (message.toLowerCase().contains("can i be op") || message.toLowerCase().contains("may i be op") ||
                message.toLowerCase().contains("can i have op") || message.toLowerCase().contains("may i have op") ||
                message.toLowerCase().contains("can i get op") || message.toLowerCase().contains("may i get op") ||
                message.toLowerCase().contains("can i be admin") || message.toLowerCase().contains("may i be admin") ||
                message.toLowerCase().contains("can i get admin") || message.toLowerCase().contains("may i get admin") ||
                message.toLowerCase().contains("can i have admin") || message.toLowerCase().contains("may i have admin") ||
                message.toLowerCase().contains("can i be mod") || message.toLowerCase().contains("may i be mod") ||
                message.toLowerCase().contains("can i get mod") || message.toLowerCase().contains("may i get mod") ||
                message.toLowerCase().contains("mod me") || message.toLowerCase().contains("op me") ||
                message.toLowerCase().contains("admin me") || message.toLowerCase().contains("make me mod") ||
                message.toLowerCase().contains("make me admin") || message.toLowerCase().contains("make me op") ||
                message.toLowerCase().contains("promote me"))
            result = JanetName + "You may only earn the rank, no free promotions";
        else if (message.toLowerCase().contains("janet")) {
            if (message.toLowerCase().contains("how are you") || message.toLowerCase().contains("what is up") ||
                    message.toLowerCase().contains("sup") || message.toLowerCase().contains("whats up") ||
                    message.toLowerCase().contains("how was your day"))
                result = JanetName + feelingMessages[r.memeRandom(feelingMessages.length)];
            else if (message.toLowerCase().startsWith("hello") || message.toLowerCase().startsWith("hey") ||
                    message.toLowerCase().startsWith("hi") || message.toLowerCase().startsWith("hai"))
                result = JanetName + heyMessages.get(r.memeRandom(heyMessages.size()));
            else if (message.toLowerCase().contains("i love you") || message.toLowerCase().contains("do you love me") ||
                    message.toLowerCase().contains("i wub you") || message.toLowerCase().contains("do you wub me") ||
                    message.toLowerCase().contains("love me")) {
                if (Necessities.getInstance().isDev(name))
                    result = JanetName + "I love you " + name + ".";
                else
                    result = JanetName + "Well I can give you a hug... but I am rejecting your love.";
            } else if (message.toLowerCase().contains("can i have a hug") || message.toLowerCase().contains("can you give me a hug") ||
                    message.toLowerCase().contains("can you hug me") || message.toLowerCase().contains("hug me") ||
                    message.toLowerCase().contains("give me a hug") || message.toLowerCase().contains("gimme a hug") ||
                    message.toLowerCase().contains("hug me") || message.toLowerCase().contains("i demand a hug") ||
                    message.toLowerCase().contains("can you gimme a hug")) {
                if (Necessities.getInstance().isDev(name))
                    result = JanetName + "Yey *hugs " + name + " while kissing them on the cheek*.";
                else
                    result = JanetName + "Sure *hugs " + name + "*.";
            } else if (message.toLowerCase().contains("can i have a kiss") || message.toLowerCase().contains("can you give me a kiss") ||
                    message.toLowerCase().contains("can you kiss me") || message.toLowerCase().contains("kiss me") ||
                    message.toLowerCase().contains("give me a kiss") || message.toLowerCase().contains("gimme a kiss") ||
                    message.toLowerCase().contains("can you gimme a kiss")) {
                if (Necessities.getInstance().isDev(name))
                    result = JanetName + "Ok, *kisses " + name + "*.";
                else
                    result = JanetName + "No, *slaps " + name + "*.";
            } else if (message.toLowerCase().contains("i see you") || message.toLowerCase().contains("i am following you"))
                result = JanetName + stalkerMessages[r.memeRandom(stalkerMessages.length)];
            else if (message.toLowerCase().contains("your drunk") || message.toLowerCase().contains("you are drunk") ||
                    message.toLowerCase().contains("you're drunk") || message.toLowerCase().contains("is drunk"))
                result = JanetName + drunkMessages[r.memeRandom(drunkMessages.length)];
            else if (message.toLowerCase().contains("tilt"))
                result = JanetName + tiltMessages[r.memeRandom(tiltMessages.length)];
            else
                result = JanetName + janetNamed[r.memeRandom(janetNamed.length)];
        }*/
        //if (result != null)
        sendMessage(result, s, isPM, user);
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

    private String corTime(String time) {
        return time.length() == 1 ? "0" + time : time;
    }

    private String dayOfWeek(int day) {
        if (day == Calendar.MONDAY)
            return "Monday";
        else if (day == Calendar.TUESDAY)
            return "Tuesday";
        else if (day == Calendar.WEDNESDAY)
            return "Wednesday";
        else if (day == Calendar.THURSDAY)
            return "Thursday";
        else if (day == Calendar.FRIDAY)
            return "Friday";
        else if (day == Calendar.SATURDAY)
            return "Saturday";
        return "Sunday";//else if(day == Calendar.SUNDAY) other days have been checked already
    }

    private String time() {
        Calendar c = Calendar.getInstance();
        String minute = corTime(Integer.toString(c.get(Calendar.MINUTE)));
        String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        String time = "AM";
        if (Integer.parseInt(hour) > 12) {
            time = "PM";
            hour = Integer.toString(Integer.parseInt(hour) - 12);
        }
        return hour + ":" + minute + " " + time;
    }

    private String date() {
        Calendar c = Calendar.getInstance();
        String day = Integer.toString(c.get(Calendar.DATE));
        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        String year = Integer.toString(c.get(Calendar.YEAR));
        return dayOfWeek(c.get(Calendar.DAY_OF_WEEK)) + " " + month + "/" + day + "/" + year;
    }

    public enum Source {
        Server,
        Slack
    }
}