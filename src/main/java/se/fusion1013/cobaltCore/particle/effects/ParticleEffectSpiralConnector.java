package se.fusion1013.cobaltCore.particle.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.BezierUtil;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.LocationVariable;

import java.util.List;

public class ParticleEffectSpiralConnector extends AbstractParticleEffect
        implements IParticleEffect {

    private final LocationVariable startLocation = new LocationVariable("startLocation");
    private final LocationVariable direction1 = new LocationVariable("direction1");
    private final LocationVariable endLocation = new LocationVariable("endLocation");
    private final LocationVariable direction2 = new LocationVariable("direction2");
    private final DoubleVariable radius = new DoubleVariable("radius");
    private final DoubleVariable turns = new DoubleVariable("turns");
    private final IntVariable points = new IntVariable("points");
    private final DoubleVariable wiggleAmplitude = new DoubleVariable("wiggleAmplitude")
            .optional();
    private final DoubleVariable animationSpeed = new DoubleVariable("animationSpeed")
            .optional();

    public ParticleEffectSpiralConnector(Particle particle) {
        super("spiral_connector", particle, new TransformationPipeline());
    }

    @Override
    public void display(Location center, Player player) {
        display(new Location(center.getWorld(), 0, 0, 0), player, particle.getParticle(), getPoints());
    }

    @Override
    public List<Vector> getPoints() {
        double time = System.currentTimeMillis() * animationSpeed.getValue();
        return ShapeUtils.spiralAroundPath(
                BezierUtil.bezierCurve(
                        startLocation.getValue().toVector(),
                        direction1.getValue().toVector(),
                        direction2.getValue().toVector(),
                        endLocation.getValue().toVector(),
                        points.getValue() * 10),
                radius.getValue(),
                turns.getValue(),
                time,
                points.getValue(),
                wiggleAmplitude.getValue());
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, startLocation, direction1, endLocation, direction2, turns, radius, points, wiggleAmplitude, animationSpeed};
    }
}
