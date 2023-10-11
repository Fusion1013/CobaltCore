package se.fusion1013.plugin.cobaltcore.database.system;

import org.bukkit.Bukkit;
import org.yaml.snakeyaml.util.EnumUtils;
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
import se.fusion1013.plugin.cobaltcore.database.system.implementations.SQLiteImplementation;
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

    private static final IDataImplementation[] IMPLEMENTATIONS = {
      new SQLiteImplementation()
    };

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
     */
    public <T extends IDao> void registerDao(T dao, Class<?> parent) {
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
        // Load the database connection
        loadDatabaseConnection();

        // Initialize all the dao's
        initDao();
    }

    /**
     * Loads the database connection.
     */
    private void loadDatabaseConnection() {
        // Get types from config
        String databaseImplementation = (String) ConfigManager.getInstance().getFromConfig(CobaltCore.getInstance(), "cobalt.yml", "database-implementation");
        String databaseFallback = (String) ConfigManager.getInstance().getFromConfig(CobaltCore.getInstance(), "cobalt.yml", "database-fallback-implementation");

        // Convert to StorageType
        StorageType implementationType = EnumUtils.findEnumInsensitiveCase(StorageType.class, databaseImplementation);
        StorageType fallbackType = EnumUtils.findEnumInsensitiveCase(StorageType.class, databaseFallback);

        for (IDataImplementation implementation : IMPLEMENTATIONS) {
            // Load implementation
            if (implementation.type() == implementationType) {
                storageType = implementationType;
                implementation.loadConnection();
            }

            // Load fallback
            if (implementation.type() == fallbackType) {
                fallbackStorageType = fallbackType;
                implementation.loadConnection();
            }
        }
    }

    /**
     * Initializes all <code>Dao</code>'s.
     */
    private void initDao() {
        for (IDataImplementation implementation : IMPLEMENTATIONS) {
            implementation.initDao(this);
        }
    }

    @Override
    public void disable() {
        for (IDataImplementation implementation : IMPLEMENTATIONS) {
            implementation.onDisable();
        }
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
