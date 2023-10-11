package se.fusion1013.plugin.cobaltcore.item.loaders;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.section.ItemSection;
import se.fusion1013.plugin.cobaltcore.item.section.ItemSectionManager;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;

import java.util.ArrayList;
import java.util.List;

public class RarityLoader implements IItemLoader {

    @Override
    public void Load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        LoadRarity(yaml, builder);
        LoadLore(yaml, builder);
    }

    private static void LoadRarity(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("rarity")) return;
        var rarityName = yaml.getString("rarity");
        ItemSection rarity = ItemSectionManager.getSection(rarityName);
        if (rarity != null) builder.rarity(rarity);
        else CobaltCore.getInstance().getLogger().warning("Could not find Item Rarity '" + rarityName + "'");
    }

    private static void LoadLore(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("rarity_lore")) return;
        List<String> rarityLore = yaml.getStringList("rarity_lore");
        List<Component> rarityLoreComponents = new ArrayList<>();

        for (String s : rarityLore) rarityLoreComponents.add(
                Component.text(s)
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );

        builder.rarityLore(rarityLoreComponents.toArray(new Component[0]));
    }

    @Override
    public void Load(JsonObject json, CobaltItem.Builder builder) {
    }
}
