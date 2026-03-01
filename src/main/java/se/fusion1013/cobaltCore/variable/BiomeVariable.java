package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.BiomeArgument;
import org.bukkit.block.Biome;
import se.fusion1013.cobaltCore.variable.provider.BiomeValueProvider;

public class BiomeVariable extends AbstractVariable<Biome, BiomeValueProvider, BiomeArgument, BiomeVariable> {

    public BiomeVariable(String name) {
        super(name, new BiomeValueProvider(name));
    }

    public BiomeVariable(String name, Biome defaultValue) {
        super(name, new BiomeValueProvider(name, defaultValue));
    }

    @Override
    public BiomeVariable getSelf() {
        return this;
    }

    @Override
    public BiomeArgument getArgument() {
        return new BiomeArgument(getName());
    }
}
