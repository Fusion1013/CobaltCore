package se.fusion1013.plugin.cobaltcore.database;

import se.fusion1013.plugin.cobaltcore.CobaltCore;

import java.sql.Connection;

/**
 * This class is used to access and edit/retrieve values from the database
 */
public abstract class Database {
    CobaltCore plugin;
    Connection connection;

    public Database(CobaltCore instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public abstract void executeString(String string);
}
