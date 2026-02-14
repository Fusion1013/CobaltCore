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

import java.util.List;

public class ParticleEffectSphere extends AbstractParticleEffect implements IParticleEffect {

    private final DoubleVariable radius = new DoubleVariable("radius");
    private final IntVariable density = new IntVariable("density");

    public ParticleEffectSphere(Particle particle) {
        super("sphere", particle, new TransformationPipeline());
    }

    @Override
    public void display(Location center, Player player, TransformationPipeline extraPipeline) {
        List<Vector> points = ShapeUtils.generateSphere(radius.getValue(), density.getValue());
        display(center, player, particle.getParticle(), particle.getData(), points, extraPipeline);
    }

    @Override
    public List<Vector> getPoints() {
        return ShapeUtils.generateSphere(radius.getValue(), density.getValue());
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, radius, density};
    }
}
