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

public class ParticleEffectSphere extends AbstractParticleEffect implements IParticleEffect {

    private double radius;
    private int density;

    public ParticleEffectSphere(Particle particle, double radius, int density) {
        this(new TransformationPipeline(), particle, radius, density);
    }

    public ParticleEffectSphere(TransformationPipeline pipeline, Particle particle, double radius, int density) {
        super(particle, pipeline);
        this.radius = radius;
        this.density = density;
    }

    @Override
    public void display(Location center, Player player) {
        List<Vector> points = ParticleShapeUtils.generateSphere(radius, density);
        display(center, player, particle, points);
    }

    @Override
    public List<Vector> getPoints() {
        return ParticleShapeUtils.generateSphere(radius, density);
    }

    @Override
    public String getName() {
        return "sphere";
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = super.getModifyArguments();
        arguments.add(new DoubleArgument("radius"));
        arguments.add(new IntegerArgument("density"));
        return arguments;
    }

    @Override
    public void modify(CommandArguments arguments) {
        super.modify(arguments);
        radius = (double) arguments.get("radius");
        density = (int) arguments.get("density");
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectSphere(transformationPipeline, particle, radius, density);
    }
}
