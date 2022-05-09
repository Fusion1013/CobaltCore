package se.fusion1013.plugin.cobaltcore.database.system;

import org.bukkit.Bukkit;
import se.fusion1013.plugin.cobaltcore.CobaltCore;

public abstract class Dao {

    protected void async(Runnable asyncCallback) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), asyncCallback);
    }

}
