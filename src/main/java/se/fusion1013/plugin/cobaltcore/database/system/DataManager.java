package se.fusion1013.plugin.cobaltcore.database.system;

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
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
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
import se.fusion1013.plugin.cobaltcore.database.trades.ITradesDao;
import se.fusion1013.plugin.cobaltcore.database.trades.TradesDaoSQLite;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataManager extends Manager {

    // ----- VARIABLES -----

    StorageType storageType;
    StorageType fallbackStorageType = StorageType.SQLITE; // TODO: Make setting
    Map<StorageType, Map<Class<?>, IDao>> daoImplementations = new HashMap<>();

    // Pending Operations
    private final int operationLimit = 100;
    private int currentOperations = 0;

    private final Queue<ISQLiteDataAccessor> sqlitePendingOperations = new LinkedList<>();

    // Data storage accessors
    private static Database sqliteDb;

    // ----- CONSTRUCTORS -----

    public DataManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- REGISTER DATA STORAGE -----

    /**
     * Registers and initializes a <code>IDao</code> implementation.
     *
     * @param dao the <code>IDao</code> to register.
     * @param <T> the <code>IDao</code> type.
     * @return the <code>IDao</code>.
     */
    public <T extends IDao> T registerDao(T dao, Class<?> parent) {
        // Adds the dao to the map
        StorageType type = dao.getStorageType();
        daoImplementations.computeIfAbsent(type, k -> new HashMap<>());
        daoImplementations.get(type).put(parent, dao);

        // Initializes the dao
        dao.init();

        // Updates the dao if needed
        ISystemDao system = getDao(ISystemDao.class);
        dao.update(system.getVersion(dao.getId() + dao.getStorageType().name(), dao.getVersion()));
        system.setVersion(dao.getId() + dao.getStorageType().name(), dao.getVersion());

        return dao;
    }

    // ----- DAO GETTING -----

    /**
     * Gets a <code>IDao</code> instance.
     *
     * @param dao the <code>IDao</code> instance to get.
     * @param <T> the type of the <code>IDao</code>.
     * @return the <code>IDao</code> instance.
     */
    @SuppressWarnings("unchecked")
    public <T extends IDao> T getDao(Class<T> dao) {

        CobaltCore.getInstance().getLogger().info("Searching for dao " + dao.getName() + "...");

        // Find the correct storage pair
        Map<Class<?>, IDao> storage = daoImplementations.get(storageType);
        if (storage == null) {
            CobaltCore.getInstance().getLogger().warning("Storage of type " + storageType.name() + " not found, using fallback");
            storage = daoImplementations.get(fallbackStorageType);

            if (storage == null) {
                CobaltCore.getInstance().getLogger().warning("Fallback storage not found");
                return null;
            }
        }

        // Get the storage object
        IDao storageDao = storage.get(dao);

        // If the storage was not found, send an error
        if (storageDao == null) {
            CobaltCore.getInstance().getLogger().warning("Could not get storage dao of type " + dao.getName());
            return null;
        }

        return (T)storageDao;
    }

    public enum StorageType {
        SQLITE,
        MONGODB
    }

    // ----- PENDING OPERATIONS -----

    Lock sqliteOperationLock = new ReentrantLock();

    /**
     * Performs a thread-safe operation on the SQLite database.
     *
     * @param dataAccessor the <code>ISQLiteDataAccessor</code>.
     */
    public void performThreadSafeSQLiteOperations(ISQLiteDataAccessor dataAccessor) {
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

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

        // MongoDB mongoDB = MongoDB.getInstance();
        // mongoDB.connect();

        // Load the database connection
        loadDatabaseConnection();

        // Initialize all the dao's
        initDao();

        // Initializes the pending operations processor
        Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltCore.getInstance(), this::processPendingOperations, 0, 20*10); // Run once every 10 seconds
    }

    /**
     * Loads the database connection.
     */
    private void loadDatabaseConnection() {
        // Load Database Connection
        String databaseImplementation = (String) ConfigManager.getInstance().getFromConfig(CobaltCore.getInstance(), "cobalt.yml", "database-implementation");

        switch (databaseImplementation) {
            case "sqlite" -> {
                storageType = StorageType.SQLITE;

                // Initialize database
                sqliteDb = new SQLite(CobaltCore.getInstance());
                sqliteDb.load();
            }
            case "mongodb" -> {
                storageType = StorageType.MONGODB;

                // Initialize database
                String url = (String) ConfigManager.getInstance().getFromConfig(CobaltCore.getInstance(), "cobalt.yml", "mongo-db-url");
                // mongoClient = new MongoClient(new MongoClientURI(url));
                // mongoDB = mongoClient.getDatabase((String) ConfigManager.getInstance().getFromConfig(CobaltCore.getInstance(), "cobalt.yml", "mongo-db-name"));
            }
        }

        // Load Database Fallback
        String databaseFallback = (String) ConfigManager.getInstance().getFromConfig(CobaltCore.getInstance(), "cobalt.yml", "database-fallback-implementation");

        switch (databaseFallback) {
            case "sqlite" -> {
                fallbackStorageType = StorageType.SQLITE;

                if (sqliteDb == null) {
                    sqliteDb = new SQLite(CobaltCore.getInstance());
                    sqliteDb.load();
                }
            }
        }
    }

    /**
     * Initializes all <code>Dao</code>'s.
     */
    private void initDao() {
        // SQLITE
        registerDao(new SystemDaoSQLite(), ISystemDao.class);

        registerDao(new MappingsDaoSQLite(), IMappingsDao.class);
        registerDao(new LocationDaoSQLite(), ILocationDao.class);
        registerDao(new ParticleGroupDaoSQLite(), IParticleGroupDao.class);
        registerDao(new ParticleStyleDaoSQLite(), IParticleStyleDao.class);
        registerDao(new PlayerDaoSQLite(), IPlayerDao.class);
        registerDao(new SettingDaoSQLite(), ISettingDao.class);
        registerDao(new TradesDaoSQLite(), ITradesDao.class);
        registerDao(new SoundAreaDaoSQLite(), ISoundAreaDao.class);
        registerDao(new CustomSpawnerDaoSQLite(), ICustomSpawnerDao.class);
        registerDao(new StructureDaoSQLite(), IStructureDao.class);
        registerDao(new ObjectStorageDaoSQLite(), IObjectStorageDao.class);
        registerDao(new PlayerDataStorageDaoSQLite(), IPlayerDataStorageDao.class);
    }

    @Override
    public void disable() {
        while (!sqlitePendingOperations.isEmpty()) {
            sqlitePendingOperations.poll().modifyDatabase(sqliteDb.getSQLConnection());
        }
    }

    // ----- GETTERS / SETTERS -----

    public Database getSqliteDb() {
        if (sqliteDb == null) {
            sqliteDb = new SQLite(CobaltCore.getInstance());
            sqliteDb.load();
        }
        return sqliteDb;
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static DataManager INSTANCE = null;
    /**
     * Returns the object representing this <code>DataManager</code>.
     *
     * @return The object of this class
     */

    @Deprecated
    public static DataManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new DataManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

}
