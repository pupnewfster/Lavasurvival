package com.crossge.necessities;

import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

public class ScoreBoards {
    public String getPrefix(User u) {
        Team t = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(u.getName());
        return t == null ? "" : t.getPrefix();
    }
}