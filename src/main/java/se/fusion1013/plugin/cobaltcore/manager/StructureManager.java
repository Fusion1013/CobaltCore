package se.fusion1013.plugin.cobaltcore.manager;

import se.fusion1013.plugin.cobaltcore.CobaltCore;

/**
 * The structure manager handles creation and storage of structures.
 */
public class StructureManager extends Manager {

    // ----- CONSTRUCTORS -----

    public StructureManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static StructureManager INSTANCE = null;
    /**
     * Returns the object representing this <code>StructureManager</code>.
     *
     * @return The object of this class
     */
    public static StructureManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new StructureManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

}
