package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.action.system.AbstractAction;
import se.fusion1013.plugin.cobaltcore.action.system.IEntityAction;
import se.fusion1013.plugin.cobaltcore.action.system.ILivingEntityAction;

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

    @Override
    public String getInternalName() {
        return "damage_action";
    }

    @Override
    public boolean activate(LivingEntity entity) {
        if (setsFire) entity.setFireTicks(fireTicks);
        if (knockback && extraData.containsKey("knockback_vector")) {
            Vector knockbackVector = (Vector) extraData.get("knockback_vector");
            entity.setVelocity(entity.getVelocity().add(knockbackVector.normalize().multiply(knockbackForce)));
        }

        entity.damage(getDamageWithCrit());

        return true;
    }

    @Override
    public boolean activate(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) return activate(livingEntity);
        return false;
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
}
