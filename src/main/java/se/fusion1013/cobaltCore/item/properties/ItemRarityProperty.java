package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.section.ItemSection;
import se.fusion1013.cobaltCore.item.section.ItemSectionManager;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

import java.util.ArrayList;
import java.util.List;

public class ItemRarityProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private ItemSection rarity;
    private final List<String> rarityLore = new ArrayList<>();

    @Override
    public String getId() {
        return "item_rarity";
    }

    @Override
    public void create(ItemCreationContext obj) {
        if (rarity == null) return;

        obj.persistent.set(rarity.getNamespacedKey(), PersistentDataType.BYTE, (byte) 1); // Set rarity key

        obj.lore.add("");
        obj.lore.add(LegacyComponentSerializer.legacyAmpersand().serialize(rarity.getFormattedName().append(Component.text(" Item").color(rarity.getColor()).decoration(TextDecoration.ITALIC, false))));
        rarityLore.forEach(rl -> {
            Component component = Component.text(rl).color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false);
            obj.lore.add(LegacyComponentSerializer.legacyAmpersand().serialize(component));
        });
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {
        loadRarity(json);
        loadRarityLore(json);
    }

    private void loadRarity(JsonObject json) {
        if (!json.has("rarity")) return;
        String rarityName = json.get("rarity").getAsString();
        rarity = ItemSectionManager.getSection(rarityName);
    }

    private void loadRarityLore(JsonObject json) {
        if (!json.has("rarity_lore")) return;
        JsonArray rarityLoreJson = json.get("rarity_lore").getAsJsonArray();
        rarityLoreJson.forEach(k -> rarityLore.add(k.getAsString()));
    }

    @Override
    public void saveJson(JsonObject json) {
    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        loadRarity(yaml);
        loadRarityLore(yaml);
    }

    private void loadRarity(ConfigurationSection yaml) {
        if (!yaml.contains("rarity")) return;
        String rarityName = yaml.getString("rarity");
        rarity = ItemSectionManager.getSection(rarityName);
    }

    private void loadRarityLore(ConfigurationSection yaml) {
        if (!yaml.contains("rarity_lore")) return;
        List<String> rarityLore = yaml.getStringList("rarity_lore");
        this.rarityLore.addAll(rarityLore);
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
        if (rarity != null) yaml.set("rarity", rarity.getInternalName());
        if (!rarityLore.isEmpty()) yaml.set("rarity_lore", rarityLore);
    }
}
