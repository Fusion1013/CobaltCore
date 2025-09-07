package se.fusion1013.cobaltCore.database.player;

import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.database.system.IDao;

import java.util.UUID;

public interface IPlayerDao extends IDao {

    void insertPlayer(Player player);
    String getPlayerName(UUID uuid);
    Player getPlayer(UUID uuid);

    @Override
    default String getId() { return "player"; }
}
