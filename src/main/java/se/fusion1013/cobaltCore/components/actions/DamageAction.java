package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.LiteralVariable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DamageAction extends AbstractAction {

    private final DoubleVariable amount = new DoubleVariable("amount", 0);

    private final LiteralVariable type = new LiteralVariable("type", "single", new String[]{"single", "area"});
    private final DoubleVariable range = new DoubleVariable("range", 1);

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(amount, type, range);
    }

    @Override
    public void execute(Map<String, Object> context) {
        if (type.getValue().equalsIgnoreCase("single")) {
            damageEntity(getTargetLivingEntity(context));
        } else if (type.getValue().equalsIgnoreCase("area")) {
            Location center = getTargetLocation(context);
            Collection<LivingEntity> entities = center.getNearbyLivingEntities(range.getValue(), range.getValue(), range.getValue());
            entities.forEach(this::damageEntity);
        }
    }

    private void damageEntity(LivingEntity target) {
        target.damage(amount.getValue());
    }

    @Override
    public String getId() {
        return "damage";
    }
}
