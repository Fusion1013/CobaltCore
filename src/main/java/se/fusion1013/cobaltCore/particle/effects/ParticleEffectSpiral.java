package se.fusion1013.cobaltCore.particle.effects;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.ParticleShapeUtils;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;

import java.util.List;

public class ParticleEffectSpiral extends AbstractParticleEffect implements IParticleEffect {

    private double radius;
    private int count;
    private double rotationSpeed;
    private double heightSpeed;
    private double waveHeight;

    public ParticleEffectSpiral(Particle particle) {
        super(particle, new TransformationPipeline());
    }

    @Override
    public void display(Location center, Player player) {
        display(center, player, particle, getPoints());
    }

    @Override
    public List<Vector> getPoints() {
        return ParticleShapeUtils.getAnimatedCircle(radius, count, rotationSpeed, heightSpeed,  waveHeight);
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = super.getModifyArguments();
        arguments.add(new DoubleArgument("radius"));
        arguments.add(new IntegerArgument("count"));
        arguments.add(new DoubleArgument("rotationSpeed"));
        arguments.add(new DoubleArgument("heightSpeed"));
        arguments.add(new DoubleArgument("waveHeight"));
        return arguments;
    }

    @Override
    public void modify(CommandArguments arguments) {
        super.modify(arguments);
        radius = (double) arguments.get("radius");
        count = (int) arguments.get("count");
        rotationSpeed = (double) arguments.get("rotationSpeed");
        heightSpeed = (double) arguments.get("heightSpeed");
        waveHeight = (double) arguments.get("waveHeight");
    }

    @Override
    public String getName() {
        return "spiral";
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectSpiral(particle);
    }
}
