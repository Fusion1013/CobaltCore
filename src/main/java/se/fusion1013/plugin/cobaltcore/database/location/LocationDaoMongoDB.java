package se.fusion1013.plugin.cobaltcore.database.location;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;

import java.util.UUID;

public class LocationDaoMongoDB extends Dao implements ILocationDao {

    MongoCollection<Document> collection;

    @Override
    public void removeLocation(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            collection.deleteOne(Filters.eq("uuid", uuid.toString()));
        });
    }

    @Override
    public void insertLocation(UUID uuid, Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            Document mongoLocation = new Document("uuid", uuid.toString())
                    .append("location", location);
            collection.insertOne(mongoLocation);
        });
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.MONGODB;
    }

    @Override
    public void init() {
        // DataManager.getInstance().getMongoDB().createCollection("location");
        // collection = DataManager.getInstance().getMongoDB().getCollection("location");
    }

}
