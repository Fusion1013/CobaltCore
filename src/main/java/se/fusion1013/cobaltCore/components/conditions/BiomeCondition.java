package se.fusion1013.cobaltCore.components.conditions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.BiomeVariable;

import java.util.List;
import java.util.Map;

public class BiomeCondition extends AbstractCondition {

    private final BiomeVariable biome = new BiomeVariable("biome");

    @Override
    public boolean evaluate(Map<String, Object> context) {
        Location location = (Location) context.get("default_location");
        if (location == null) return false;

        World world = location.getWorld();
        if (world == null) return false;

        Biome biome = world.getBiome(location);
        return biome == this.biome.getValue();
    }

    @Override
    public String getInternalName() {
        return "biome";
    }

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(biome);
    }
}
