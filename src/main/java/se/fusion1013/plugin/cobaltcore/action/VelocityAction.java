package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.action.system.*;
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;

import java.util.Map;

/**
 * This action adds velocity to an entity when activated.
 */
public class VelocityAction extends AbstractAction implements IEntityAction, ILivingEntityAction {

    //region FIELDS

    private double velocity = 1.0;
    private RelativeAxis relativeAxis = RelativeAxis.WORLD;

    private final Vector directionModifier = new Vector(0, 0, 0);

    //endregion

    //region CONSTRUCTORS

    protected VelocityAction(Map<?, ?> data) {
        super(data);

        if (data.containsKey("xModifier")) this.directionModifier.setX((double) data.get("xModifier"));
        if (data.containsKey("yModifier")) this.directionModifier.setY((double) data.get("yModifier"));
        if (data.containsKey("zModifier")) this.directionModifier.setZ((double) data.get("zModifier"));

        if (data.containsKey("velocity")) this.velocity = (double) data.get("velocity");

        if (data.containsKey("relative_axis")) this.relativeAxis = EnumUtils.findEnumInsensitiveCase(RelativeAxis.class, (String) data.get("relative_axis"));
    }

    //endregion

    //region ACTIVATION

    @Override
    public IActionResult activate(Entity entity) {
        entity.setVelocity(entity.getVelocity().add(directionModifier.clone().multiply(velocity)));
        return new ActionResult(true);
    }

    @Override
    public IActionResult activate(LivingEntity entity) {
        if (relativeAxis == RelativeAxis.HEAD) {
            Vector direction = entity.getEyeLocation().getDirection().clone().multiply(velocity);

            // Rotate vector around head
            Vector rightVector = getRightVector(direction);
            Vector upVector = direction.clone().rotateAroundAxis(rightVector, Math.toRadians(90));
            direction.rotateAroundAxis(upVector, Math.toRadians(directionModifier.getX()));

            // Rotate vector up/down
            direction.rotateAroundAxis(rightVector, Math.toRadians(directionModifier.getY()));

            entity.setVelocity(entity.getVelocity().add(direction));

        } else if (relativeAxis == RelativeAxis.WORLD) {
            entity.setVelocity(entity.getVelocity().add(directionModifier.clone().multiply(velocity)));
        }

        return new ActionResult(true);
    }

    @Override
    public IActionResult activate() {
        if (extraData.containsKey("living_entity")) { // Prioritize entity activation
            LivingEntity livingEntity = (LivingEntity) extraData.get("living_entity");
            return activate(livingEntity);
        } else if (extraData.containsKey("entity")) {
            Entity entity = (Entity) extraData.get("entity");
            return activate(entity);
        }

        return new ActionResult(false);
    }

    //endregion

    //region GETTERS/SETTERS

    private static Vector getRightVector(Vector vector){
        Vector direction = vector.clone().normalize();
        return new Vector(direction.getZ(), 0, -direction.getX()).normalize();
    }

    @Override
    public String getInternalName() {
        return "velocity_action";
    }

    //endregion

    //region RELATIVITY ENUM

    public enum RelativeAxis {
        WORLD,
        HEAD
    }

    //endregion

}
