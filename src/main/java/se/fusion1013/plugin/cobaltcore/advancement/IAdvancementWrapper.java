package se.fusion1013.plugin.cobaltcore.advancement;

import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;

import java.util.List;

public interface IAdvancementWrapper {

    boolean register(AdvancementManager manager);

    String getNamespace();

    CobaltPlugin getPlugin();
}
