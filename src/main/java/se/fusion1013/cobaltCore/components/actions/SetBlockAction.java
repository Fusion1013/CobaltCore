package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.MaterialVariable;

import java.util.List;
import java.util.Map;

public class SetBlockAction extends AbstractAction {

    private final MaterialVariable material = new MaterialVariable("block");

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(material);
    }

    @Override
    public void execute(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        if (location == null) return;

        World world = location.getWorld();
        world.setBlockData(location, material.getValue().createBlockData());
    }

    @Override
    public String getId() {
        return "set_block";
    }
}
