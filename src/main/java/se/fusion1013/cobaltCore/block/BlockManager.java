package se.fusion1013.cobaltCore.block;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.commands.system.CommandManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.CobaltRegistry;
import se.fusion1013.cobaltCore.manager.registry.RegistryProviderStorage;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.IFileConstructor;
import se.fusion1013.cobaltCore.util.INameProvider;

public class BlockManager extends Manager<CobaltCore> {

    private static final CobaltRegistry<ICustomBlock> BLOCKS = new CobaltRegistry<>();

    public static void loadCustomBlocks(CobaltPlugin plugin, boolean overwrite) {
        FileUtil.loadFilesInto(plugin, "block/", new RegistryProviderStorage<>(BLOCKS), new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return new CustomBlock(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return null;
            }
        }, overwrite);
    }

    public static void reloadBlocks() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadCustomBlocks(plugin, true);
        }
    }

    public BlockManager(CobaltCore plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        reloadBlocks();
        CommandManager.registerReloadMethod("blocks", BlockManager::reloadBlocks, BLOCKS::getNames);
    }

    @Override
    public void disable() {

    }
}
