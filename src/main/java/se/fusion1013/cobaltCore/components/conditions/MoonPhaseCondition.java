package se.fusion1013.cobaltCore.components.conditions;

import io.papermc.paper.world.MoonPhase;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.LiteralVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MoonPhaseCondition extends AbstractCondition {

    private final LiteralVariable moonPhase = new LiteralVariable("phase", Arrays.stream(MoonPhase.values()).map(Enum::toString).toArray(String[]::new));

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(moonPhase);
    }

    @Override
    protected boolean evaluateCondition(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        if (location == null) return false;

        World world = location.getWorld();
        MoonPhase currentMoonPhase = world.getMoonPhase();
        for (String s : moonPhase.getValueList()) {
            if (currentMoonPhase.toString().equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    @Override
    public String getInternalName() {
        return "moon_phase";
    }

    @Override
    public String getDescription() {
        String value = String.join(", ", moonPhase.getValueList().toArray(String[]::new));
        return "Requires any Moon Phase in: " + value;
    }
}
