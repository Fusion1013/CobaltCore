package se.fusion1013.plugin.cobaltcore.database.particle.style;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleStyleDaoSQLite extends Dao implements IParticleStyleDao {

    // ----- CREATE TABLES -----

    public static String SQLiteCreateParticleStyleTable = "CREATE TABLE IF NOT EXISTS particle_styles (" +
            "`name` varchar(32)," +
            "`particle` varchar(32) NOT NULL," +
            "`offset_x` REAL NOT NULL," +
            "`offset_y` REAL NOT NULL," +
            "`offset_z` REAL NOT NULL," +
            "`count` REAL NOT NULL," +
            "`speed` REAL NOT NULL," +
            "`rotation_x` REAL NOT NULL," +
            "`rotation_y` REAL NOT NULL," +
            "`rotation_z` REAL NOT NULL," +
            "`angular_velocity_x` REAL NOT NULL," +
            "`angular_velocity_y` REAL NOT NULL," +
            "`angular_velocity_z` REAL NOT NULL," +
            "`style_type` varchar(32) NOT NULL," +
            "`data` TEXT NOT NULL," +
            "`style_extra` TEXT NOT NULL," +
            "PRIMARY KEY (`name`)" +
            ");";

    @Override
    public void removeParticleStyle(String styleName) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            try (
                    Connection conn = SQLiteImplementation.getSqliteDb().getSQLConnection();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM particle_styles WHERE name = ?");
                    PreparedStatement ps2 = conn.prepareStatement("DELETE FROM particle_style_holders WHERE style_name = ?")
            ) {
                conn.setAutoCommit(false);
                ps.setString(1, styleName);
                ps2.setString(1, styleName);
                ps.executeUpdate();
                ps2.executeUpdate();
                conn.commit();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void insertParticleStyles(List<ParticleStyle> styles) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO particle_styles(name, particle, offset_x, offset_y, offset_z, count, speed, rotation_x, rotation_y, rotation_z, angular_velocity_x, angular_velocity_y, angular_velocity_z, style_type, data, style_extra) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
            ) {
                conn.setAutoCommit(false);
                Gson gson = new Gson();
                for (ParticleStyle style : styles) {
                    ps.setString(1, style.getName());
                    ps.setString(2, style.getParticle().name());
                    ps.setDouble(3, style.getOffset().getX());
                    ps.setDouble(4, style.getOffset().getY());
                    ps.setDouble(5, style.getOffset().getZ());
                    ps.setDouble(6, style.getCount());
                    ps.setDouble(7, style.getSpeed());
                    ps.setDouble(8, style.getRotation().getX());
                    ps.setDouble(9, style.getRotation().getY());
                    ps.setDouble(10, style.getRotation().getZ());
                    ps.setDouble(11, style.getAngularVelocityX());
                    ps.setDouble(12, style.getAngularVelocityY());
                    ps.setDouble(13, style.getAngularVelocityZ());
                    ps.setString(14, style.getInternalName());
                    ps.setString(15, gson.toJson(style.getData()));
                    ps.setString(16, style.getExtraSettings());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public Map<String, ParticleStyle> getParticleStyles() {

        Map<String, ParticleStyle> styles = new HashMap<>();

        try (
                Connection conn = SQLiteImplementation.getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM particle_styles");
                ResultSet rs = ps.executeQuery()
        ) {

            Gson gson = new Gson();

            while (rs.next()) {
                String name = rs.getString("name");
                Particle particle = Particle.valueOf(rs.getString("particle"));
                double offsetX = rs.getDouble("offset_x");
                double offsetY = rs.getDouble("offset_y");
                double offsetZ = rs.getDouble("offset_z");
                int count = rs.getInt("count");
                double speed = rs.getDouble("speed");
                double rotationX = rs.getDouble("rotation_x");
                double rotationY = rs.getDouble("rotation_y");
                double rotationZ = rs.getDouble("rotation_z");
                double angularVelocityX = rs.getDouble("angular_velocity_x");
                double angularVelocityY = rs.getDouble("angular_velocity_y");
                double angularVelocityZ = rs.getDouble("angular_velocity_z");
                String styleType = rs.getString("style_type");

                String dataString = rs.getString("data");
                Object data = null;
                if (particle.getDataType() != Void.class) data = gson.fromJson(dataString, particle.getDataType());

                String styleExtra = rs.getString("style_extra");

                ParticleStyle style = ParticleStyleManager.getDefaultParticleStyle(styleType).clone();
                if (style != null) {
                    style.setName(name);
                    style.setParticle(particle);
                    style.setOffset(new Vector(offsetX, offsetY, offsetZ));
                    style.setCount(count);
                    style.setSpeed(speed);
                    style.updateRotation(new Vector(rotationX, rotationY, rotationZ), angularVelocityX, angularVelocityY, angularVelocityZ);
                    if (data != null) style.setData(data);
                    style.setExtraSettings(styleExtra);

                    styles.put(name, style);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return styles;
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        SQLiteImplementation.getSqliteDb().executeString(SQLiteCreateParticleStyleTable);
    }
}
