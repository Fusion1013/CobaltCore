package se.fusion1013.cobaltCore.item.properties;

import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.loader.IObjectProperty;
import se.fusion1013.cobaltCore.manager.Manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemPropertyManager extends Manager<CobaltCore> {

    private static final Map<String, Supplier<IObjectProperty<ItemCreationContext, AbstractCobaltItem>>> ITEM_PROPERTIES = new HashMap<>();

    public ItemPropertyManager(CobaltCore plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        ITEM_PROPERTIES.clear();
        register("item_enchantment", ItemEnchantmentProperty::new);
        register("item_tag", ItemTagProperty::new);
        register("item_visual", ItemVisualProperty::new);
        register("item_rarity", ItemRarityProperty::new);
        register("item_category", ItemCategoryProperty::new);
        register("item_attribute", ItemAttributeProperty::new);
        register("item_toggle", ItemToggleProperty::new);
        register("item_recipe", ItemRecipeProperty::new);
        register("item_meta", ItemMetaProperty::new);
        register("item_banner", ItemBannerProperty::new);
        register("item_component", ItemComponentProperty::new);
        register("activator", ItemActivatorProperty::new);
    }

    @Override
    public void disable() {

    }

    public static Supplier<IObjectProperty<ItemCreationContext, AbstractCobaltItem>> register(String id, Supplier<IObjectProperty<ItemCreationContext, AbstractCobaltItem>> property) {
        ITEM_PROPERTIES.put(id, property);
        return property;
    }

    public static Collection<IObjectProperty<ItemCreationContext, AbstractCobaltItem>> getProperties() {
        return ITEM_PROPERTIES.values().stream().map(Supplier::get).toList();
    }

    private static ItemPropertyManager INSTANCE;

    public static ItemPropertyManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ItemPropertyManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
