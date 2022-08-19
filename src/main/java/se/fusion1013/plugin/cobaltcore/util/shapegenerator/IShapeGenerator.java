package se.fusion1013.plugin.cobaltcore.util.shapegenerator;

import org.bukkit.Location;

public interface IShapeGenerator {
    void place(Location location);

    IShapeGenerator clone();
}
