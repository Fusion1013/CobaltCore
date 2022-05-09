package se.fusion1013.plugin.cobaltcore.database.player;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.database.system.IDao;

import java.util.UUID;

public interface IPlayerDao extends IDao {

    /**
     * Inserts a <code>Player</code>.
     *
     * @param player the <code>Player</code> to insert.
     */
    void insertPlayer(Player player);

    /**
     * Gets a <code>Player</code>'s name from the data storage.
     *
     * @param uuid the <code>UUID</code> of the <code>Player</code>.
     * @return the name of the <code>Player</code>.
     */
    String getPlayerName(UUID uuid);

    /**
     * Gets a <code>Player</code> from the data storage.
     *
     * @param uuid the <code>UUID</code> of the <code>Player</code>.
     * @return the <code>Player</code>.
     */
    Player getPlayer(UUID uuid);

    @Override
    default String getId() { return "player"; }

}
