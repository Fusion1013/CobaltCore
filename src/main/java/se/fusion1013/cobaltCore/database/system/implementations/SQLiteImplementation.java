package se.fusion1013.cobaltCore.database.system.implementations;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.database.player.IPlayerDao;
import se.fusion1013.cobaltCore.database.player.PlayerDaoSQLite;
import se.fusion1013.cobaltCore.database.system.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQLiteImplementation implements IDataImplementation {

    private static ConnectionSource connectionSource;
    private static final String DATABASE_URL = "jdbc:sqlite:plugins/CobaltCore/cobalt.db";

    private static Database sqliteDb;
    private static final Lock sqliteOperationLock = new ReentrantLock();

    @Override
    public void loadConnection() {
        if (sqliteDb == null) {
            sqliteDb = new SQLite(CobaltCore.getInstance());
            sqliteDb.load();
        }
    }

    private static void init() {
        try {
            if (connectionSource == null) {
                connectionSource = new JdbcConnectionSource(DATABASE_URL);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SQLite connection", e);
        }
    }

    public static ConnectionSource getConnectionSource() {
        if (connectionSource == null) {
            init();
        }
        return connectionSource;
    }

    private static void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        close();
    }

    @Override
    public DataStorageType type() {
        return DataStorageType.SQLITE;
    }

    @Override
    public void initDao(DataManager manager) {
        // Register all dao's using manager.registerDao()

        manager.registerDao(new SystemDaoSQLite(), ISystemDao.class);
        manager.registerDao(new PlayerDaoSQLite(), IPlayerDao.class);
    }

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

    public static Database getSqliteDb() {
        if (sqliteDb == null) {
            sqliteDb = new SQLite(CobaltCore.getInstance());
            sqliteDb.load();
        }
        return sqliteDb;
    }
}
