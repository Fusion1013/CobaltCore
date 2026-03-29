package se.fusion1013.cobaltCore.manager.registry;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.commands.system.CommandManager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.IFileConstructor;
import se.fusion1013.cobaltCore.util.INameProvider;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class FileLoadedRegistry<T extends IRegistryItem> extends CobaltRegistry<T> {

    private final CobaltPlugin parent; // Where to store files
    private final String name; // Used ex for the folder path
    private final Function<YamlConfiguration, T> createFromYaml;
    private final Function<JsonObject, T> createFromJson;
    private final BiConsumer<Player, T> applyItem;

    public FileLoadedRegistry(CobaltPlugin parent, String name, Function<YamlConfiguration, T> createFromYaml, Function<JsonObject, T> createFromJson, BiConsumer<Player, T> applyItem) {
        this.parent = parent;
        this.name = name;
        this.createFromYaml = createFromYaml;
        this.createFromJson = createFromJson;
        this.applyItem = applyItem;
    }

    public void reload() {
        reloadAll();

        // Register Commands
        CommandManager.registerReloadMethod(name, this::reloadAll, this::getNames);
        CommandManager.registerPastebinMethod(parent, name, (player, yaml) -> {
            T result = createFromYaml.apply(yaml);
            applyItem.accept(player, result);
        }, this::reloadAll);
        CommandManager.registerItemInfo(name, this::getNames, (f) -> get(f).getInfo());
    }

    private void reloadAll() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadFiles(plugin);
        }
    }

    private void loadFiles(CobaltPlugin plugin) {
        FileUtil.loadFilesInto(plugin, name, new RegistryProviderStorage<>(this), new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return createFromYaml.apply(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return createFromJson.apply(json);
            }
        }, true);
    }

}
