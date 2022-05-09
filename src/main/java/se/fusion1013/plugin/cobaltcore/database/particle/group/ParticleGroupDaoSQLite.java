package se.fusion1013.plugin.cobaltcore.database.particle.group;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ParticleGroupDaoSQLite extends Dao implements IParticleGroupDao {

    public static String SQLiteCreateParticleGroupStyleView = "CREATE VIEW IF NOT EXISTS particle_group_view AS" +
            " SELECT particle_groups.uuid, particle_groups.name, particle_groups.integrity, particle_style_holders.style_name, particle_style_holders.offset_x, particle_style_holders.offset_y, particle_style_holders.offset_z, particle_style_holders.rotation_x, particle_style_holders.rotation_y, particle_style_holders.rotation_z, particle_style_holders.rotation_speed_x, particle_style_holders.rotation_speed_y, particle_style_holders.rotation_speed_z" +
            " FROM particle_groups" +
            " INNER JOIN particle_style_holders ON particle_style_holders.group_uuid = particle_groups.uuid;";

    public static String SQLiteCreateParticleGroupTable = "CREATE TABLE IF NOT EXISTS particle_groups (" +
            "`uuid` varchar(36)," +
            "`name` varchar(32) NOT NULL," +
            "`integrity` REAL NOT NULL," +
            "PRIMARY KEY (`uuid`)" +
            ");";

    public static String SQLiteCreateParticleStyleHolderTable = "CREATE TABLE IF NOT EXISTS particle_style_holders (" +
            "`group_uuid` varchar(32)," +
            "`style_name` varchar(32)," +
            "`offset_x` REAL NOT NULL," +
            "`offset_y` REAL NOT NULL," +
            "`offset_z` REAL NOT NULL," +
            "`rotation_x` REAL NOT NULL," +
            "`rotation_y` REAL NOT NULL," +
            "`rotation_z` REAL NOT NULL," +
            "`rotation_speed_x` REAL NOT NULL," +
            "`rotation_speed_y` REAL NOT NULL," +
            "`rotation_speed_z` REAL NOT NULL," +
            "PRIMARY KEY (`group_uuid`, `style_name`)," +
            "FOREIGN KEY (`style_name`) REFERENCES particle_styles(`name`) ON DELETE CASCADE," +
            "FOREIGN KEY (`group_uuid`) REFERENCES particle_groups(`uuid`) ON DELETE CASCADE" +
            ");";

    @Override
    public void removeParticleGroup(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            try {
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                conn.setAutoCommit(false);
                PreparedStatement ps = conn.prepareStatement("DELETE FROM particle_groups WHERE uuid = ?");
                PreparedStatement ps2 = conn.prepareStatement("DELETE FROM particle_style_holders WHERE group_uuid = ?");
                ps.setString(1, uuid.toString());
                ps2.setString(1, uuid.toString());
                ps.executeUpdate();
                ps2.executeUpdate();
                conn.commit();
                conn.close();
                ps.close();
                ps2.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void insertParticleGroups(List<ParticleGroup> groups) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO particle_groups(uuid, name, integrity) VALUES(?,?, ?)");
            PreparedStatement ps2 = conn.prepareStatement("INSERT OR REPLACE INTO particle_style_holders(group_uuid, style_name, offset_x, offset_y, offset_z, rotation_x, rotation_y, rotation_z, rotation_speed_x, rotation_speed_y, rotation_speed_z) VALUES(?,?,?,?,?,?,?,?,?,?,?)");

            for (ParticleGroup group : groups) {
                ps.setString(1, group.getUuid().toString());
                ps.setString(2, group.getName());
                ps.setDouble(3, group.getIntegrity());

                // Insert style holders
                for (ParticleGroup.ParticleStyleHolder holder : group.getParticleStyleList()) {
                    ps2.setString(1, group.getUuid().toString());
                    ps2.setString(2, holder.style.getName());
                    ps2.setDouble(3, holder.offset.getX());
                    ps2.setDouble(4, holder.offset.getY());
                    ps2.setDouble(5, holder.offset.getZ());
                    ps2.setDouble(6, holder.rotation.getX());
                    ps2.setDouble(7, holder.rotation.getY());
                    ps2.setDouble(8, holder.rotation.getZ());
                    ps2.setDouble(9, holder.rotationSpeed.getX());
                    ps2.setDouble(10, holder.rotationSpeed.getY());
                    ps2.setDouble(11, holder.rotationSpeed.getZ());

                    ps2.executeUpdate();
                }

                ps.executeUpdate();
            }

            conn.commit();
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Map<String, ParticleGroup> getParticleGroups() {

        Map<String, ParticleGroup> groupMap = new HashMap<>();

        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM particle_group_view");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID groupUuid = UUID.fromString(rs.getString("uuid"));
                String groupName = rs.getString("name");
                double integrity = rs.getDouble("integrity");
                String styleName = rs.getString("style_name");
                double offsetX = rs.getDouble("offset_x");
                double offsetY = rs.getDouble("offset_y");
                double offsetZ = rs.getDouble("offset_z");
                double rotationX = rs.getDouble("rotation_x");
                double rotationY = rs.getDouble("rotation_y");
                double rotationZ = rs.getDouble("rotation_z");
                double rotationSpeedX = rs.getDouble("rotation_speed_x");
                double rotationSpeedY = rs.getDouble("rotation_speed_y");
                double rotationSpeedZ = rs.getDouble("rotation_speed_z");

                ParticleGroup group = groupMap.get(groupName);
                if (group == null) {
                    group = new ParticleGroup(groupUuid, groupName);
                    group.setIntegrity(integrity);
                    groupMap.put(groupName, group);
                }
                ParticleStyle style = ParticleStyleManager.getParticleStyle(styleName);
                if (style != null) {
                    group.addParticleStyle(style);
                    group.setStyleOffset(styleName, new Vector(offsetX, offsetY, offsetZ));
                    group.setStyleRotation(styleName, new Vector(rotationX, rotationY, rotationZ), new Vector(rotationSpeedX, rotationSpeedY, rotationSpeedZ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return groupMap;
    }

    @Override
    public void update(int version) {
        if (version <= 0) {
            DataManager.getInstance().getSqliteDb().executeString("ALTER TABLE particle_groups ADD integrity INTEGER NOT NULL default 1;");
            DataManager.getInstance().getSqliteDb().executeString("DROP VIEW IF EXISTS particle_group_view;");
            DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateParticleGroupStyleView);
        }
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateParticleGroupStyleView);
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateParticleGroupTable);
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateParticleStyleHolderTable);
    }
}
