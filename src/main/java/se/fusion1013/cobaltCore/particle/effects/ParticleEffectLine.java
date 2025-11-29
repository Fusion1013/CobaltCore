package se.fusion1013.cobaltCore.particle.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.LocationVariable;

import java.util.List;

public class ParticleEffectLine extends AbstractParticleEffect implements IParticleEffect {

    private final IntVariable density = new IntVariable("density");
    private final LocationVariable targetLocation = new LocationVariable("targetLocation");
    private final DoubleVariable randomRange = new DoubleVariable("randomRange")
            .optional();

    public ParticleEffectLine(Particle particle) {
        super("line", particle, new TransformationPipeline());
    }

    @Override
    public void display(Location center, Player player) {
        List<Vector> points = getPoints(center);
        display(center, player, particle.getParticle(), points);
    }

    @Override
    public List<Vector> getPoints() {
        return List.of();
    }

    @Override
    public List<Vector> getPoints(Location center) {
        return ShapeUtils.generateLine(center.toVector(), targetLocation.getValue().toVector(), density.getValue()); // TODO: Add random range
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, density, targetLocation, randomRange};
    }
}
