package se.fusion1013.plugin.cobaltcore.storage;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public interface IStorageObject {

    // ----- GETTERS / SETTERS -----

    /**
     * Gets the unique identifier <code>UUID</code> of this object.
     *
     * @return a <code>UUID</code>.
     */
    UUID getUniqueIdentifier();

    /**
     * Sets the unique identifier <code>UUID</code> for this object.
     */
    void setUniqueIdentifier(UUID uuid);

    /**
     * Gets the object identifier string. This is unique to a specific group of the same objects.
     *
     * @return the object identifier string.
     */
    String getObjectIdentifier();

    /**
     * Gets the <code>Location</code> of this object.
     *
     * @return the <code>Location</code>.
     */
    Location getLocation();

    /**
     * Sets the <code>Location</code> of this object.
     *
     * @param location the <code>Location</code> to set.
     */
    void setLocation(Location location);

    // ----- JSON INTERACTION METHODS -----

    /**
     * Converts the object ot a <code>JsonObject</code>.
     *
     * @return a <code>JsonObject</code>.
     */
    JsonObject toJson();

    /**
     * Populates the object using the given <code>JsonObject</code>.
     *
     * @param json the <code>JsonObject</code>.
     */
    void fromJson(JsonObject json);

    // ----- COMMAND INTERACTION METHODS -----

    void fromCommandArguments(Object[] args);

    Argument<?>[] getCommandArguments();

    /**
     * Gets commands used for operations on lists.
     *
     * @return command arguments.
     */
    default Argument<?>[] getListCommandArguments() {
        return new Argument[0];
    }

    default void addItem(String key, Object value) {}

    default void removeItem(String key, Object value) {}

    void setValue(String key, Object value); // TODO: Add methods for interacting with arrays / lists on objects

    // ----- INFO GETTING -----

    List<String> getInfoStrings();

    // ----- LOAD / UNLOAD METHODS -----

    /**
     * Called when this object is loaded.
     */
    default void onLoad() {}

    /**
     * Called when this object is unloaded.
     */
    default void onUnload() {}

    // ----- TRIGGER -----

    /**
     * Can be implemented by plugins to execute specific triggers.
     */
    default void onTrigger(Object... args) {}

    // ----- CLONE METHOD -----

    /**
     * Creates a clone of this object.
     *
     * @return a clone of this object.
     */
    IStorageObject clone();

}
