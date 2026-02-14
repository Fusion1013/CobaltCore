package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltCore.variable.VectorVariable;

import java.util.List;
import java.util.Map;

public class VelocityAction extends AbstractAction {

    private final DoubleVariable amplifier = new DoubleVariable("amplifier", 1);
    private final VectorVariable direction = new VectorVariable("direction", new Vector(0, 1, 0));
    private final StringVariable axis = new StringVariable("axis", "provided");

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(amplifier, direction, axis);
    }

    @Override
    public void execute(Map<String, Object> context) {
        LivingEntity entity = getTargetLivingEntity(context);

        Vector dir = direction.getValue();
        if (axis.getValue().equalsIgnoreCase("look") || axis.getValue().equalsIgnoreCase("eyes"))
            dir = entity.getEyeLocation().getDirection();

        entity.setVelocity(entity.getVelocity().add(dir.multiply(amplifier.getValue())));
    }

    @Override
    public String getId() {
        return "velocity";
    }
}
