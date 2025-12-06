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

public class ParticleEffectSpiral extends AbstractParticleEffect implements IParticleEffect {

    private final DoubleVariable radius = new DoubleVariable("radius");
    private final IntVariable count = new IntVariable("count");
    private final DoubleVariable rotationSpeed = new DoubleVariable("rotationSpeed");
    private final DoubleVariable heightSpeed = new DoubleVariable("heightSpeed");
    private final DoubleVariable waveHeight = new DoubleVariable("waveHeight");

    public ParticleEffectSpiral(Particle particle) {
        super("spiral", particle, new TransformationPipeline());
    }

    @Override
    public void display(Location center, Player player, TransformationPipeline extraPipeline) {
        display(center, player, particle.getParticle(), getPoints(), extraPipeline);
    }

    @Override
    public List<Vector> getPoints() {
        return ShapeUtils.getAnimatedCircle(radius.getValue(), count.getValue(), rotationSpeed.getValue(), heightSpeed.getValue(), waveHeight.getValue());
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, radius, count, rotationSpeed, heightSpeed, waveHeight};
    }
}
