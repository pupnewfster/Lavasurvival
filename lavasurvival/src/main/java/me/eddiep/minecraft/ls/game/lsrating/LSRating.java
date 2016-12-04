package me.eddiep.minecraft.ls.game.lsrating;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class LSRating {
    private int rating;

    public LRSating() {}

    public void addMatch(int blockCount, String uuid) {
        String url = "jdbc:mariadb://" + config.getString("Lavasurvival.DBHost") + "/" + config.getString("Lavasurvival.DBTable"), user = config.getString("Lavasurvival.DBUser"),
                pass = config.getString("Lavasurvival.DBPassword");
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "UPDATE users SET matches=CONCAT('," + blockCount + "',matches) WHERE uuid='" + uuid + "';"
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
