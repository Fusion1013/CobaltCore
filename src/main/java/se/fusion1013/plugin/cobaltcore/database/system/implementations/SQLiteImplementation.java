package se.fusion1013.plugin.cobaltcore.database.system.implementations;

import org.bukkit.Bukkit;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.location.LocationDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.mappings.IMappingsDao;
import se.fusion1013.plugin.cobaltcore.database.mappings.MappingsDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.particle.group.IParticleGroupDao;
import se.fusion1013.plugin.cobaltcore.database.particle.group.ParticleGroupDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.particle.style.IParticleStyleDao;
import se.fusion1013.plugin.cobaltcore.database.particle.style.ParticleStyleDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.player.IPlayerDao;
import se.fusion1013.plugin.cobaltcore.database.player.PlayerDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.setting.ISettingDao;
import se.fusion1013.plugin.cobaltcore.database.setting.SettingDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.sound.area.ISoundAreaDao;
import se.fusion1013.plugin.cobaltcore.database.sound.area.SoundAreaDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.spawner.CustomSpawnerDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.spawner.ICustomSpawnerDao;
import se.fusion1013.plugin.cobaltcore.database.storage.IObjectStorageDao;
import se.fusion1013.plugin.cobaltcore.database.storage.IPlayerDataStorageDao;
import se.fusion1013.plugin.cobaltcore.database.storage.ObjectStorageDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.storage.PlayerDataStorageDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.structure.IStructureDao;
import se.fusion1013.plugin.cobaltcore.database.structure.StructureDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.system.*;
import se.fusion1013.plugin.cobaltcore.database.trades.ITradesDao;
import se.fusion1013.plugin.cobaltcore.database.trades.TradesDaoSQLite;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQLiteImplementation implements IDataImplementation {

    // Pending Operations
    private final int operationLimit = 100;
    private int currentOperations = 0;
    private final Queue<ISQLiteDataAccessor> sqlitePendingOperations = new LinkedList<>();

    // Database
    private static Database sqliteDb;

    @Override
    public void loadConnection() {
        if (sqliteDb == null) {
            sqliteDb = new SQLite(CobaltCore.getInstance());
            sqliteDb.load();
        }
    }

    @Override
    public DataManager.StorageType type() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void initDao(DataManager manager) {
        manager.registerDao(new SystemDaoSQLite(), ISystemDao.class);

        manager.registerDao(new MappingsDaoSQLite(), IMappingsDao.class);
        manager.registerDao(new LocationDaoSQLite(), ILocationDao.class);
        manager.registerDao(new ParticleGroupDaoSQLite(), IParticleGroupDao.class);
        manager.registerDao(new ParticleStyleDaoSQLite(), IParticleStyleDao.class);
        manager.registerDao(new PlayerDaoSQLite(), IPlayerDao.class);
        manager.registerDao(new SettingDaoSQLite(), ISettingDao.class);
        manager.registerDao(new TradesDaoSQLite(), ITradesDao.class);
        manager.registerDao(new SoundAreaDaoSQLite(), ISoundAreaDao.class);
        manager.registerDao(new CustomSpawnerDaoSQLite(), ICustomSpawnerDao.class);
        manager.registerDao(new StructureDaoSQLite(), IStructureDao.class);
        manager.registerDao(new ObjectStorageDaoSQLite(), IObjectStorageDao.class);
        manager.registerDao(new PlayerDataStorageDaoSQLite(), IPlayerDataStorageDao.class);
    }

    private static final Lock sqliteOperationLock = new ReentrantLock();

    /**
     * Performs a thread-safe operation on the SQLite database.
     *
     * @param dataAccessor the <code>ISQLiteDataAccessor</code>.
     */
    public static void performThreadSafeSQLiteOperations(ISQLiteDataAccessor dataAccessor) {
        try (
                Connection conn = getSqliteDb().getSQLConnection()
        ) {

            if (dataAccessor != null) {
                sqliteOperationLock.lock();
                dataAccessor.modifyDatabase(conn);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            sqliteOperationLock.unlock();
        }
    }

    /**
     * Adds a new <code>ISQLiteDataAccessor</code> to the pending operations.
     *
     * @param dataAccessor the <code>ISQLiteDataAccessor</code>.
     */
    public void insertSQLiteOperation(ISQLiteDataAccessor dataAccessor) {
        sqlitePendingOperations.add(dataAccessor);
    }

    private void processPendingOperations() {

        while (currentOperations < operationLimit && !sqlitePendingOperations.isEmpty()) {
            try (
                    Connection conn = getSqliteDb().getSQLConnection()
            ) {
                ISQLiteDataAccessor accessor = sqlitePendingOperations.poll();
                if (accessor != null) accessor.modifyDatabase(conn);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            currentOperations++;
        }

        if (currentOperations > 0) {
            CobaltCore.getInstance().getLogger().info("Performed " + currentOperations + " database operations");
            CobaltCore.getInstance().getLogger().info("Pending operations left: " + sqlitePendingOperations.size());
        }

        currentOperations = 0;
    }

    public static Database getSqliteDb() {
        if (sqliteDb == null) {
            sqliteDb = new SQLite(CobaltCore.getInstance());
            sqliteDb.load();
        }
        return sqliteDb;
    }

    @Override
    public void onReload() {
        // Initializes the pending operations processor
        Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltCore.getInstance(), this::processPendingOperations, 0, 20*10); // Run once every 10 seconds
    }

    @Override
    public void onDisable() {
        while (!sqlitePendingOperations.isEmpty()) {
            sqlitePendingOperations.poll().modifyDatabase(sqliteDb.getSQLConnection());
        }
    }
}
