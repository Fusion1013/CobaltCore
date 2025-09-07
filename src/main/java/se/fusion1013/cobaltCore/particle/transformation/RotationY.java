package se.fusion1013.cobaltCore.particle.transformation;

import org.bukkit.util.Vector;

public class RotationY implements IParticleTransformation {

    private final double angle;

    public RotationY(double angle) {
        this.angle = angle;
    }

    @Override
    public Vector apply(Vector input) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double x = input.getX() * cos - input.getZ() * sin;
        double z = input.getX() * sin + input.getZ() * cos;

        return new Vector(x, input.getY(), z);
    }
}
