package se.fusion1013.cobaltCore.particle.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandSet;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.variable.ParticleVariable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParticleEffect implements IParticleEffect, ICommandSet {

    protected final String internalName;
    protected ParticleVariable particle = new ParticleVariable("particle");
    protected TransformationPipeline transformationPipeline;

    public AbstractParticleEffect(String internalName, Particle particle, TransformationPipeline transformationPipeline) {
        this.internalName = internalName;
        this.particle.setParticle(particle);
        this.transformationPipeline = transformationPipeline;
    }

    protected void display(Location center, Player player, Particle particle, List<Vector> points, TransformationPipeline extraPipeline) {
        for (Vector vector : points) {
            Vector transformed1 = transformationPipeline.apply(vector);
            Vector transformed2 = extraPipeline.apply(transformed1);
            displayParticleAtLocation(center, player, particle, transformed2);
        }
    }

    private void displayParticleAtLocation(
            Location center, Player player, Particle particle, Vector vector) {
        Location point = center.clone().add(vector);
        if (player != null) {
            player.spawnParticle(particle, point, 1, 0, 0, 0, 0, null, true);
        } else {
            center.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0, null, true);
        }
    }

    @Override
    public List<String> getInfoStrings() {
        return new ArrayList<>();
    }

    // ##### GETTERS / SETTERS #####

    @Override
    public String getName() {
        return internalName;
    }

    @Override
    public List<Vector> getPoints(Location center) {
        return List.of();
    }

    public void setTransformationPipeline(TransformationPipeline transformationPipeline) {
        this.transformationPipeline = transformationPipeline;
    }

    @Override
    public ICommandSet getCommandIntegration() {
        return this;
    }
}
