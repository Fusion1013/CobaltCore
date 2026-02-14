package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Location;
import se.fusion1013.cobaltCore.variable.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LocationAction extends AbstractAction {

    private final Random random = new Random();

    private final ActionVariable actions = new ActionVariable("actions");
    private final LiteralVariable type = new LiteralVariable("type", new String[]{"none", "point", "random_area"});
    private final DoubleVariable radius = new DoubleVariable("radius", 0);
    private final IntVariable count = new IntVariable("count", 1);

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(actions, type, radius, count);
    }

    @Override
    public void execute(Map<String, Object> context) {
        for (IAction action : actions.getValue()) {
            executeNewAction(action, context);
        }
    }

    private void executeNewAction(IAction action, Map<String, Object> oldContext) {
        if (type.getValue().equalsIgnoreCase("none")) {
            action.execute(oldContext);
        } else if (type.getValue().equalsIgnoreCase("random_area")) {
            Location oldLocation = getTargetLocation(oldContext);
            Map<String, Object> newContext = new HashMap<>(oldContext);
            for (int i = 0; i < count.getValue(); i++) {
                double x = random.nextDouble(-radius.getValue(), radius.getValue());
                double y = random.nextDouble(-radius.getValue(), radius.getValue());
                double z = random.nextDouble(-radius.getValue(), radius.getValue());
                Location newLocation = oldLocation.clone().add(x, y, z);
                newContext.put("default_location", newLocation);
                action.execute(newContext);
            }
        }
    }

    @Override
    public String getId() {
        return "location";
    }
}
