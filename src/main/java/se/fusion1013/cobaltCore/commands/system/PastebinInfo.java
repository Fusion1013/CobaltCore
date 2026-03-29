package se.fusion1013.cobaltCore.commands.system;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

import java.util.function.BiConsumer;

public record PastebinInfo(CobaltPlugin plugin, String name,
                           BiConsumer<Player, YamlConfiguration> yamlConsumer,
                           Runnable postLoad) implements IRegistryItem {
    @Override
    public String getInternalName() {
        return name;
    }
}
