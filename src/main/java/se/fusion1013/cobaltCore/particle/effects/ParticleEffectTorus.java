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

public class ParticleEffectTorus extends AbstractParticleEffect implements IParticleEffect {

    private final DoubleVariable majorRadius = new DoubleVariable("majorRadius");
    private final DoubleVariable minorRadius = new DoubleVariable("minorRadius");
    private final IntVariable density = new IntVariable("density");

    public ParticleEffectTorus(Particle particle) {
        super("torus", particle, new TransformationPipeline());
    }

    @Override
    public void display(Location center, Player player, TransformationPipeline extraPipeline) {
        List<Vector> points = getPoints();
        display(center, player, particle.getParticle(), points, extraPipeline);
    }

    @Override
    public List<Vector> getPoints() {
        return ShapeUtils.generateTorus(majorRadius.getValue(), minorRadius.getValue(), density.getValue());
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, majorRadius, minorRadius, density};
    }
}
