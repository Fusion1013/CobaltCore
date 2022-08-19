package se.fusion1013.plugin.cobaltcore.database.spawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.world.spawner.CustomSpawner;
import se.fusion1013.plugin.cobaltcore.world.spawner.SpawnerType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomSpawnerDaoSQLite extends Dao implements ICustomSpawnerDao {

    // ----- VARIABLES -----

    public static String SQLiteCreateCustomSpawnerTable = "CREATE TABLE IF NOT EXISTS custom_spawners (" +
            "`custom_spawner_uuid` varchar(36)," +
            "`entity` TEXT NOT NULL," +
            "`spawner_type` TEXT NOT NULL," +
            "`spawn_count` INTEGER NOT NULL," +
            "`activation_range` REAL NOT NULL," +
            "`spawn_radius` INTEGER NOT NULL," +
            "`cooldown` INTEGER," +
            "`delay_summon` INTEGER NOT NULL, " +
            "`play_sound` TEXT NOT NULL," +
            "`play_sound_delayed` TEXT NOT NULL," +
            "PRIMARY KEY (`custom_spawner_uuid`)" +
            ");";

    public static String SQLiteCreateCustomSpawnerView = "CREATE VIEW IF NOT EXISTS custom_spawners_view AS" +
            " SELECT custom_spawners.custom_spawner_uuid, custom_spawners.entity, custom_spawners.spawner_type, custom_spawners.spawn_count, custom_spawners.activation_range, custom_spawners.spawn_radius, custom_spawners.cooldown, custom_spawners.delay_summon, custom_spawners.play_sound, custom_spawners.play_sound_delayed, locations.world, locations.x_pos, locations.y_pos, locations.z_pos" +
            " FROM custom_spawners" +
            " INNER JOIN locations ON locations.uuid = custom_spawners.custom_spawner_uuid;";

    @Override
    public Map<Long, Map<Location, CustomSpawner>> getCustomSpawners() {
        Map<Long, Map<Location, CustomSpawner>> spawners = new HashMap<>();

        try (
                Connection conn = getDataManager().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM custom_spawners_view");
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("custom_spawner_uuid"));
                String entity = rs.getString("entity");
                SpawnerType spawnerType = SpawnerType.valueOf(rs.getString("spawner_type"));
                int spawnCount = rs.getInt("spawn_count");
                double activationRange = rs.getDouble("activation_range");
                int spawnRadius = rs.getInt("spawn_radius");

                World world = Bukkit.getWorld(rs.getString("world"));
                Location location = new Location(world, rs.getDouble("x_pos"), rs.getDouble("y_pos"), rs.getDouble("z_pos"));
                long chunk = location.getChunk().getChunkKey();

                CustomSpawner spawner = null;

                switch (spawnerType) {
                    case CONTINUOUS -> {
                        int cooldown = rs.getInt("cooldown");
                        spawner = new CustomSpawner(uuid, location, entity, spawnCount, activationRange, spawnRadius, cooldown);
                    }
                    case INSTANT -> spawner = new CustomSpawner(uuid, location, entity, spawnCount, activationRange, spawnRadius);
                }

                if (spawner == null) continue;

                int delaySummon = rs.getInt("delay_summon");
                String playSound = rs.getString("play_sound");
                String playSoundDelayed = rs.getString("play_sound_delayed");

                spawner.addSpawnDelay(delaySummon);
                spawner.addSound(playSound);
                spawner.addDelayedSound(playSoundDelayed);

                Map<Location, CustomSpawner> spawnerMap = spawners.computeIfAbsent(chunk, k -> new HashMap<>());
                spawnerMap.put(location, spawner);
                spawners.put(location.getChunk().getChunkKey(), spawnerMap);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return spawners;
    }

    @Override
    public void saveCustomSpawners(Map<Long, Map<Location, CustomSpawner>> spawners) {
        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO custom_spawners(custom_spawner_uuid, entity, spawner_type, spawn_count, activation_range, spawn_radius, cooldown, delay_summon, play_sound, play_sound_delayed) VALUES(?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement psLocation = conn.prepareStatement("INSERT OR REPLACE INTO locations(uuid, world, x_pos, y_pos, z_pos, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?)")
        ) {
            conn.setAutoCommit(false);

            for (Map<Location, CustomSpawner> spawnerMap : spawners.values()) {
                for (CustomSpawner spawner : spawnerMap.values()) {
                    UUID uuid = spawner.getUuid();
                    String entity = spawner.getEntityName();
                    SpawnerType spawnerType = spawner.getType();
                    int spawnCount = spawner.getSpawnCount();
                    double activationRange = spawner.getActivationRange();
                    int spawnRadius = spawner.getSpawnRadius();
                    int cooldown = spawner.getCooldown();
                    Location location = spawner.getLocation();
                    int delaySummon = spawner.getDelaySummon();
                    String playSound = spawner.getPlaySound();
                    String playSoundDelayed = spawner.getPlaySoundDelayed();

                    // Insert location
                    psLocation.setString(1, uuid.toString());
                    psLocation.setString(2, location.getWorld().getName());
                    psLocation.setDouble(3, location.getX());
                    psLocation.setDouble(4, location.getY());
                    psLocation.setDouble(5, location.getZ());
                    psLocation.setDouble(6, location.getYaw());
                    psLocation.setDouble(7, location.getPitch());
                    psLocation.execute();

                    ps.setString(1, uuid.toString());
                    ps.setString(2, entity);
                    ps.setString(3, spawnerType.name());
                    ps.setInt(4, spawnCount);
                    ps.setDouble(5, activationRange);
                    ps.setInt(6, spawnRadius);
                    ps.setInt(7, cooldown);
                    ps.setInt(8, delaySummon);
                    ps.setString(9, playSound);
                    ps.setString(10, playSoundDelayed);

                    ps.executeUpdate();
                }
            }

            conn.commit();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void removeCustomSpawner(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            try (
                    Connection conn = getDataManager().getSqliteDb().getSQLConnection();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM custom_spawners WHERE custom_spawner_uuid = ?")
            ) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();

                // Remove Location
                DataManager.getInstance().getDao(ILocationDao.class).removeLocation(uuid);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateCustomSpawnerTable);
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateCustomSpawnerView);
    }

    @Override
    public String getId() {
        return "custom_spawner";
    }
}
