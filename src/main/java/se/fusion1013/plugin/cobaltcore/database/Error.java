package se.fusion1013.plugin.cobaltcore.database;

import se.fusion1013.plugin.cobaltcore.CobaltCore;

import java.util.logging.Level;

public class Error {
    public static void execute(CobaltCore plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(CobaltCore plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
