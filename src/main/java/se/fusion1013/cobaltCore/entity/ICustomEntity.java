package se.fusion1013.cobaltCore.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import se.fusion1013.cobaltCore.util.INameProvider;

import java.util.List;

public interface ICustomEntity extends INameProvider {

    EntityType getEntityType();

    String getId();

    List<String> getNoAttackPlayerNames();

    ICustomEntityInstance spawn(World world, Location location);

    ICustomEntityInstance load(Entity entity);

}
