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

/**
 * Generates particles in the shape of a circle.
 */
public class ParticleEffectCircle extends AbstractParticleEffect implements IParticleEffect {

    private final DoubleVariable radius = new DoubleVariable("radius");
    private final IntVariable iterations = new IntVariable("iterations");
    private final BooleanVariable randomPlacement = new BooleanVariable("randomPlacement", false)
            .optional();

    /**
     * Constructor.
     *
     * @param particle the particle.
     */
    public ParticleEffectCircle(Particle particle) {
        super("circle", particle, new TransformationPipeline());
    }

    @Override
    public List<Vector> getPoints(Location center) {
        return ShapeUtils.generateCircle(radius.getValue(), iterations.getValue(), randomPlacement.getValue());
    }

    @Override
    public void display(Location center, Player player, TransformationPipeline extraPipeline) {
        display(center, player, particle.getParticle(), particle.getData(), getPoints(), extraPipeline);
    }

    @Override
    public List<Vector> getPoints() {
        return ShapeUtils.generateCircle(radius.getValue(), iterations.getValue(), randomPlacement.getValue());
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, radius, iterations, randomPlacement};
    }

    @Override
    public String getName() {
        return "circle";
    }
}
