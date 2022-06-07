package se.fusion1013.plugin.cobaltcore.world.sound;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.HashMap;
import java.util.Map;

public class SoundManager extends Manager {

    // ----- VARIABLES -----

    private static final Map<String, CustomSound> REGISTERED_CUSTOM_SOUNDS = new HashMap<>();

    // ----- REGISTER -----

    /**
     * Registers a new <code>CustomSound</code>.
     *
     * @param sound the <code>CustomSound</code> to register.
     * @return the <code>CustomSound</code>.
     */
    public static CustomSound register(CustomSound sound) {
        return REGISTERED_CUSTOM_SOUNDS.put(sound.sound, sound);
    }

    public static final CustomSound ETERNAL_HALLS = register(new CustomSound("cobalt.eternal_halls", 173000));

    // ----- GETTERS / SETTERS -----

    /**
     * Gets a <code>CustomSound</code> from the registered sounds.
     *
     * @param sound the sound to get.
     * @return the <code>CustomSound</code>.
     */
    public static CustomSound getSound(String sound) {
        return REGISTERED_CUSTOM_SOUNDS.get(sound);
    }

    // ----- CONSTRUCTORS -----

    public SoundManager(CobaltCore cobaltCore) {
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

    private static SoundManager INSTANCE = null;
    /**
     * Returns the object representing this <code>SoundManager</code>.
     *
     * @return The object of this class
     */
    public static SoundManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SoundManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
