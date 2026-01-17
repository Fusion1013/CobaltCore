package se.fusion1013.cobaltCore.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import se.fusion1013.cobaltCore.util.INameProvider;

public interface ICustomEntity extends INameProvider {

    EntityType getEntityType();

    String getId();

    ICustomEntityInstance spawn(World world, Location location);

}
