package se.fusion1013.plugin.cobaltcore.storage;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageTestObject implements IStorageObject {

    // ----- VARIABLES -----

    private String name;
    private int id;
    private UUID uuid;
    private Location location;

    // ----- CONSTRUCTORS -----

    public StorageTestObject(String name, int id, UUID uuid) {
        this.name = name;
        this.id = id;
        this.uuid = uuid;
    }

    public StorageTestObject(String name, int id) {
        this.name = name;
        this.id = id;
        this.uuid = UUID.randomUUID();
    }

    private StorageTestObject() {}

    // ----- JSON STORAGE METHODS -----

    @Override
    public UUID getUniqueIdentifier() {
        return uuid;
    }

    @Override
    public void setUniqueIdentifier(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getObjectIdentifier() {
        return "storage_test";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("name", name);
        jo.addProperty("id", id);
        jo.addProperty("uuid", uuid.toString());
        jo.add("location", JsonUtil.toJson(location));
        return jo;
    }

    @Override
    public void fromJson(JsonObject json) {
        name = json.get("name").getAsString();
        id = json.get("id").getAsInt();
        uuid = UUID.fromString(json.get("uuid").getAsString());
        location = JsonUtil.toLocation(json.getAsJsonObject("location"));
    }

    // ----- COMMAND INTERACTION METHODS -----

    @Override
    public void fromCommandArguments(Object[] args) {
        name = (String) args[0];
        id = (int) args[1];
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
                new StringArgument("name"),
                new IntegerArgument("id")
        };
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        info.add("Name: " + name);
        info.add("ID: " + id);
        return info;
    }

    @Override
    public void setValue(String key, Object value) {
        switch (key) {
            case "name" -> name = (String) value;
            case "id" -> id = (int) value;
        }
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public StorageTestObject(StorageTestObject target) {
        this.name = target.name;
        this.id = target.id;
        this.uuid = target.uuid;
        this.location = target.location;
    }

    @Override
    public IStorageObject clone() {
        return new StorageTestObject(this);
    }
}
