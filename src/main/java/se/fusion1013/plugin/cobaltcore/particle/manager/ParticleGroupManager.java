package se.fusion1013.plugin.cobaltcore.particle.manager;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.SQLite;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleGroupManager extends Manager {

    // ----- VARIABLES -----

    private static Map<String, ParticleGroup> particleGroupMap = new HashMap<>();

    // ----- STYLE REMOVING -----

    /**
     * Removes a style from all <code>ParticleGroup</code>'s.
     * @param styleName the name of the <code>ParticleStyle</code>.
     * @return the number of styles that were removed.
     */
    public static int removeStyle(String styleName) {
        int count = 0;
        for (ParticleGroup group : particleGroupMap.values()) {
            count += group.removeParticleStyle(styleName);
        }
        return count;
    }

    // ----- GROUP REMOVING -----

    /**
     * Removes a <code>ParticleGroup</code>.
     *
     * @param name the name of the <code>ParticleGroup</code> to remove.
     * @return whether the group was removed or not.
     */
    public static boolean removeGroup(String name) {
        ParticleGroup group = particleGroupMap.remove(name);
        if (group != null) SQLite.removeParticleGroup(group.getUuid());
        return group != null;
    }

    // ----- GROUP INFO -----

    /**
     * Checks if a group with the given name exists.
     *
     * @param name the name of the group.
     * @return whether the group exists.
     */
    public static boolean groupExists(String name) {
        return particleGroupMap.get(name) != null;
    }

    /**
     * Gets a <code>ParticleGroup</code> registered under the given name.
     *
     * @param name the name of the group to get.
     * @return a <code>ParticleGroup</code>, or null if not found.
     */
    public static ParticleGroup getParticleGroup(String name) {
        return particleGroupMap.get(name);
    }

    /**
     * Gets an array of <code>ParticleGroup</code> names.
     *
     * @return an array of <code>ParticleGroup</code> names.
     */
    public static String[] getParticleGroupNames() {
        List<String> groupNames = new ArrayList<>();
        for (ParticleGroup group : particleGroupMap.values()) groupNames.add(group.getName());
        return groupNames.toArray(new String[0]);
    }

    // ----- GROUP DISPLAY -----

    /**
     * Displays a <code>ParticleGroup</code> at the given location.
     *
     * @param groupName the name of the group.
     * @param location the location to display the <code>ParticleGroup</code> at.
     * @return whether the <code>ParticleGroup</code> was successfully displayed or not.
     */
    public static boolean displayGroup(String groupName, Location location) {
        ParticleGroup group = particleGroupMap.get(groupName);
        if (group == null) return false;

        group.display(location);
        return true;
    }

    // ----- GROUP CREATION -----

    /**
     * Adds a <code>ParticleStyle</code> to a <code>ParticleGroup</code>.
     *
     * @param groupName the name of the <code>ParticleGroup</code>.
     * @param style the <code>ParticleStyle</code> to add to the <code>ParticleStyle</code>.
     * @return whether the <code>ParticleStyle</code> was successfully added.
     */
    public static boolean addParticleStyle(String groupName, ParticleStyle style) {
        ParticleGroup group = particleGroupMap.get(groupName);
        if (group == null) return false;

        if (!group.hasParticleStyle(style.getName())) {
            group.addParticleStyle(style);
            return true;
        } else return false;
    }

    /**
     * Creates a new <code>ParticleGroup</code> and stores it in the map.
     *
     * @param name the name of the <code>ParticleGroup</code>.
     * @return if the group was successfully inserted or not.
     */
    public static boolean createParticleGroup(String name) {
        if (groupExists(name)) return false;
        ParticleGroup group = new ParticleGroup(name);
        particleGroupMap.put(name, group);
        return true;
    }

    // ----- CONSTRUCTORS -----

    public ParticleGroupManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        particleGroupMap = SQLite.getParticleGroups();
    }

    @Override
    public void disable() {
        SQLite.insertParticleGroups(new ArrayList<>(particleGroupMap.values()));
    }
}
