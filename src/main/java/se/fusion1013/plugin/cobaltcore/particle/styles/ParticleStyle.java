package se.fusion1013.plugin.cobaltcore.particle.styles;

import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.List;

public abstract class ParticleStyle implements IParticleStyle, Cloneable {

    // ----- VARIABLES -----

    // Internals
    String internalStyleName;
    boolean enabled = true;

    // Display
    String name;

    // Particle
    Particle particle = Particle.FLAME;
    Vector offset = new Vector(0, 0, 0);
    int count = 1;
    double speed = 0;
    Object extra = null;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>ParticleStyle</code> with the given parameters.
     * @param internalStyleName the internal name of the <code>ParticleStyle</code>.
     * @param name the name of the <code>ParticleStyle</code>.
     * @param particle the <code>Particle</code> the style uses.
     * @param offset the offset of the <code>ParticleStyle</code>.
     * @param count the particle count.
     * @param speed the particle speed.
     * @param extra extra particle information. Set to null if not used.
     */
    public ParticleStyle(String internalStyleName, String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        this.internalStyleName = internalStyleName;
        this.name = name;
        this.particle = particle;
        this.offset = offset;
        this.count = count;
        this.speed = speed;
        this.extra = extra;
    }

    /**
     * Creates a new <code>ParticleStyle</code> with an internal name.
     *
     * @param internalStyleName the internal name of the particle style.
     * @param name the name of the particle style.
     */
    public ParticleStyle(String internalStyleName, String name) {
        this.internalStyleName = internalStyleName;
        this.name = name;
    }

    /**
     * Creates a clone of a <code>ParticleStyle</code>.
     *
     * @param target the <code>ParticleStyle</code> to clone.
     */
    public ParticleStyle(ParticleStyle target) {
        if (target == null) return;

        this.internalStyleName = target.internalStyleName;
        this.name = target.name;
        this.enabled = target.enabled;

        this.particle = target.particle;
        this.offset = target.offset.clone();
        this.count = target.count;
        this.speed = target.speed;
        this.extra = target.extra;
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("name", name)
                .addPlaceholder("particle", particle.name().toLowerCase())
                .addPlaceholder("offset", offset)
                .addPlaceholder("count", count)
                .addPlaceholder("speed", speed)
                .build();

        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.abstract.info", placeholders));
        return info;
    }

    // ----- EXTRA SETTINGS -----

    @Override
    public Argument[] getExtraSettingsArguments() {
        return new Argument[0];
    }

    @Override
    public void setExtraSettings(Object[] args) { }


    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticles(Location location) {
        return new ParticleContainer[0];
    }

    @Override
    public ParticleContainer[] getParticles(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getInternalName() {
        return internalStyleName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Particle getParticle() {
        return particle;
    }

    @Override
    public Object getExtra() {
        return extra;
    }

    @Override
    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    @Override
    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public void setExtra(Object extra) {
        this.extra = extra;
    }

    // ----- CLONING -----

    public abstract ParticleStyle clone();

    /**
     * Creates a clone of a list of <code>ParticleStyle</code>'s.
     *
     * @param list a list of <code>ParticleStyle</code>'s.
     * @return a list of <code>ParticleStyle</code>'s.
     */
    public static List<ParticleStyle> cloneList(List<ParticleStyle> list) {
        if (list == null) return new ArrayList<>();
        List<ParticleStyle> clone = new ArrayList<>(list.size());
        for (ParticleStyle item : list) clone.add(item.clone());
        return clone;
    }
}
