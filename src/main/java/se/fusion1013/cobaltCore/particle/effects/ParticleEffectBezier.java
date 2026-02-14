package se.fusion1013.cobaltCore.particle.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.BezierUtil;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.LocationVariable;

import java.util.List;

public class ParticleEffectBezier extends AbstractParticleEffect implements IParticleEffect {

    private final LocationVariable startLocation = new LocationVariable("startLocation");
    private final LocationVariable startDirection = new LocationVariable("startDirection");
    private final LocationVariable endLocation = new LocationVariable("endLocation");
    private final LocationVariable endDirection = new LocationVariable("endDirection");
    private final IntVariable density = new IntVariable("density");

    public ParticleEffectBezier(Particle particle) {
        super("bezier", particle, new TransformationPipeline());
    }

    @Override
    public void display(Location center, Player player, TransformationPipeline extraPipeline) {
        display(new Location(center.getWorld(), 0, 0, 0), player, particle.getParticle(), particle.getData(), getPoints(), extraPipeline);
    }

    @Override
    public List<Vector> getPoints() {
        return BezierUtil.bezierCurve(
                startLocation.getValue().toVector(),
                startDirection.getValue().toVector(),
                endDirection.getValue().toVector(),
                endLocation.getValue().toVector(),
                density.getValue());
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, startLocation, startDirection, endLocation, endDirection, density};
    }
}
