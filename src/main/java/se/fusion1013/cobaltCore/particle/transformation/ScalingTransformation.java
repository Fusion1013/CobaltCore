package se.fusion1013.cobaltCore.particle.transformation;

import org.bukkit.util.Vector;

public class ScalingTransformation implements IParticleTransformation {

    private final double factor;

    public ScalingTransformation(double factor) {
        this.factor = factor;
    }

    @Override
    public Vector apply(Vector input) {
        return input.clone().multiply(factor);
    }
}
