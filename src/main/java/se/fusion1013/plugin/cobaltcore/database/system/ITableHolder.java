package se.fusion1013.plugin.cobaltcore.database.system;

public interface ITableHolder { // TODO: Rename to a more fitting name

    /**
     * Creates all tables and views that this storage needs.
     */
    void initialize();

}
