package se.fusion1013.cobaltCore.particle.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.util.EasingUtil;

import java.util.ArrayList;
import java.util.List;

public class ParticleEffectMorph extends AbstractParticleEffect implements IParticleEffect {

    private final List<Vector> fromPoints;
    private final List<Vector> toPoints;
    private double progress; // between 0.0 and 1.0

    public ParticleEffectMorph(Particle particle, List<Vector> fromPoints, List<Vector> toPoints) {
        super("morph", particle, new TransformationPipeline());
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

            double easedProgress = EasingUtil.EasingMethod.EaseInOutSine.ease(progress);

            Vector interpolated = a.clone().multiply(1.0 - easedProgress).add(b.clone().multiply(easedProgress));
            Location point = center.clone().add(interpolated);

            if (player != null) {
                player.spawnParticle(particle.getParticle(), point, 1, 0, 0, 0, 0);
            } else {
                center.getWorld().spawnParticle(particle.getParticle(), point, 1, 0, 0, 0, 0);
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

    private List<Vector> loadPointsFromEffect(IParticleEffect effect) {
        return effect.getPoints();
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[0];
    }
}
