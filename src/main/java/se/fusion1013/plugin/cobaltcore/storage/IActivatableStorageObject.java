package se.fusion1013.plugin.cobaltcore.storage;

public interface IActivatableStorageObject extends IStorageObject {

    /**
     * Attempt to activate this <code>IActivatableStorageObject</code>.
     *
     * @param args optional arguments.
     */
    void activate(Object... args);

    /**
     * Attempt to deactivate this <code>IActivatableStorageObject</code>.
     *
     * @param args optional arguments.
     */
    void deactivate(Object... args);

    /**
     * True if this object is currently active.
     *
     * @return if this object is currently active.
     */
    boolean isActive();

}
