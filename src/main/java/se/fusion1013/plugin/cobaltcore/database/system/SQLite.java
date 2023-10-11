package se.fusion1013.plugin.cobaltcore.database.system;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.implementations.SQLiteImplementation;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * This class is responsible for creating the database and setting up the tables and values
 */
public class SQLite extends Database {

    // ----- VARIABLES -----

    String dbname;

    // ----- CONSTRUCTORS -----

    public SQLite(CobaltCore plugin){
        super(plugin);
        dbname = plugin.getConfig().getString("SQLite.Filename", "cobalt");
    }

    // ----- LOGIC -----

    public static void dropTable(String table) {
        try (
                Connection connection = CobaltCore.getInstance().getSQLDatabase().getSQLConnection();
                PreparedStatement s = connection.prepareStatement("DROP TABLE IF EXISTS " + table + ";")
        ) {
            s.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Performs the string statement on the database.
     *
     * @param string Statement to execute.
     */
    public void executeString(String string){
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    Statement s = conn.createStatement()
            ) {
                s.executeUpdate(string);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private Lock connectionLock = new ReentrantLock();

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
            // This is a stupid solution
            long startTick = System.currentTimeMillis();
            long lastActivation = System.currentTimeMillis();
            long waitTick;
            while (connection != null && !connection.isClosed()) {
                waitTick = System.currentTimeMillis();
                if (lastActivation + 20000 <= waitTick) {
                    CobaltCore.getInstance().getLogger().warning("Something has been trying to access the database while it has been busy for " + (waitTick - startTick) + "ms now. The system might be under heavy load or something might not be closing connection objects correctly");
                    lastActivation = System.currentTimeMillis();
                }
            }

            try {
                connectionLock.lock();
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
                return connection;
            } finally {
                connectionLock.unlock();
            }


        } catch (SQLException ex) {
            System.out.println(dbname);
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex){
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }

        return null;
    }

    // ----- LOADING -----

    public void load(){
        executeString("PRAGMA foreign_keys = ON;");
    }
}
