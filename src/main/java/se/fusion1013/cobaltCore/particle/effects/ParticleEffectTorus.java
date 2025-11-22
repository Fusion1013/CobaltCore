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

public class ParticleEffectTorus extends AbstractParticleEffect implements IParticleEffect {

    private double majorRadius;
    private double minorRadius;
    private int density;

    public ParticleEffectTorus(Particle particle, double majorRadius, double minorRadius, int density) {
        this(new TransformationPipeline(), particle, majorRadius, minorRadius, density);
    }

    public ParticleEffectTorus(TransformationPipeline pipeline, Particle particle, double majorRadius, double minorRadius, int density) {
        super(particle, pipeline);
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
        this.density = density;
    }

    @Override
    public void display(Location center, Player player) {
        List<Vector> points = getPoints();
        display(center, player, particle, points);
    }

    @Override
    public List<Vector> getPoints() {
        return ParticleShapeUtils.generateTorus(majorRadius, minorRadius, density);
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = super.getModifyArguments();

        arguments.add(new DoubleArgument("majorRadius"));
        arguments.add(new DoubleArgument("minorRadius"));
        arguments.add(new IntegerArgument("density"));

        return arguments;
    }

    @Override
    public void modify(Object[] args) {
        super.modify(args);

    }

    @Override
    public void modify(String key, Object value) {
        super.modify(key, value);
        switch (key) {
            case "majorRadius" -> majorRadius = (double) value;
            case "minorRadius" -> minorRadius = (double) value;
            case "density" -> density = (int) value;

        }
    }

    @Override
    public void modify(CommandArguments arguments) {
        super.modify(arguments);
        majorRadius = (double) arguments.get("majorRadius");
        minorRadius = (double) arguments.get("minorRadius");
        density = (int) arguments.get("density");
    }

    @Override
    public String getName() {
        return "torus";
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectTorus(transformationPipeline, particle, majorRadius, minorRadius, density);
    }
}
