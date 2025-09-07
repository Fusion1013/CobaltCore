package se.fusion1013.cobaltCore.database.system;

import org.bukkit.Bukkit;
import se.fusion1013.cobaltCore.CobaltCore;

public abstract class Dao {

    protected DataManager getDataManager() {
        return CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class);
    }

    protected void async(Runnable asyncCallback) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), asyncCallback);
    }

}
