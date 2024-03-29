package se.fusion1013.plugin.cobaltcore.database.sound.area;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.plugin.cobaltcore.world.sound.SoundArea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoundAreaDaoSQLite extends Dao implements ISoundAreaDao {

    public static String SQLiteCreateSoundAreaTable = "CREATE TABLE IF NOT EXISTS sound_areas (" +
            "`sound_area_uuid` varchar(36)," +
            "`sound` TEXT NOT NULL," +
            "`activation_range` REAL NOT NULL," +
            "`cooldown` INTEGER NOT NULL," +
            "PRIMARY KEY (`sound_area_uuid`)" +
            ");";

    public static String SQLiteCreateSoundAreaView = "CREATE VIEW IF NOT EXISTS sound_area_view AS" +
            " SELECT sound_areas.sound_area_uuid, sound_areas.sound, sound_areas.activation_range, sound_areas.cooldown, locations.world, locations.x_pos, locations.y_pos, locations.z_pos" +
            " FROM sound_areas" +
            " INNER JOIN locations ON locations.uuid = sound_areas.sound_area_uuid;";

    @Override
    public Map<Location, SoundArea> getSoundAreas() {
        Map<Location, SoundArea> soundAreas = new HashMap<>();

        try (
                Connection conn = SQLiteImplementation.getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM sound_area_view");
                ResultSet rs = ps.executeQuery();
        ) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("sound_area_uuid"));
                String sound = rs.getString("sound");
                double activationRange = rs.getDouble("activation_range");
                int cooldown = rs.getInt("cooldown");
                World world = Bukkit.getWorld(rs.getString("world"));
                Location location = new Location(world, rs.getDouble("x_pos"), rs.getDouble("y_pos"), rs.getDouble("z_pos"));

                soundAreas.put(location, new SoundArea(uuid, location, activationRange, cooldown, sound));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return soundAreas;
    }

    @Override
    public void saveSoundAreas(Map<Location, SoundArea> soundAreaMap) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO sound_areas(sound_area_uuid, sound, activation_range, cooldown) VALUES(?,?,?,?)");
                    PreparedStatement psLocation = conn.prepareStatement("INSERT OR REPLACE INTO locations(uuid, world, x_pos, y_pos, z_pos, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?)")
            ) {
                conn.setAutoCommit(false);

                for (SoundArea area : soundAreaMap.values()) {
                    UUID uuid = area.uuid;
                    Location location = area.location;

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
                    ps.setString(2, area.sound);
                    ps.setDouble(3, area.activationRange);
                    ps.setInt(4, area.cooldown);

                    ps.executeUpdate();
                }

                conn.commit();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void removeSoundArea(UUID uuid) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
                try (
                        PreparedStatement ps = conn.prepareStatement("DELETE FROM sound_areas WHERE sound_area_uuid = ?");
                ) {
                    // Remove sound area
                    ps.setString(1, uuid.toString());
                    ps.executeUpdate();
                    conn.close();

                    // Remove location
                    DataManager.getInstance().getDao(ILocationDao.class).removeLocation(uuid);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        });
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        SQLiteImplementation.getSqliteDb().executeString(SQLiteCreateSoundAreaTable);
        SQLiteImplementation.getSqliteDb().executeString(SQLiteCreateSoundAreaView);
    }

    @Override
    public String getId() {
        return "sound_area";
    }
}
