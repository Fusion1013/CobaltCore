package se.fusion1013.plugin.cobaltcore.database.system;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.trades.CustomTradesManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.settings.PlayerSettingHolder;
import se.fusion1013.plugin.cobaltcore.util.PreCalculateWeightsRandom;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
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
        try {
            Connection connection = CobaltCore.getInstance().getSQLDatabase().getSQLConnection();
            PreparedStatement s = connection.prepareStatement("DROP TABLE IF EXISTS " + table + ";");
            s.executeUpdate();
            s.close();
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
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(string);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    // ----- LOADING -----

    public void load(){
        executeString("PRAGMA foreign_keys = ON;");
    }
}
