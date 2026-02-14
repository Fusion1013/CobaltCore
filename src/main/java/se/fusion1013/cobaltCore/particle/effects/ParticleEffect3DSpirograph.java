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

public class ParticleEffect3DSpirograph extends AbstractParticleEffect implements IParticleEffect {

    private final DoubleVariable majorRadius = new DoubleVariable("majorRadius");
    private final DoubleVariable minorRadius = new DoubleVariable("minorRadius");
    private final DoubleVariable amplitude = new DoubleVariable("amplitude");
    private final DoubleVariable verticalTwistAmplitude = new DoubleVariable("verticalTwistAmplitude");
    private final DoubleVariable freq1 = new DoubleVariable("freq1");
    private final DoubleVariable freq2 = new DoubleVariable("freq2");
    private final DoubleVariable freq3 = new DoubleVariable("freq3");
    private final IntVariable points = new IntVariable("points");

    public ParticleEffect3DSpirograph(Particle particle) {
        super("3d_spirograph", particle, new TransformationPipeline());
    }

    @Override
    public void display(Location center, Player player, TransformationPipeline extraPipeline) {
        display(center, player, particle.getParticle(), particle.getData(), getPoints(), extraPipeline);
    }

    @Override
    public List<Vector> getPoints() {
        return ShapeUtils.generate3DSpirograph(majorRadius.getValue(),
                minorRadius.getValue(),
                amplitude.getValue(),
                verticalTwistAmplitude.getValue(),
                freq1.getValue(),
                freq2.getValue(),
                freq3.getValue(),
                points.getValue());
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, majorRadius, minorRadius, amplitude, verticalTwistAmplitude, freq1, freq2, freq3, points};
    }
}
