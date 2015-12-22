package com.crossge.necessities;

import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class ScoreBoards {
    private static ScoreboardManager man;
    private static Scoreboard b;
    RankManager rm = new RankManager();

    public void createScoreboard() {
        man = Bukkit.getScoreboardManager();
        b = man.getMainScoreboard();//Use main scoreboard instead of a new one for better compatability with other plugins
        for (Rank r : rm.getOrder())
            if (b.getTeam((rm.getOrder().size() - rm.getOrder().indexOf(r)) + "") == null) {
                Team t = b.registerNewTeam((rm.getOrder().size() - rm.getOrder().indexOf(r)) + "");
                t.setPrefix(r.getColor());
            }
    }

    public void addPlayer(User u) {
        if (u.getRank() == null || u.getPlayer() == null)
            return;
        Team t = b.getTeam((rm.getOrder().size() - rm.getOrder().indexOf(u.getRank())) + "");
        if (t == null)
            return;
        for (Team team : b.getTeams())
            if (team.hasEntry(u.getPlayer().getName()))
                team.removeEntry(u.getPlayer().getName());
        t.addEntry(u.getPlayer().getName());
        u.getPlayer().setScoreboard(b);
    }

    public void delPlayer(User u) {
        if (u.getRank() == null || u.getPlayer() == null)
            return;
        Team t = b.getTeam(u.getPlayer().getName());
        if (t == null)
            t = b.getTeam((rm.getOrder().size() - rm.getOrder().indexOf(u.getRank())) + "");
        if (t == null)
            return;
        t.removeEntry(u.getPlayer().getName());
        u.getPlayer().setScoreboard(man.getNewScoreboard());
    }
}