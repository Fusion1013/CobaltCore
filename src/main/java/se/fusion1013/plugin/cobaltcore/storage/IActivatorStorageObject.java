package se.fusion1013.plugin.cobaltcore.storage;

import java.util.UUID;

/**
 * An object that is capable of activating <code>IActivatableStorageObject</code>'s.
 */
public interface IActivatorStorageObject extends IStorageObject {

    void addActivatable(UUID objectUUID);

    void removeActivatable(UUID objectUUID);

}
