package se.fusion1013.cobaltCore.particle.transformation;

import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class Translation implements IParticleTransformation {

    private final Vector offset;

    public Translation(Vector offset) {
        this.offset = offset;
    }

    @Override
    public Vector apply(Vector input) {
        return input.clone().add(offset);
    }
}
