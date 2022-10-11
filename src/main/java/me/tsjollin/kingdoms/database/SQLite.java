package me.tsjollin.kingdoms.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import me.tsjollin.kingdoms.Main;

public class SQLite extends Database{
    String dbname;
    public SQLite(Main instance){
        super(instance);
        dbname = Main.getKConfig().getString("SQLite.Filename", "database"); // Set the table name here e.g player_kills
    }
    //https://sqliteonline.com/
    public String SQLiteCreateKingdomTable = "CREATE TABLE IF NOT EXISTS kingdoms (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`name` varchar(255) NOT NULL," +
            "`prefix` varchar(255)," +
            "`suffix` varchar(255)," +
            "`spawn` varchar(255)," +
            "`invite_only` tinyint(1) DEFAULT 0" +
            ");";
    public String SQLiteCreatePlayersTable = "CREATE TABLE IF NOT EXISTS players (" +
            "`id` varchar(32) NOT NULL," +
            "`kingdom_id` int(11)," +
            "PRIMARY KEY (`id`)," +
            "CONSTRAINT fk_players_kingdom_id FOREIGN KEY (kingdom_id) REFERENCES kingdoms(id) ON DELETE SET NULL ON UPDATE SET NULL" +
            ");";

    // SQL creation stuff, You can leave the blow stuff untouched.
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            Statement s = connection.createStatement();
            s.execute("PRAGMA foreign_keys = ON");
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateKingdomTable);
            s.executeUpdate(SQLiteCreatePlayersTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}