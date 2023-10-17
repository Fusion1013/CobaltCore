package se.fusion1013.plugin.cobaltcore.database.location;

import org.bukkit.Location;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class LocationJsonDao implements ILocationDao {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File jsonFile;

    public LocationJsonDao(File jsonFile) {
        this.jsonFile = jsonFile; // Initialize the jsonFile variable
    }
    @Override
    public void init() {
        // Implement initialization logic here
        // This method is called when your data storage implementation is initialized.
    }
    @Override
    public void removeLocation(UUID uuid) {
        Map<UUID, Location> locations = getAllLocations();
        locations.remove(uuid);
        saveLocations(locations);
    }

    @Override
    public void removeLocationSync(UUID uuid) {
        removeLocation(uuid);
    }

    @Override
    public void insertLocation(UUID uuid, Location location) {
        Map<UUID, Location> locations = getAllLocations();
        locations.put(uuid, location);
        saveLocations(locations);
    }

    @Override
    public void insertLocationSync(UUID uuid, Location location) {
        insertLocation(uuid, location);
    }

    @Override
    public String getId() {
        return "location";
    }
    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.JSON; // You can return the appropriate storage type
    }
    private Map<UUID, Location> getAllLocations() {
        if (jsonFile.exists()) {
            try {
                return objectMapper.readValue(jsonFile, new TypeReference<Map<UUID, Location>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    private void saveLocations(Map<UUID, Location> locations) {
        try {
            objectMapper.writeValue(jsonFile, locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
