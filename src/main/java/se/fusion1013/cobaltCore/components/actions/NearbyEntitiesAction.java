package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.ActionVariable;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyEntitiesAction extends AbstractAction {

    private final StringVariable entityType = new StringVariable("entity");
    private final DoubleVariable radius = new DoubleVariable("radius");
    private final ActionVariable actions = new ActionVariable("actions");

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(entityType, radius, actions);
    }

    @Override
    public void execute(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        CobaltCore.getInstance().getLogger().info("Location: " + location);
        CobaltCore.getInstance().getLogger().info("Entity: " + entityType.getValue());
        CobaltCore.getInstance().getLogger().info("Radius: " + radius.getValue());
        if (location == null) return;

        if (entityType.getValue().equalsIgnoreCase("player")) {
            Collection<Player> nearbyPlayers = location.getNearbyPlayers(radius.getValue());
            CobaltCore.getInstance().getLogger().info("Found " + nearbyPlayers.size());
            for (Player player : nearbyPlayers) {
                Map<String, Object> newContext = new HashMap<>(context);
                newContext.put("nearby_entity", player);
                actions.getValueList().forEach(a -> a.execute(newContext));
            }
        }
    }

    @Override
    public String getId() {
        return "nearby_entities";
    }
}
