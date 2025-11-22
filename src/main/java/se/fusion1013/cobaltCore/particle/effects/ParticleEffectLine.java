package se.fusion1013.cobaltCore.particle.effects;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.particle.ParticleShapeUtils;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;

import java.util.ArrayList;
import java.util.List;

public class ParticleEffectLine extends AbstractParticleEffect implements IParticleEffect {

    private int density = 8;
    private Location location2;

    public ParticleEffectLine(Particle particle) {
        super(particle, new TransformationPipeline());
    }

    public ParticleEffectLine(Particle particle, TransformationPipeline transformationPipeline, int density, Location location2) {
        super(particle, transformationPipeline);
        this.density = density;
        this.location2 = location2;
    }

    @Override
    public void display(Location center, Player player) {
        List<Vector> points = getPoints(center);
        display(center, player, particle, points);
    }

    @Override
    public List<Vector> getPoints() {
        return List.of();
    }

    @Override
    public List<Vector> getPoints(Location center) {
        List<Vector> points = new ArrayList<>();
        double distance = center.distance(location2);
        int steps = (int) Math.round(density * distance);
        Vector direction = location2.clone().subtract(center).toVector().normalize();
        for (int i = 0; i < steps; i++) {
            Vector location = direction.clone().multiply((double) i / (double) density);
            points.add(location);
        }
        return points;
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = super.getModifyArguments();

        arguments.add(new IntegerArgument("density"));
        arguments.add(new LocationArgument("end_location"));

        return arguments;
    }

    @Override
    public void modify(CommandArguments arguments) {
        super.modify(arguments);
        density = (int) arguments.get("density");
        location2 = (Location) arguments.get("end_location");
    }

    @Override
    public String getName() {
        return "line";
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectLine(particle, transformationPipeline, density, location2);
    }
}
