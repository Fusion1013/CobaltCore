package se.fusion1013.plugin.cobaltcore.database.system;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.location.LocationDaoSQLite;
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
import se.fusion1013.plugin.cobaltcore.database.structure.IStructureDao;
import se.fusion1013.plugin.cobaltcore.database.structure.StructureDaoSQLite;
import se.fusion1013.plugin.cobaltcore.database.trades.ITradesDao;
import se.fusion1013.plugin.cobaltcore.database.trades.TradesDaoSQLite;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.HashMap;
import java.util.Map;

public class DataManager extends Manager {

    // ----- VARIABLES -----

    StorageType storageType;
    StorageType fallbackStorageType = StorageType.SQLITE; // TODO: Make setting
    Map<StorageType, Map<Class<?>, IDao>> daoImplementations = new HashMap<>();

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

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

        // MongoDB mongoDB = MongoDB.getInstance();
        // mongoDB.connect();

        // Load the database connection
        loadDatabaseConnection();

        // Initialize all the dao's
        initDao();
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

        registerDao(new LocationDaoSQLite(), ILocationDao.class);
        registerDao(new ParticleGroupDaoSQLite(), IParticleGroupDao.class);
        registerDao(new ParticleStyleDaoSQLite(), IParticleStyleDao.class);
        registerDao(new PlayerDaoSQLite(), IPlayerDao.class);
        registerDao(new SettingDaoSQLite(), ISettingDao.class);
        registerDao(new TradesDaoSQLite(), ITradesDao.class);
        registerDao(new SoundAreaDaoSQLite(), ISoundAreaDao.class);
        registerDao(new CustomSpawnerDaoSQLite(), ICustomSpawnerDao.class);
        registerDao(new StructureDaoSQLite(), IStructureDao.class);
    }

    @Override
    public void disable() {}

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
    public static DataManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new DataManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

}
