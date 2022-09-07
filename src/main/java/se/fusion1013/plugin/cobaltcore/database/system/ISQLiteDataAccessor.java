package se.fusion1013.plugin.cobaltcore.database.system;

import java.sql.Connection;

public interface ISQLiteDataAccessor {

    void modifyDatabase(Connection connection);

}
