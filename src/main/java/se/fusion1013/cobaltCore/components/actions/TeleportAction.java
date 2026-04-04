package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import se.fusion1013.cobaltCore.variable.AbstractVariable;

import java.util.List;
import java.util.Map;

public class TeleportAction extends AbstractAction {

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of();
    }

    @Override
    public void execute(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        LivingEntity entity = getTargetLivingEntity(context);
        entity.teleport(location);
    }

    @Override
    public String getId() {
        return "teleport";
    }
}
