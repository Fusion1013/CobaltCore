package se.fusion1013.cobaltCore.database.system.implementations;

import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.database.player.IPlayerDao;
import se.fusion1013.cobaltCore.database.player.PlayerDaoSQLite;
import se.fusion1013.cobaltCore.database.system.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQLiteImplementation implements IDataImplementation {

    private static Database sqliteDb;
    private static final Lock sqliteOperationLock = new ReentrantLock();

    @Override
    public void loadConnection() {
        if (sqliteDb == null) {
            sqliteDb = new SQLite(CobaltCore.getInstance());
            sqliteDb.load();
        }
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
