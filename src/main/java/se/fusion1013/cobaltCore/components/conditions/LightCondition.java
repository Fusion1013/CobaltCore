package se.fusion1013.cobaltCore.components.conditions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;

import java.util.List;
import java.util.Map;

public class LightCondition extends AbstractCondition {

    private final IntVariable skyLightLevel = new IntVariable("sky", -1);
    private final IntVariable blockLightLevel = new IntVariable("block", -1);

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(skyLightLevel, blockLightLevel);
    }

    @Override
    protected boolean evaluateCondition(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        if (location == null) return false;

        Block block = location.getBlock();
        byte lightFromBlocks = block.getLightFromBlocks();
        byte lightFromSky = block.getLightFromSky();

        if (skyLightLevel.getValue() != -1 && (lightFromSky < skyLightLevel.getMin() || lightFromSky > skyLightLevel.getMax()))
            return false;
        return blockLightLevel.getValue() == -1 || (lightFromBlocks >= blockLightLevel.getMin() && lightFromBlocks <= blockLightLevel.getMax());
    }

    @Override
    public String getInternalName() {
        return "light";
    }

    @Override
    public String getDescription() {
        return "Requires Skylight Between: " + skyLightLevel.getMax() + " and " + skyLightLevel.getMin() + ", and Blocklight Between: " + blockLightLevel.getMax() + " and " + blockLightLevel.getMin();
    }
}
