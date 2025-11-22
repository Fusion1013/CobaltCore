package se.fusion1013.cobaltCore.particle.transformation;

import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.ParticleShapeUtils;

public class WiggleTransformation implements IParticleTransformation {

    private double amplitude = 0.4;
    private double frequency = 1.2;

    @Override
    public Vector apply(Vector input) {
        return ParticleShapeUtils.applyPositionWiggle(amplitude, frequency, input, System.currentTimeMillis());
    }
}
