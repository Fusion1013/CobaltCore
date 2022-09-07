package se.fusion1013.plugin.cobaltcore.database.storage;

import com.google.gson.JsonObject;
import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;

import java.util.Map;
import java.util.UUID;

public interface IObjectStorageDao extends IDao {

    /**
     * Removes a <code>IJsonStorage</code> from the database asynchronously.
     *
     * @param uuid the <code>UUID</code> of the <code>IJsonStorage</code>.
     */
    void removeJsonStorageAsync(UUID uuid);

    /**
     * Removes a <code>IJsonStorage</code> from the database synchronously.
     *
     * @param uuid the <code>UUID</code> of the <code>IJsonStorage</code>.
     */
    void removeJsonStorageSync(UUID uuid);

    /**
     * Inserts a <code>IJsonStorage</code> into the database asynchronously.
     *
     * @param uuid the <code>UUID</code> of the <code>IJsonStorage</code>.
     * @param chunkWorldKey the chunk world key of the <code>IJsonStorage</code>.
     * @param storage the <code>IJsonStorage</code>.
     */
    void insertJsonStorageAsync(UUID uuid, String chunkWorldKey, IStorageObject storage);

    /**
     * Inserts a <code>IJsonStorage</code> into the database synchronously.
     *
     * @param uuid the <code>UUID</code> of the <code>IJsonStorage</code>.
     * @param chunkWorldKey the chunk world key of the <code>IJsonStorage</code>.
     * @param storage the <code>IJsonStorage</code>.
     */
    void insertJsonStorageSync(UUID uuid, String chunkWorldKey, IStorageObject storage);

    /**
     * Gets all storage objects in the given chunk.
     *
     * @param chunkWorldKey the chunk world key.
     * @return a map of object identifiers & Json data
     */
    Map<String, JsonObject> getJsonStorageInChunk(String chunkWorldKey);

    @Override
    default String getId() { return "json_storage"; }

}
