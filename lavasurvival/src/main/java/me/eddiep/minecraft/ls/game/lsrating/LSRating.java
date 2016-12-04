package me.eddiep.minecraft.ls.game.lsrating;

import me.eddiep.minecraft.ls.Lavasurvival;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class LSRating {
    private int rating;

    public void addMatch(int blockCount, String uuid) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(Lavasurvival.INSTANCE.getDBURL(), Lavasurvival.INSTANCE.getDBUser(), Lavasurvival.INSTANCE.getDBPass());
            Statement stmt = conn.createStatement();
            stmt.execute("UPDATE users SET matches = CONCAT(\"," + blockCount + "\", matches) WHERE uuid= \"" + uuid + "\";");
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRating() {
        return this.rating;
    }
}