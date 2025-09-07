package se.fusion1013.cobaltCore.database.system;

import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Database {

    // ----- VARIABLES -----

    CobaltCore plugin;
    Connection connection;

    // ----- CONSTRUCTOR -----

    public Database(CobaltCore instance){
        plugin = instance;
    }

    // ----- DATABASE INFO -----

    public static String[] getDatabaseTables() {
        List<String> tables = new ArrayList<>();

        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> { // TODO: Verify that this is working as expected
            try (
                    PreparedStatement stmt = conn.prepareStatement("SELECT name FROM sqlite_schema WHERE type ='table' AND name NOT LIKE 'sqlite_%'");
                    ResultSet rs = stmt.executeQuery();
            ) {
                while (rs.next()) {
                    tables.add(rs.getString("name"));
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        return tables.toArray(new String[0]);
    }

    // ----- GETTERS / SETTERS -----

    public abstract Connection getSQLConnection();

    public abstract void load();

    public abstract void executeString(String string);
}
