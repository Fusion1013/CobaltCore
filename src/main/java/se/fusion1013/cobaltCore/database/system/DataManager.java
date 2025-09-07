package se.fusion1013.cobaltCore.database.system;

import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltCore.manager.Manager;

import java.util.HashMap;
import java.util.Map;

public class DataManager extends Manager<CobaltCore> {

    private DataStorageType dataStorageType;
    private DataStorageType fallbackDataStorageType = DataStorageType.SQLITE;
    private static final Map<DataStorageType, Map<Class<?>, IDao>> daoImplementations = new HashMap<>();

    private static DataManager INSTANCE;

    public static DataManager getInstance() {
        return INSTANCE;
    }

    private static final IDataImplementation[] DATA_IMPLEMENTATIONS = {
            new SQLiteImplementation()
    };

    public DataManager(CobaltCore plugin) {
        super(plugin);
        INSTANCE = this;
    }

    public <T extends IDao> void registerDao(T dao, Class<?> parent) {
        // Adds the dao to the map
        DataStorageType type = dao.getDataStorageType();
        daoImplementations.computeIfAbsent(type, k -> new HashMap<>());
        daoImplementations.get(type).put(parent, dao);

        // Initializes the dao
        dao.init();

        // Updates the dao if needed
        ISystemDao system = getDao(ISystemDao.class);
        dao.update(system.getVersion(dao.getId() + dao.getDataStorageType().name(), dao.getVersion()));
        system.setVersion(dao.getId() + dao.getDataStorageType().name(), dao.getVersion());
    }

    @SuppressWarnings("unchecked")
    public <T extends IDao> T getDao(Class<T> dao) {
        // Find the correct storage pair
        Map<Class<?>, IDao> storage = daoImplementations.get(dataStorageType);
        if (storage == null) {
            CobaltCore.getInstance().getLogger().warning("Storage of type " + dataStorageType.name() + " not found, using fallback");
            storage = daoImplementations.get(fallbackDataStorageType);

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

    @Override
    public void reload() {
        loadDatabaseConnection();
        initDao();
    }

    private void loadDatabaseConnection() {
        DataStorageType implementationType = DataStorageType.SQLITE;

        for (IDataImplementation implementation : DATA_IMPLEMENTATIONS) {
            // Load implementation
            if (implementation.type() == implementationType) {
                dataStorageType = implementationType;
                implementation.loadConnection();
            }
        }
    }

    private void initDao() {
        for (IDataImplementation implementation : DATA_IMPLEMENTATIONS) {
            implementation.initDao(this);
        }
    }

    @Override
    public void disable() {
        for (IDataImplementation implementation : DATA_IMPLEMENTATIONS) {
            implementation.onDisable();
        }
    }
}
