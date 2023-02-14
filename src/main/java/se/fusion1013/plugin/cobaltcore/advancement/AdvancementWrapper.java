package se.fusion1013.plugin.cobaltcore.advancement;

import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancementWrapper implements IAdvancementWrapper {

    //region FIELDS

    private final CobaltPlugin plugin;

    // Internal
    private final String namespace;
    private final String internalName;

    // Display
    private final String title;
    private final String description;
    private final String itemIconName;

    private final AdvancementDisplay.AdvancementFrame frame;
    private final AdvancementVisibility visibility;

    private String parent;

    private String backgroundTexture;
    private int xPos = 0;
    private int yPos = 0;

    private final List<AdvancementFlag> flags = new ArrayList<>();

    //endregion

    //region CONSTRUCTORS

    public AdvancementWrapper(CobaltPlugin plugin, YamlConfiguration yaml) {
        this.plugin = plugin;

        // Internals
        namespace = yaml.getString("namespace");
        internalName = yaml.getString("internal_name");

        // Display
        title = yaml.getString("title");
        description = yaml.getString("description");
        itemIconName = yaml.getString("icon_item");

        frame = EnumUtils.findEnumInsensitiveCase(AdvancementDisplay.AdvancementFrame.class, yaml.getString("frame"));

        visibility = AdvancementVisibility.parseVisibility(yaml.getString("visibility"));

        // Optional
        if (yaml.contains("parent")) parent = yaml.getString("parent");
        if (yaml.contains("background_texture")) backgroundTexture = yaml.getString("background_texture");
        if (yaml.contains("x_pos")) xPos = yaml.getInt("x_pos");
        if (yaml.contains("y_pos")) yPos = yaml.getInt("y_pos");

        if (yaml.contains("flags")) {
            List<String> newFlags = yaml.getStringList("flags");
            for (String s : newFlags) {
                if (s.equalsIgnoreCase("toast_and_message")) flags.addAll(Arrays.stream(AdvancementFlag.TOAST_AND_MESSAGE).toList());
                else flags.add(EnumUtils.findEnumInsensitiveCase(AdvancementFlag.class, s));
            }
        }
    }


    //endregion

    @Override
    public boolean register(AdvancementManager manager) {
        AdvancementDisplay display = new AdvancementDisplay(CustomItemManager.getItemStack(itemIconName), title, description, frame, visibility);
        if (backgroundTexture != null) display.setBackgroundTexture(backgroundTexture);
        display.setX(xPos);

        Advancement advancement;
        display.setY(yPos);

        if (parent != null) {
            Advancement parentAdvancement = manager.getAdvancement(createNameKey(plugin, namespace, parent));
            if (parentAdvancement == null) return false;

            advancement = new Advancement(parentAdvancement, createNameKey(plugin, namespace, internalName), display, flags.toArray(new AdvancementFlag[0]));
        } else {
            advancement = new Advancement(createNameKey(plugin, namespace, internalName), display, flags.toArray(new AdvancementFlag[0]));
        }

        manager.addAdvancement(advancement);

        return true;
    }

    private static NameKey createNameKey(CobaltPlugin plugin, String namespace, String internalName) {
        return new NameKey(plugin.getInternalName() + "." + namespace + "." + internalName, internalName);
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public CobaltPlugin getPlugin() {
        return plugin;
    }
}
