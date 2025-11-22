package se.fusion1013.cobaltCore.particle.effects;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public interface IParticleEffect {

    default void display(Location center) { display(center, null); }
    void display(Location center, Player player);
    List<Vector> getPoints();
    List<Vector> getPoints(Location center);
    String getName();
    List<String> getInfoStrings();
    IParticleEffect copy();

    void modify(String key, Object value);
    void modify(Object[] args);
    void modify(CommandArguments arguments);
    List<Argument> getModifyArguments();

}
