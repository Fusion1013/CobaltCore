package se.fusion1013.cobaltCore.particle.effects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandSet;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;

import java.util.List;

/**
 * Interface for interacting with a particle effect.
 */
public interface IParticleEffect {

    default void display(Location center) {
        display(center, null, null);
    }

    void display(Location center, Player player, TransformationPipeline extraPipeline);

    List<Vector> getPoints();

    List<Vector> getPoints(Location center);

    String getName();

    List<String> getInfoStrings();

    ICommandSet getCommandIntegration();

}
