package se.fusion1013.cobaltCore.particle.effects;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.ShapeUtils;

import java.util.List;

public class ParticleEffect3DSpirograph extends AbstractParticleEffect implements IParticleEffect {

    private double majorRadius;
    private double minorRadius;
    private double amplitude;
    private double verticalTwistAmplitude;
    private double freq1;
    private double freq2;
    private double freq3;
    private int points;

    public ParticleEffect3DSpirograph(Particle particle) {
        super(particle, new TransformationPipeline());
    }

    public ParticleEffect3DSpirograph(Particle particle, TransformationPipeline transformationPipeline, double majorRadius, double minorRadius, double amplitude, double verticalTwistAmplitude, double freq1, double freq2, double freq3, int points) {
        super(particle, transformationPipeline);
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
        this.amplitude = amplitude;
        this.verticalTwistAmplitude = verticalTwistAmplitude;
        this.freq1 = freq1;
        this.freq2 = freq2;
        this.freq3 = freq3;
        this.points = points;
    }

    @Override
    public void display(Location center, Player player) {
        display(center, player, particle, getPoints());
    }

    @Override
    public List<Vector> getPoints() {
        return ShapeUtils.generate3DSpirograph(majorRadius, minorRadius, amplitude, verticalTwistAmplitude, freq1, freq2, freq3, points);
    }

    @Override
    public void modify(CommandArguments arguments) {
        super.modify(arguments);
        majorRadius = (double) arguments.get("major_radius");
        minorRadius = (double) arguments.get("minor_radius");
        amplitude = (double) arguments.get("amplitude");
        verticalTwistAmplitude = (double) arguments.get("vertical_twist_amplitude");
        freq1 = (double) arguments.get("freq1");
        freq2 = (double) arguments.get("freq2");
        freq3 = (double) arguments.get("freq3");
        points = (int) arguments.get("points");
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = super.getModifyArguments();
        arguments.add(new DoubleArgument("major_radius"));
        arguments.add(new DoubleArgument("minor_radius"));
        arguments.add(new DoubleArgument("amplitude"));
        arguments.add(new DoubleArgument("vertical_twist_amplitude"));
        arguments.add(new DoubleArgument("freq1"));
        arguments.add(new DoubleArgument("freq2"));
        arguments.add(new DoubleArgument("freq3"));
        arguments.add(new IntegerArgument("points"));
        return arguments;
    }

    @Override
    public String getName() {
        return "spirograph3d";
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffect3DSpirograph(particle, transformationPipeline, majorRadius, minorRadius, amplitude, verticalTwistAmplitude, freq1, freq2, freq3, points);
    }
}
