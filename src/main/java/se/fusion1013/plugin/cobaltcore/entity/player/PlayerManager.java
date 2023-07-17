package se.fusion1013.plugin.cobaltcore.entity.player;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.storage.IPlayerDataStorageDao;
import se.fusion1013.plugin.cobaltcore.database.storage.PlayerDataStorageDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager extends Manager implements Listener {

    //region FIELDS

    private static final Map<UUID, JsonObject> PLAYER_DATA = new HashMap<>();

    //endregion

    //region CONSTRUCTORS

    public PlayerManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    //endregion

    //region DATA MANAGEMENT

    public static void updatePlayerDataStorage(UUID playerUuid) {
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class)
                .getDao(IPlayerDataStorageDao.class)
                .insertPlayerDataStorageAsync(playerUuid, PLAYER_DATA.get(playerUuid).toString());
    }

    public static void setPlayerDataValue(UUID playerUuid, String key, String value) {
        JsonObject data = PLAYER_DATA.get(playerUuid);
        if (data == null) return;

        data.addProperty(key, value);
        updatePlayerDataStorage(playerUuid);
    }

    public static String getPlayerDataValue(UUID playerUuid, String key) {
        JsonObject data = PLAYER_DATA.get(playerUuid);
        if (data == null) return null;
        return data.get(key).getAsString();
    }

    public static JsonObject getPlayerData(UUID playerUuid) {
        return PLAYER_DATA.get(playerUuid);
    }

    //endregion

    //region LISTENERS

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PLAYER_DATA.put(
                event.getPlayer().getUniqueId(),
                CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class)
                        .getDao(IPlayerDataStorageDao.class)
                        .getPlayerData(event.getPlayer().getUniqueId())
        );
    }

    //endregion

    //region RELOADING/DISABLING

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltCore.getInstance());
    }

    @Override
    public void disable() {

    }

    //endregion
}
