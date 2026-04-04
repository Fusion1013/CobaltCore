package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.BooleanVariable;
import se.fusion1013.cobaltCore.variable.DoubleVariable;

import java.util.List;
import java.util.Map;

public class ExplodeAction extends AbstractAction {

    private final DoubleVariable power = new DoubleVariable("power", 1);
    private final BooleanVariable fire = new BooleanVariable("fire", false);

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(power, fire);
    }

    @Override
    public void execute(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        World world = location.getWorld();
        world.createExplosion(location, power.getValue().floatValue(), fire.getValue());
    }

    @Override
    public String getId() {
        return "explode";
    }
}
