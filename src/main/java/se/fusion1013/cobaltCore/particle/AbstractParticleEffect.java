package se.fusion1013.cobaltCore.particle;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ParticleArgument;
import dev.jorel.commandapi.wrappers.ParticleData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParticleEffect implements IParticleEffect {

    protected Particle particle;
    protected TransformationPipeline transformationPipeline;

    public AbstractParticleEffect(Particle particle, TransformationPipeline transformationPipeline) {
        this.particle = particle;
        this.transformationPipeline = transformationPipeline;
    }

    protected void display(Location center, Player player, Particle particle, List<Vector> points) {
        for (Vector vector : points) {
            Vector transformed = transformationPipeline.apply(vector);
            displayParticleAtLocation(center, player, particle, transformed);
        }
    }

    private void displayParticleAtLocation(Location center, Player player, Particle particle, Vector vector) {
        Location point = center.clone().add(vector);
        if (player != null) {
            player.spawnParticle(particle, point, 1, 0, 0, 0, 0);
        } else {
            center.getWorld().spawnParticle(particle, point, 1, 0 ,0 ,0 ,0);
        }
    }

    @Override
    public List<String> getInfoStrings() {
        return new ArrayList<>();
    }

    // ##### GETTERS / SETTERS #####

    public void setTransformationPipeline(TransformationPipeline transformationPipeline) {
        this.transformationPipeline = transformationPipeline;
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = new ArrayList<>();

        arguments.add(new ParticleArgument("particle"));

        return arguments;
    }

    @Override
    public void modify(Object[] args) {

    }

    @Override
    public void modify(String key, Object value) {
        switch (key) {
            case "particle" -> particle = ((ParticleData) value).particle();
        }
    }
}
