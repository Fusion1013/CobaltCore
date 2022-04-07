package se.fusion1013.plugin.cobaltcore.particle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.particle.styles.IParticleStyle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;

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
    private final List<ParticleStyleHolder> particleStyleList;

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
        this.name = target.name;
        this.particleStyleList = target.particleStyleList;
    }

    // ----- PARTICLE STYLE INFO -----

    /**
     * Gets an array of particle style names contained within this group.
     *
     * @return an array of particle style names.
     */
    public String[] getParticleStyleNames() {
        String[] names = new String[particleStyleList.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = particleStyleList.get(i).style.getName();
        }
        return names;
    }

    // ----- PARTICLE STYLE EDITING -----

    public void setStyleOffset(String styleName, Vector offset) {
        ParticleStyleHolder holder = getHolder(styleName);
        if (holder == null) return;
        holder.offset = offset;
    }

    public void setStyleRotation(String styleName, Vector rotation, Vector rotationSpeed) {
        ParticleStyleHolder holder = getHolder(styleName);
        if (holder == null) return;
        holder.rotation = rotation;
        holder.rotationSpeed = rotationSpeed;
    }

    private ParticleStyleHolder getHolder(String styleName) {
        for (ParticleStyleHolder holder : particleStyleList) if (holder.style.getName().equalsIgnoreCase(styleName)) return holder;
        return null;
    }

    // ----- PARTICLE DISPLAY -----

    /**
     * Displays all particles in the <code>ParticleGroup</code> in relation to one <code>Location</code>, to the given players.
     * @param location the location to center the <code>ParticleGroup</code> on.
     * @param players the players to display the <code>ParticleGroup</code> to.
     */
    public void display(Location location, Player... players) {
        if (location == null) return;

        // Display the particle for all online players
        for (Player p : players){
            for (ParticleStyleHolder psh : particleStyleList){
                ParticleContainer[] particles = psh.getParticles(location);
                Object extra = psh.style.getExtra();

                // Display all particles in the style
                for (ParticleContainer particle : particles){
                    if (extra != null) p.spawnParticle(psh.style.getParticle(), particle.getLocation(), particle.getCount(), particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed(), extra);
                    else p.spawnParticle(psh.style.getParticle(), particle.getLocation(), particle.getCount(), particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed());
                }
            }
        }
    }

    /**
     * Displays all particles in the <code>ParticleGroup</code> in relation to two <code>Location</code>'s, to the given players.
     *
     * @param location1 the first location.
     * @param location2 the second location.
     * @param players the players to display the <code>ParticleGroup</code> to.
     */
    public void display(Location location1, Location location2, Player... players) {
        if (location1 == null || location2 == null) return;

        // Display the particle for all online players
        for (Player p : players){
            for (ParticleStyleHolder psh : particleStyleList) {
                ParticleContainer[] particles = psh.getParticles(location1, location2);
                Object extra = psh.style.getExtra();

                // Display all particles in the style
                for (ParticleContainer particle : particles){
                    if (extra != null) p.spawnParticle(psh.style.getParticle(), particle.getLocation(), particle.getCount(), particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed(), extra);
                    else p.spawnParticle(psh.style.getParticle(), particle.getLocation(), particle.getCount(), particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed());
                }
            }
        }
    }

    /**
     * Displays all particles in the <code>ParticleGroup</code> in relation to one <code>Location</code>.
     *
     * @param location the location to center the <code>ParticleGroup</code> on.
     */
    public void display(Location location) {
        display(location, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    /**
     * Displays all particles in the <code>ParticleGroup</code> in relation to two <code>Location</code>'s.
     *
     * @param location1 the first location.
     * @param location2 the second location.
     */
    public void display(Location location1, Location location2) {
        display(location1, location2, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    // ----- GETTERS / SETTERS -----

    public boolean hasParticleStyle(String styleName) {
        for (ParticleStyleHolder holder : particleStyleList) {
            if (holder.style.getName().equalsIgnoreCase(styleName)) return true;
        }
        return false;
    }

    public int getParticleStyleCount() {
        if (particleStyleList != null) return particleStyleList.size();
        else return 0;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<ParticleStyleHolder> getParticleStyleList() {
        return particleStyleList;
    }

    public void addParticleStyle(ParticleStyle style) {
        particleStyleList.add(new ParticleStyleHolder(style, new Vector(0, 0, 0)));
    }

    public int removeParticleStyle(String styleName) {
        int count = 0;
        for (int i = 0; i < particleStyleList.size(); i++) {
            ParticleStyleHolder holder = particleStyleList.get(i);
            if (holder.style.getName().equalsIgnoreCase(styleName)) {
                particleStyleList.remove(i);
                i--;
                count++;
            }
        }
        return count;
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

    // ----- HOLDER CLASS -----

    public static class ParticleStyleHolder {

        // ----- VARIABLES -----

        public ParticleStyle style;
        public Vector offset;
        public Vector rotation = new Vector(0, 0, 0);
        public Vector rotationSpeed = new Vector(0, 0, 0);

        // ----- CONSTRUCTOR -----

        public ParticleStyleHolder(ParticleStyle style, Vector offset) {
            this.style = style;
            this.offset = offset;
        }

        // ----- PARTICLE GETTING -----

        public ParticleContainer[] getParticles(Location location) {
            rotation.add(rotationSpeed);
            return offsetParticles(rotateParticles(style.getParticles(location), location));
        }

        // TODO: Add option for not rotating style with group

        public ParticleContainer[] getParticles(Location location1, Location location2) {
            rotation.add(rotationSpeed);
            return offsetParticles(rotateParticles(style.getParticles(location1, location2), location1));
        }

        // ----- PARTICLE OFFSETTING / ROTATING -----

        private ParticleContainer[] offsetParticles(ParticleContainer[] particles) {
            Vector rotatedOffset = VectorUtil.rotateVector(offset.clone(), rotation.getX(), rotation.getY(), rotation.getZ());
            for (ParticleContainer p : particles) p.getLocation().add(rotatedOffset);
            return particles;
        }

        private ParticleContainer[] rotateParticles(ParticleContainer[] particles, Location center) {
            for (ParticleContainer particleContainer : particles) {
                Vector current = particleContainer.getLocation().toVector();
                Vector delta = new Vector(current.getX() - center.getX(), current.getY() - center.getY(), current.getZ() - center.getZ());
                VectorUtil.rotateVector(delta, rotation.getX(), rotation.getY(), rotation.getZ());
                Vector newPos = new Vector(delta.getX() + center.getX(), delta.getY() + center.getY(), delta.getZ() + center.getZ());
                particleContainer.setLocationPosition(newPos);
            }
            return particles;
        }

    }

}
