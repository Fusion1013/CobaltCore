package se.fusion1013.cobaltCore.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;

import java.util.List;

public class ParticleEffectSphere extends AbstractParticleEffect implements IParticleEffect {

    private final double radius;
    private final int density;

    public ParticleEffectSphere(Particle particle, double radius, int density) {
        this(new TransformationPipeline(), particle, radius, density);
    }

    public ParticleEffectSphere(TransformationPipeline pipeline, Particle particle, double radius, int density) {
        super(particle, pipeline);
        this.radius = radius;
        this.density = density;
    }

    @Override
    public void display(Location center, Player player) {
        List<Vector> points = ParticleShapeUtils.generateSphere(radius, density);
        display(center, player, particle, points);
    }

    @Override
    public List<Vector> getPoints() {
        return ParticleShapeUtils.generateSphere(radius, density);
    }

    @Override
    public String getName() {
        return "sphere";
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectSphere(transformationPipeline, particle, radius, density);
    }
}
