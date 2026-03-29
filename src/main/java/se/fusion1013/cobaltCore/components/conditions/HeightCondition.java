package se.fusion1013.cobaltCore.components.conditions;

import org.bukkit.Location;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;

import java.util.List;
import java.util.Map;

public class HeightCondition extends AbstractCondition {

    private final IntVariable height = new IntVariable("height");

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(height);
    }

    @Override
    protected boolean evaluateCondition(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        if (location == null) return false;

        double y = location.getY();
        return y >= height.getMin() && y <= height.getMax();
    }

    @Override
    public String getInternalName() {
        return "height";
    }

    @Override
    public String getDescription() {
        return "Requires Height Between: " + height.getMax() + " and " + height.getMin();
    }
}
