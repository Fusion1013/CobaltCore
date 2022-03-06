package se.fusion1013.plugin.cobaltcore.particle.manager;

import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleStyleManager extends Manager {

    // ----- VARIABLES -----

    private static Map<String, ParticleStyle> particleStyleList = new HashMap<>(); // Holds particle styles that the player has created through the in-game command. <name, style>.
    private static Map<String, ParticleStyle> registeredParticleStyles = new HashMap<>(); // Holds all registered, default particle styles. <internalName, style>

    // ----- STYLE INFO -----

    /**
     * Checks if a style with the given name exists.
     *
     * @param name the name to check for.
     * @return whether the style exists.
     */
    public static boolean styleExists(String name) {
        return particleStyleList.get(name) != null;
    }

    /**
     * Gets a <code>ParticleStyle</code> registered under the given name.
     * @param name the name of the style to get.
     * @return a <code>ParticleStyle</code>, or null if not found.
     */
    public static ParticleStyle getParticleStyle(String name) {
        return particleStyleList.get(name);
    }

    /**
     * Gets all internal style names for all registered <code>ParticleStyle</code>'s.
     *
     * @return an array of internal style names.
     */
    public static String[] getInternalParticleStyleNames() {
        List<String> styleNames = new ArrayList<>();
        for (ParticleStyle style : registeredParticleStyles.values()) styleNames.add(style.getInternalName());
        return styleNames.toArray(new String[0]);
    }

    /**
     * Gets an array of <code>ParticleStyle</code> names, of the given style type.
     *
     * @param internalStyleName the internal style name of the <code>ParticleStyle</code> to get the names of.
     * @return an array of <code>ParticleStyle</code> names.
     */
    public static String[] getParticleStyleNames(String internalStyleName) {
        List<String> styleNames = new ArrayList<>();
        for (ParticleStyle style : particleStyleList.values()) {
            if (style.getInternalName().equalsIgnoreCase(internalStyleName)) styleNames.add(style.getName());
        }
        return styleNames.toArray(new String[0]);
    }

    public static String[] getParticleStyleNames() {
        List<String> styleNames = new ArrayList<>();
        for (ParticleStyle style : particleStyleList.values()) styleNames.add(style.getName());
        return styleNames.toArray(new String[0]);
    }

    // ----- STYLE DELETION -----

    /**
     * Deletes the <code>ParticleStyle</code> with the given name.
     * @param styleName the name of the <code>ParticleStyle</code>.
     * @return true if the <code>ParticleStyle</code> was deleted.
     */
    public static boolean deleteStyle(String styleName) {
        if (particleStyleList.remove(styleName) == null) return false;
        else return true;
    }

    // ----- STYLE CREATION -----

    /**
     * Creates a new <code>ParticleStyle</code> and stores it in the map.
     *
     * @param internalStyleName the internal name of the style.
     * @param name the name of the style.
     * @param particle the particle that the style uses.
     * @param offset the offset of the style.
     * @param count the particle count.
     * @param speed the speed of the particle.
     * @param extra extra particle information.
     * @return if the style was successfully inserted or not.
     */
    public static boolean createParticleStyle(String internalStyleName, String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        ParticleStyle style = getDefaultParticleStyle(internalStyleName);
        if (style == null) return false;

        // Create clone of the default style and edit its values to match the given ones
        style = style.clone();
        style.setName(name);
        style.setParticle(particle);
        style.setOffset(offset);
        style.setCount(count);
        style.setSpeed(speed);
        style.setExtra(extra);

        particleStyleList.put(name, style);
        return true;
    }

    /**
     * Gets a default <code>ParticleStyle</code> from the registered styles.
     *
     * @param internalStyleName the internal name of the style to get.
     * @return the <code>ParticleStyle</code>.
     */
    public static ParticleStyle getDefaultParticleStyle(String internalStyleName) {
        return registeredParticleStyles.get(internalStyleName);
    }

    // ----- REGISTER -----

    public static final ParticleStyle PARTICLE_STYLE_POINT = register(new ParticleStylePoint("default_point"));
    public static final ParticleStyle PARTICLE_STYLE_SPHERE = register(new ParticleStyleSphere("default_sphere"));

    /**
     * Registers a new <code>ParticleStyle</code>.
     *
     * @param style the <code>ParticleStyle</code> to register.
     * @return the <code>ParticleStyle</code>.
     */
    private static ParticleStyle register(ParticleStyle style) {
        registeredParticleStyles.put(style.getInternalName(), style);
        return style;
    }

    // ----- CONSTRUCTORS -----

    public ParticleStyleManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING ------

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static ParticleStyleManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ParticleManager</code>.
     *
     * @return The object of this class
     */
    public static ParticleStyleManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ParticleStyleManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}