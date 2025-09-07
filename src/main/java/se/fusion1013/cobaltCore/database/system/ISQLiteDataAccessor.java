package se.fusion1013.cobaltCore.database.system;

import java.sql.Connection;

public interface ISQLiteDataAccessor {

    void modifyDatabase(Connection connection);

}
