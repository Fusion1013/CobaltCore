package se.fusion1013.plugin.cobaltcore.database.structure;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.util.LocationUUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StructureDaoSQLite extends Dao implements IStructureDao {

    public static String SQLiteCreateStructureTable = "CREATE TABLE IF NOT EXISTS structures (" +
            "`structure_name` TEXT," +
            "`location_uuid` varchar(36)," +
            "PRIMARY KEY (`structure_name`,`location_uuid`)" +
            ");";

    public static String SQLiteCreateStructureView = "CREATE VIEW IF NOT EXISTS structure_view AS" +
            " SELECT structures.structure_name, structures.location_uuid, locations.world, locations.x_pos, locations.y_pos, locations.z_pos" +
            " FROM structures" +
            " INNER JOIN locations ON locations.uuid = structures.location_uuid;";

    @Override
    public Map<Long, Map<LocationUUID, String>> getStructures() {
        Map<Long, Map<LocationUUID, String>> structures = new HashMap<>();

        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM structure_view");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("structure_name");
                UUID uuid = UUID.fromString(rs.getString("location_uuid"));
                World world = Bukkit.getWorld(rs.getString("world"));
                Location location = new Location(world, rs.getDouble("x_pos"), rs.getDouble("y_pos"), rs.getDouble("z_pos"));;

                structures.computeIfAbsent(location.getChunk().getChunkKey(), k -> new HashMap<>()).put(new LocationUUID(uuid, location), name);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return structures;
    }

    @Override
    public void saveStructures(Map<Long, Map<LocationUUID, String>> structures) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO structures(structure_name, location_uuid) VALUES(?,?)");

            for (Map<LocationUUID, String> locStruPair : structures.values()) {
                for (LocationUUID location : locStruPair.keySet()) {
                    String structure = locStruPair.get(location);
                    ps.setString(1, structure);
                    ps.setString(2, location.uuid().toString());

                    DataManager.getInstance().getDao(ILocationDao.class).insertLocation(location.uuid(), location.location());

                    ps.executeUpdate();
                }
            }

            conn.commit();
            conn.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateStructureTable);
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateStructureView);
    }

    @Override
    public String getId() {
        return "structure";
    }
}
