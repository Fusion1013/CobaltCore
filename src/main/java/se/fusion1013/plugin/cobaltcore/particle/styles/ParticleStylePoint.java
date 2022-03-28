package se.fusion1013.plugin.cobaltcore.particle.styles;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;

import java.util.ArrayList;
import java.util.List;

public class ParticleStylePoint extends ParticleStyle implements IParticleStyle, Cloneable {

    // ----- CONSTRUCTORS -----

    public ParticleStylePoint(String name) {
        super("point", name);
    }

    /**
     * Creates a clone of a <code>ParticleStylePoint</code>.
     *
     * @param target the <code>ParticleStylePoint</code> to clone.
     */
    public ParticleStylePoint(ParticleStylePoint target) {
        super(target);
    }

    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        List<ParticleContainer> particles = new ArrayList<>();
        particles.add(new ParticleContainer(location.clone(), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        return particles.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.point.info"));
        return info;
    }

    // ----- CLONE -----

    /**
     * Creates a clone of the object.
     *
     * @return a clone of the object.
     */
    @Override
    public ParticleStylePoint clone() {
        return new ParticleStylePoint(this);
    }
}
