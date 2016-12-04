package me.eddiep.minecraft.ls.game.lsrating;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class LSRating {
    private int rating;
    private int avgAir;
    private double variance;
    private int matchesPlayed;
    private int[] matches; //Match rewards

    private static final double LOSS_MULTIPLIER = .875;

    public static final int DEFAULT_RATING = 1000;

    public LRSating() {}

    public void addMatch(int blockCount, String uuid) {
        this.matchesPlayed++;
        String url = "jdbc:mariadb://" + config.getString("Lavasurvival.DBHost") + "/" + config.getString("Lavasurvival.DBTable"), user = config.getString("Lavasurvival.DBUser"),
                pass = config.getString("Lavasurvival.DBPassword");
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "IF NOT EXISTS (SELECT 1 FROM users WHERE uuid='" + uuid + "') " +
                            "BEGIN " +
                                "INSERT INTO users (uuid, rating, matches, matchCount) VALUES ('" + uuid + "', '" + this.DEFAULT_RATING + "', '" + blockCount + "', '1');" +
                            "END" +
                    "ELSE " +
                            "BEGIN " +
                                "UPDATE users SET matches=CONCAT('," + blockCount + "',matches) WHERE uuid='" + uuid + "';" +
                            "END ;"
            );
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRating() {return this.rating;}
}
