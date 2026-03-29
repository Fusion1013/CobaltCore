package se.fusion1013.cobaltCore.components.conditions;

import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.LiteralVariable;

import java.util.List;
import java.util.Map;

public class TimeCondition extends AbstractCondition {

    private final LiteralVariable time = new LiteralVariable("time", new String[]{"day", "night", "twilight"});

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(time);
    }

    @Override
    protected boolean evaluateCondition(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        if (location == null) return false;

        World world = location.getWorld();
        if (world == null) return false;

        long currentTimeInTicks = world.getTime();

        switch (time.getValue()) {
            case "day" -> {
                return currentTimeInTicks <= 12000;
            }
            case "night" -> {
                return currentTimeInTicks <= 24000 && currentTimeInTicks >= 12000;
            }
            case "twilight" -> {
                return currentTimeInTicks <= 1000 || currentTimeInTicks >= 23000 || (currentTimeInTicks >= 11000 && currentTimeInTicks <= 13000);
            }
        }

        return false;
    }

    @Override
    public String getInternalName() {
        return "time";
    }

    @Override
    public String getDescription() {
        return "Requires time to be " + time.getValue();
    }
}
