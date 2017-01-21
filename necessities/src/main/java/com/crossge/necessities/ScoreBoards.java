package com.crossge.necessities;

import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class ScoreBoards {
    private final String[] ALPHABET = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private ScoreboardManager man;
    private Scoreboard b;

    void createScoreboard() {
        this.man = Bukkit.getScoreboardManager();
        RankManager rm = Necessities.getRM();
        this.b = this.man.getMainScoreboard();//Use main scoreboard instead of a new one for better compatibility with other plugins
        for (Rank r : rm.getOrder()) {
            Team t = this.b.getTeam(fromInt(rm.getOrder().size() - rm.getOrder().indexOf(r)));
            if (t == null)
                t = this.b.registerNewTeam(fromInt(rm.getOrder().size() - rm.getOrder().indexOf(r)));
            t.setPrefix(r.getColor());
            t.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
    }

    public void addPlayer(User u) {
        if (u.getRank() == null || u.getPlayer() == null)
            return;
        RankManager rm = Necessities.getRM();
        Team t = this.b.getTeam(fromInt(rm.getOrder().size() - rm.getOrder().indexOf(u.getRank())));
        if (t == null)
            return;
        this.b.getTeams().stream().filter(team -> team.hasEntry(u.getPlayer().getName())).forEach(team -> team.removeEntry(u.getPlayer().getName()));
        t.addEntry(u.getPlayer().getName());
        u.getPlayer().setScoreboard(this.b);
    }

    public void delPlayer(User u) {
        if (u.getRank() == null || u.getPlayer() == null)
            return;
        Team t = this.b.getTeam(u.getPlayer().getName());
        if (t == null) {
            RankManager rm = Necessities.getRM();
            t = this.b.getTeam(fromInt(rm.getOrder().size() - rm.getOrder().indexOf(u.getRank())));
        }
        if (t == null)
            return;
        t.removeEntry(u.getPlayer().getName());
        u.getPlayer().setScoreboard(this.man.getNewScoreboard());
    }

    private String fromInt(int toTranslate) {
        toTranslate -= 1;
        return (toTranslate < 0 || this.ALPHABET.length <= toTranslate) ? "ERROR" : this.ALPHABET[toTranslate];
    }
}