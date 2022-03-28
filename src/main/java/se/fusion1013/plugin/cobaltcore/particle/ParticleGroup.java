package se.fusion1013.plugin.cobaltcore.particle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.particle.styles.IParticleStyle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A particle group is a holder class for multiple particle styles.
 */
public class ParticleGroup implements Cloneable {

    // ----- VARIABLES -----

    UUID uuid;
    String name;
    private final List<ParticleStyle> particleStyleList;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>ParticleGroup</code> with a name and a random <code>UUID</code>.
     *
     * @param name the name of the group.
     */
    public ParticleGroup(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();

        this.particleStyleList = new ArrayList<>();
    }

    /**
     * Creates a new <code>ParticleGroup</code> with a name and a <code>UUID</code>.
     *
     * @param uuid the uuid of the group.
     * @param name the name of the group.
     */
    public ParticleGroup(UUID uuid, String name) {
        this.name = name;
        this.uuid = uuid;

        this.particleStyleList = new ArrayList<>();
    }

    /**
     * Creates a clone of the given <code>ParticleGroup</code>.
     * @param target the <code>ParticleGroup</code> object to clone.
     */
    public ParticleGroup(ParticleGroup target) {
        this.uuid = target.uuid;
        this.particleStyleList = target.particleStyleList;
    }

    // ----- PARTICLE DISPLAY -----

    /**
     * Displays all particles in the <code>ParticleGroup</code> in relation to one <code>Location</code>.
     *
     * @param location the location to center the <code>ParticleGroup</code> on.
     */
    public void display(Location location) {
        if (location == null) return;

        // Display the particle for all online players
        for (Player p : Bukkit.getOnlinePlayers()){
            for (IParticleStyle ps : particleStyleList){
                ParticleContainer[] particles = ps.getParticles(location);
                Object extra = ps.getExtra();

                // Display all particles in the style
                for (ParticleContainer particle : particles){
                    if (extra != null) p.spawnParticle(ps.getParticle(), particle.getLocation(), particle.getCount(), particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed(), extra);
                    else p.spawnParticle(ps.getParticle(), particle.getLocation(), particle.getCount(), particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed());
                }
            }
        }
    }

    /**
     * Displays all particles in the <code>ParticleGroup</code> in relation to two <code>Location</code>'s.
     *
     * @param location1 the first location.
     * @param location2 the second location.
     */
    public void display(Location location1, Location location2) {
        if (location1 == null || location2 == null) return;

        // Display the particle for all online players
        for (Player p : Bukkit.getOnlinePlayers()){
            for (IParticleStyle ps : particleStyleList){
                ParticleContainer[] particles = ps.getParticles(location1, location2);
                Object extra = ps.getExtra();

                // Display all particles in the style
                for (ParticleContainer particle : particles){
                    if (extra != null) p.spawnParticle(ps.getParticle(), particle.getLocation(), particle.getCount(), particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed(), extra);
                    else p.spawnParticle(ps.getParticle(), particle.getLocation(), particle.getCount(), particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed());
                }
            }
        }
    }

    // ----- GETTERS / SETTERS -----

    public int getParticleStyleCount() {
        if (particleStyleList != null) return particleStyleList.size();
        else return 0;
    }

    public String getName() {
        return name;
    }

    public void addParticleStyle(ParticleStyle style) {
        particleStyleList.add(style);
    }

    public void removeParticleStyle(int styleId) {
        if (styleId < 0 || styleId >= particleStyleList.size()) return;

        particleStyleList.remove(styleId);
    }

    // ----- CLONING -----

    @Override
    public ParticleGroup clone() {
        return new ParticleGroup(this);
    }
}
