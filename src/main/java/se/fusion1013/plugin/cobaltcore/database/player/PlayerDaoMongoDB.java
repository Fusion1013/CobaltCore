package se.fusion1013.plugin.cobaltcore.database.player;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;

import java.util.UUID;

public class PlayerDaoMongoDB extends Dao implements IPlayerDao {
    @Override
    public void insertPlayer(Player player) {
    }

    @Override
    public String getPlayerName(UUID uuid) {
        return null;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return null;
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.MONGODB;
    }

    @Override
    public void init() {

    }
}
