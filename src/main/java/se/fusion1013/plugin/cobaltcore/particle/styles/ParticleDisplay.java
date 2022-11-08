package se.fusion1013.plugin.cobaltcore.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleDisplay implements IDisplayInstance {

    // ----- VARIABLES -----

    private final Location location;
    private final Particle particle;
    private final double speed;
    private final double xOff, yOff, zOff;
    private final boolean directional;
    private final Object overrideData;
    private final int count;

    // ----- CONSTRUCTORS -----

    public ParticleDisplay(Location location, Particle particle, double xOff, double yOff, double zOff, double speed, int count, boolean directional, Object overrideData){
        this.location = location;
        this.particle = particle;
        this.xOff = xOff;
        this.yOff = yOff;
        this.zOff = zOff;
        this.speed = speed;
        this.directional = directional;
        this.overrideData = overrideData;
        this.count = count;
    }

    public ParticleDisplay(Location location, Particle particle, double xOff, double yOff, double zOff, double speed, int count, boolean directional){
        this(location, particle, xOff, yOff, zOff, speed, count, directional, null);
    }

    public ParticleDisplay(Location location, Particle particle, double xOff, double yOff, double zOff, double speed, int count){
        this(location, particle, xOff, yOff, zOff, speed, count,false, null);
    }

    public ParticleDisplay(Location location, Particle particle){
        this(location, particle, 0.0F, 0.0F, 0.0F, 0.0F, 1, false, null);
    }

    // ----- DISPLAY METHODS -----

    @Override
    public void display(Location location, Player... players) {

    }

    @Override
    public void display(Location location1, Location location2, Player... players) {

    }

    @Override
    public void display(Location location) {

    }

    @Override
    public void display(Location location1, Location location2) {
        // location1.getWorld().spawnParticle(particle, location, );
    }
}
