package se.fusion1013.plugin.cobaltcore.particle.styles;

import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;

import java.util.List;

public interface IParticleStyle {

    // ----- INTERNALS -----

    String getInternalName();
    String getName();
    void setName(String name);
    boolean isEnabled();

    // ----- PARTICLE GETTING -----

    /**
     * Gets the <code>Particle</code> that this style is using.
     *
     * @return a <code>Particle</code>.
     */
    Particle getParticle();

    /**
     * Gets the particles for the style, in relation to one location.
     *
     * @param location the center location of the <code>ParticleStyle</code>.
     * @return an array of <code>ParticleContainers</code>.
     */
    ParticleContainer[] getParticles(Location location);

    /**
     * Gets the particles for the style, in relation to two locations.
     * Only some styles will make use of this secondary location, while some need it to function properly. (ex. ParticleStyleLine).
     *
     * @param location1 the center location of the <code>ParticleStyle</code>.
     * @param location2 the secondary position of the <code>ParticleStyle</code>.
     * @return an array of <code>ParticleContainers</code>.
     */
    ParticleContainer[] getParticles(Location location1, Location location2);

    /**
     * Gets extra data.
     *
     * @return object containing extra data.
     */
    Object getExtra();

    // ----- PARTICLE SETTING -----

    void setParticle(Particle particle);

    void setOffset(Vector offset);

    void setCount(int count);

    void setSpeed(double speed);

    void setExtra(Object extra);

    // ----- EXTRA STYLE SETTINGS -----

    Argument[] getExtraSettingsArguments();

    void setExtraSettings(Object[] args);

    // ----- INFO -----

    List<String> getInfoStrings();

}
