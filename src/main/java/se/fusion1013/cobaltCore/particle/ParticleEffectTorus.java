package se.fusion1013.cobaltCore.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;

import java.util.List;

public class ParticleEffectTorus extends AbstractParticleEffect implements IParticleEffect {

    private final double majorRadius;
    private final double minorRadius;
    private final int density;

    public ParticleEffectTorus(Particle particle, double majorRadius, double minorRadius, int density) {
        this(new TransformationPipeline(), particle, majorRadius, minorRadius, density);
    }

    public ParticleEffectTorus(TransformationPipeline pipeline, Particle particle, double majorRadius, double minorRadius, int density) {
        super(particle, pipeline);
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
        this.density = density;
    }

    @Override
    public void display(Location center, Player player) {
        List<Vector> points = getPoints();
        display(center, player, particle, points);
    }

    @Override
    public List<Vector> getPoints() {
        return ParticleShapeUtils.generateTorus(majorRadius, minorRadius, density);
    }

    @Override
    public String getName() {
        return "torus";
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectTorus(transformationPipeline, particle, majorRadius, minorRadius, density);
    }
}
