package se.fusion1013.plugin.cobaltcore.database;

import se.fusion1013.plugin.cobaltcore.CobaltCore;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * This class is responsible for creating the database and setting up the tables and values
 */
public class SQLite extends Database {
    String dbname;
    public SQLite(CobaltCore plugin){
        super(plugin);
        dbname = plugin.getConfig().getString("SQLite.Filename", "cobalt");
    }

    public Connection getSQLConnection(){
        File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");
        if (!dataFolder.exists()){
            try {
                plugin.getDataFolder().mkdir();
                dataFolder.createNewFile();
            } catch (IOException e){
                plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db", e);
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            System.out.println(dbname);
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex){
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }

        return null;
    }

    public void load(){
        /*
        executeString(SQLiteCreateWarpsTable);
        executeString(SQLiteCreateWandsTable);
        executeString(SQLiteCreateWandSpellsTable);
         */
    }

    /**
     * Performs the string statement on the database
     * @param string Statement to execute.
     */
    public void executeString(String string){
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(string);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
