package se.fusion1013.plugin.cobaltcore.particle.styles;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IDisplayInstance {

    void display(Location location, Player... players);

    void display(Location location1, Location location2, Player... players);

    void display(Location location);

    void display(Location location1, Location location2);

}
