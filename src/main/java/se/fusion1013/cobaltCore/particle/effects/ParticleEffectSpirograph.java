package se.fusion1013.cobaltCore.particle.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltCore.variable.BooleanVariable;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;

import java.util.List;

public class ParticleEffectSpirograph extends AbstractParticleEffect implements IParticleEffect {

    private final DoubleVariable fixedCircleRadius = new DoubleVariable("fixedCircleRadius");
    private final DoubleVariable rollingCircleRadius = new DoubleVariable("rollingCircleRadius");
    private final DoubleVariable drawingPointOffset = new DoubleVariable("drawingPointOffset");
    private final IntVariable points = new IntVariable("points");
    private final BooleanVariable epitrochoid = new BooleanVariable("epitrochoid")
            .optional();
    private final DoubleVariable animationSpeed = new DoubleVariable("animationSpeed")
            .optional();
    private final DoubleVariable height = new DoubleVariable("height")
            .optional();

    public ParticleEffectSpirograph(Particle particle) {
        super("spirograph", particle, new TransformationPipeline());
    }

    @Override
    public void display(Location center, Player player, TransformationPipeline extraPipeline) {
        display(center, player, particle.getParticle(), particle.getData(), getPoints(), extraPipeline);
    }

    @Override
    public List<Vector> getPoints() {
        return ShapeUtils.generateSpirograph(
                fixedCircleRadius.getValue(),
                rollingCircleRadius.getValue() + Math.sin(System.currentTimeMillis() * animationSpeed.getValue()),
                drawingPointOffset.getValue(),
                points.getValue(),
                epitrochoid.getValue(),
                animationSpeed.getValue() * System.currentTimeMillis(),
                height.getValue());
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, fixedCircleRadius, rollingCircleRadius, drawingPointOffset, points, epitrochoid, animationSpeed, height};
    }
}
