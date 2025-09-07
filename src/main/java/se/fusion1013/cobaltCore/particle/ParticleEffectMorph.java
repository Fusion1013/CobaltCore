package se.fusion1013.cobaltCore.particle;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.particle.ParticleCommand;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.util.EasingUtil;

import java.util.ArrayList;
import java.util.List;

public class ParticleEffectMorph extends AbstractParticleEffect implements IParticleEffect {

    private List<Vector> fromPoints;
    private List<Vector> toPoints;
    private double progress; // between 0.0 and 1.0

    public ParticleEffectMorph(List<Vector> fromPoints, List<Vector> toPoints, Particle particle) {
        this(new TransformationPipeline(), fromPoints, toPoints, particle);
    }

    public ParticleEffectMorph(TransformationPipeline pipeline, List<Vector> fromPoints, List<Vector> toPoints, Particle particle) {
        super(particle, pipeline);
        // int size = Math.min(fromPoints.size(), toPoints.size());
        // this.fromPoints = fromPoints.subList(0, size);
        // this.toPoints = matchPoints(fromPoints, toPoints.subList(0, size));

        this.fromPoints = fromPoints;
        this.toPoints = matchPoints(fromPoints, toPoints);

        this.progress = 0.0;
    }

    public void setProgress(double progress) {
        this.progress = Math.max(0, Math.min(1, progress));
    }

    public double getProgress() {
        return progress;
    }

    @Override
    public void display(Location center, Player player) {
        for (int i = 0; i < fromPoints.size(); i++) {
            Vector a = fromPoints.get(i);
            Vector b = toPoints.get(i);

            double easedProgress = EasingUtil.ease(progress, EasingUtil.EasingMethod.EaseInOutSine);

            Vector interpolated = a.clone().multiply(1.0 - easedProgress).add(b.clone().multiply(easedProgress));
            Location point = center.clone().add(interpolated);

            if (player != null) {
                player.spawnParticle(particle, point, 1, 0, 0, 0, 0);
            } else {
                center.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
            }
        }
    }

    private static List<Vector> matchPoints(List<Vector> from, List<Vector> to) {
        List<Vector> matchedTo = new ArrayList<>(from.size());
        List<Vector> remaining = new ArrayList<>(to);

        for (Vector f : from) {
            Vector closest = null;
            double bestDist = Double.MAX_VALUE;

            for (Vector candidate : remaining) {
                double dist = f.distanceSquared(candidate);
                if (dist < bestDist) {
                    bestDist = dist;
                    closest = candidate;
                }
            }

            matchedTo.add(closest);
            remaining.remove(closest);
        }

        return matchedTo;
    }

    @Override
    public List<Vector> getPoints() {
        return List.of();
    }

    @Override
    public String getName() {
        return "morph";
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = super.getModifyArguments();

        arguments.add(new StringArgument("from_effect").replaceSuggestions(ArgumentSuggestions.strings(s -> ParticleEffectManager.getCustomParticleEffectNames())));
        arguments.add(new StringArgument("to_effect").replaceSuggestions(ArgumentSuggestions.strings(s -> ParticleEffectManager.getCustomParticleEffectNames())));
        arguments.add(new DoubleArgument("progress"));

        return arguments;
    }

    @Override
    public void modify(String key, Object value) {
        super.modify(key, value);
        switch (key) {
            case "from_effect":
                fromPoints = loadPointsFromEffect(ParticleEffectManager.getCustomEffect((String) value));
                matchPoints(fromPoints, toPoints);
                break;
            case "to_effect":
                toPoints = loadPointsFromEffect(ParticleEffectManager.getCustomEffect((String) value));
                matchPoints(fromPoints, toPoints);
                break;
            case "progress":
                progress = (double) value;
                break;
        }
    }

    private List<Vector> loadPointsFromEffect(IParticleEffect effect) {
        return effect.getPoints();
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectMorph(transformationPipeline, fromPoints, toPoints, particle);
    }
}
