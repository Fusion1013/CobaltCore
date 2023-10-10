package se.fusion1013.plugin.cobaltcore.item.section;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.FileUtil;
import se.fusion1013.plugin.cobaltcore.util.IFileConstructor;
import se.fusion1013.plugin.cobaltcore.util.INameProvider;
import se.fusion1013.plugin.cobaltcore.util.IProviderStorage;

import java.util.HashMap;
import java.util.Map;

public class ItemSectionManager extends Manager {

    // region Fields

    private static final Map<String, ItemSection> REGISTERED_SECTIONS = new HashMap<>();

    // endregion

    // region Constructors

    public ItemSectionManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // endregion

    // region Loading

    public static void load(CobaltPlugin plugin, boolean overwrite) {
        FileUtil.loadFilesInto(plugin, "item_sections/", new IProviderStorage() {
            @Override
            public void put(String key, INameProvider provider) {
                REGISTERED_SECTIONS.put(key, (ItemSection) provider);
            }

            @Override
            public boolean has(String key) {
                return REGISTERED_SECTIONS.containsKey(key);
            }

            @Override
            public INameProvider get(String key) {
                return REGISTERED_SECTIONS.get(key);
            }
        }, new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return new ItemSection(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return new ItemSection(json);
            }
        }, overwrite);
    }

    // endregion

    // region Reload / Disable

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // endregion

    // region Getters / Setters

    public static ItemSection getSection(String key) {
        return REGISTERED_SECTIONS.get(key);
    }

    // endregion

}
