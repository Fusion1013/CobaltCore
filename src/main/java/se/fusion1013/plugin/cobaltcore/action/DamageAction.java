package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.action.system.*;

import java.util.Map;
import java.util.Random;

public class DamageAction extends AbstractAction implements ILivingEntityAction {

    //region FIELDS

    private int damage;

    private boolean setsFire = false;
    private int fireTicks;

    private boolean knockback = false;
    private double knockbackForce;

    private double criticalChance;

    //endregion

    //region CONSTRUCTION

    protected DamageAction(Map<?, ?> data) {
        super(data);

        if (data.containsKey("damage")) damage = (int) data.get("damage");

        if (data.containsKey("fire")) setsFire = (boolean) data.get("fire");
        if (data.containsKey("sets_fire")) setsFire = (boolean) data.get("sets_fire");
        if (data.containsKey("fire_ticks")) fireTicks = (int) data.get("fire_ticks");

        if (data.containsKey("knockback")) knockback = (boolean) data.get("knockback");
        if (data.containsKey("knockback_force")) knockbackForce = (double) data.get("knockback_force");

        if (data.containsKey("critical_chance")) criticalChance = (double) data.get("critical_chance");
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public String getInternalName() {
        return "damage_action";
    }

    private int getDamageWithCrit(){
        int critIncrease = (int)criticalChance;
        Random r = new Random();
        double chance = r.nextDouble();
        if (criticalChance-critIncrease >= chance){
            critIncrease++;
        }
        critIncrease++;

        return damage * critIncrease;
    }

    //endregion

    //region ACTIVATION

    @Override
    public IActionResult activate() {
        if (extraData.containsKey("living_entity")) {
            LivingEntity livingEntity = (LivingEntity) extraData.get("living_entity");
            return activate(livingEntity);
        } else if (extraData.containsKey("entity")) {
            Entity entity = (Entity) extraData.get("entity");
            return activate(entity);
        }

        return new ActionResult(false);
    }

    @Override
    public IActionResult activate(LivingEntity entity) {
        if (setsFire) entity.setFireTicks(fireTicks);
        if (knockback && extraData.containsKey("knockback_vector")) {
            Vector knockbackVector = (Vector) extraData.get("knockback_vector");
            entity.setVelocity(entity.getVelocity().add(knockbackVector.normalize().multiply(knockbackForce)));
        }

        entity.damage(getDamageWithCrit());

        return new ActionResult(true);
    }

    @Override
    public IActionResult activate(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) return activate(livingEntity);
        return new ActionResult(false);
    }

    //endregion
}
