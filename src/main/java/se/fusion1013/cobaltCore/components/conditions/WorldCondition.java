package se.fusion1013.cobaltCore.components.conditions;

import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.List;
import java.util.Map;

public class WorldCondition extends AbstractCondition {

    private final StringVariable world = new StringVariable("world");

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(world);
    }

    @Override
    public boolean evaluate(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        if (location == null) return false;

        World targetWorld = location.getWorld();
        for (String worldName : world.getValueList()) {
            if (targetWorld.getName().equalsIgnoreCase(worldName)) return true;
        }
        return false;
    }

    @Override
    public String getInternalName() {
        return "world";
    }
}
