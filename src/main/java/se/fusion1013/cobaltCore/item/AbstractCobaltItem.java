package se.fusion1013.cobaltCore.item;


import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.components.AbstractItemComponent;
import se.fusion1013.cobaltCore.item.components.IItemComponent;
import se.fusion1013.cobaltCore.item.properties.*;
import se.fusion1013.cobaltCore.item.section.ItemSection;
import se.fusion1013.cobaltCore.item.toggles.IItemToggles;
import se.fusion1013.cobaltCore.item.toggles.ItemToggleType;
import se.fusion1013.cobaltCore.item.toggles.ItemToggles;
import se.fusion1013.cobaltCore.loader.IObjectProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCobaltItem implements ICustomItem {

    // ----- VARIABLES -----

    // NOTE: All things that can be set through a builder must have a default value.

    // -- INTERNALS
    private final String internalName; // Should be unique to every item. Used to generate the NamespacedKey
    private final NamespacedKey key; // Unique for each item. Generated using the internal name

    protected Material material = Material.CLOCK;

    protected final List<IObjectProperty<ItemCreationContext, AbstractCobaltItem>> properties = List.of(
            new ItemEnchantmentProperty(),
            new ItemTagProperty(),
            new ItemVisualProperty(),
            new ItemRarityProperty(),
            new ItemCategoryProperty(),
            new ItemAttributeProperty(),
            new ItemToggleProperty(),
            new ItemRecipeProperty(),
            new ItemMetaProperty()
    );

    // -- ITEM ACTIVATORS
    protected final Map<ItemActivator, IItemActivatorExecutor> itemActivatorExecutorsSync = new HashMap<>();
    protected final Map<ItemActivator, IItemActivatorExecutor> itemActivatorExecutorsAsync = new HashMap<>();

    // -- ITEM COMPONENTS
    protected final Map<String, IItemComponent> itemComponents = new HashMap<>();

    // -- TOGGLES
    protected final IItemToggles toggles = new ItemToggles();

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>AbstractCobaltItem</code>.
     *
     * @param internalName the internal name of the item.
     */
    public AbstractCobaltItem(String internalName) {
        // Internals must be set by constructors
        this.internalName = internalName;
        this.key = new NamespacedKey(CobaltCore.getInstance(), this.internalName);
    }

    public AbstractCobaltItem(String internalName, Material material) {
        this(internalName);
        this.material = material;
    }

    // ----- ITEM LOADING / DISABLING -----

    public void onLoad() {
        for (IItemComponent component : itemComponents.values()) {
            component.onLoad();
        }
    }

    public void onDisable() { // Should be called before plugin reloads items
        for (IItemComponent component : itemComponents.values()) {
            component.onDisable();
        }
    }

    // ----- ITEM CONSTRUCTION -----

    @Override
    public ItemStack getItemStack() {
        // TODO: Lore strings should automatically get put into new lines based on the length of the strings
        ItemCreationContext context = new ItemCreationContext(key, material);

        for (IObjectProperty<ItemCreationContext, AbstractCobaltItem> property : properties) {
            property.create(context);
        }

        return context.finalizeItem();

        /*
        // -- ITEM COMPONENT LORE
        itemComponents.values().forEach(k -> lore.addAll(k.getLore()));
        for (IItemComponent component : itemComponents.values())
            component.onItemConstruction(stack, meta, persistentDataContainer);
         */
    }

    protected void loadInternalData(YamlConfiguration yamlConfiguration) {
        for (IObjectProperty<ItemCreationContext, AbstractCobaltItem> property : properties) {
            property.fromYaml(yamlConfiguration, this);
        }
    }

    public static ICustomItem load(YamlConfiguration yaml) {
        String internalName = yaml.getString("internal_name");
        String materialString = yaml.getString("material");
        Material material = EnumUtils.findEnumInsensitiveCase(Material.class, materialString);
        CobaltItem cobaltItem = new CobaltItem(internalName, material);
        cobaltItem.loadInternalData(yaml);
        return cobaltItem;
    }

    public static ICustomItem load(JsonObject json) {
        return null; // TODO
    }

    // ----- ITEM COMPARISON -----

    @Override
    public boolean compareTo(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(key);
    }

    // ----- ACTIVATORS -----

    @Override
    public void activatorTriggeredAsync(ItemActivator activator, Event event, EquipmentSlot slot) {
        IItemActivatorExecutor executor = itemActivatorExecutorsAsync.get(activator);
        if (executor != null) executor.execute(this, event, slot);
    }

    @Override
    public void activatorTriggeredAsync(ItemActivator activator, Event event) {
        activatorTriggeredAsync(activator, event, null);
    }

    @Override
    public <T extends Event> void activatorTriggeredSync(ItemActivator activator, T event, EquipmentSlot slot) {
        IItemActivatorExecutor executor = itemActivatorExecutorsSync.get(activator);
        if (executor != null) executor.execute(this, event, slot);

        // Attempt to activate Component events
        itemComponents.values().forEach(k -> k.onEvent(activator, event, slot));
    }

    @Override
    public <T extends Event> void activatorTriggeredSync(ItemActivator activator, T event) {
        activatorTriggeredSync(activator, event, null);
    }

    // ----- BUILDER -----

    protected static abstract class Builder<T extends AbstractCobaltItem, B extends Builder> {

        // ----- VARIABLES -----

        private final T obj;
        public final String internalName;

        // ----- CONSTRUCTORS -----

        public Builder(String internalName) {
            this.internalName = internalName;
            obj = createObj();
        }

        // ----- CREATION METHODS -----

        public T build() {
            obj.onLoad();
            return obj;
        }

        protected abstract T createObj();

        protected abstract B getThis();

        // ----- BUILDER METHODS -----

        // -- ITEM VISUALS

        public B material(Material material) {
            obj.material = material;
            return getThis();
        }

        // -- ITEM ACTIVATORS

        public B itemActivatorSync(ItemActivator activator, IItemActivatorExecutor executor) {
            obj.itemActivatorExecutorsSync.put(activator, executor);
            return getThis();
        }

        public B itemActivatorAsync(ItemActivator activator, IItemActivatorExecutor executor) {
            obj.itemActivatorExecutorsAsync.put(activator, executor);
            return getThis();
        }

        // -- ITEM COMPONENTS
        public B component(AbstractItemComponent.Builder<?, ?> componentBuilder) {
            // Build the component
            AbstractItemComponent component = componentBuilder.build();
            this.component(component);
            return getThis();
        }

        public B component(IItemComponent component) {
            // Set the owning item
            component.setOwningItem(internalName);

            // Load events registered by component
            Map<ItemActivator, IItemActivatorExecutor> activators = component.registerEvents();
            obj.itemActivatorExecutorsSync.putAll(activators);

            // Add the component
            obj.itemComponents.put(component.getInternalName(), component);
            return getThis();
        }

        public B setToggle(ItemToggleType type, boolean value) {
            obj.toggles.setValue(type, value);
            return getThis();
        }
    }

    // ----- GETTERS / SETTERS -----


    @Override
    public YamlConfiguration toYaml() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("internal_name", internalName);
        yaml.set("material", material.toString());

        for (IObjectProperty<ItemCreationContext, AbstractCobaltItem> property : properties) {
            property.saveYaml(yaml);
        }

        return yaml;
    }

    @Override
    public JSONObject toJson() {
        // TODO: This should probably not be done like this
        JSONObject json = new JSONObject();

        json.put("internal_name", internalName);
        json.put("material", material.toString());

        JSONObject enchantments = new JSONObject();
        // TODO

        JSONArray rarityLore = new JSONArray();
        json.put("rarity_lore", rarityLore);

        JSONArray extraLore = new JSONArray();
        extraLore.forEach(l -> extraLore.add(l));
        json.put("extra_lore", extraLore);

        return json;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    @Override
    public ItemSection getItemCategory() {
        return null; // TODO
    }

    @Override
    public String[] getTags() {
        return new String[0]; // TODO
    }

    @Override
    public IItemToggles getItemToggles() {
        return toggles;
    }

    public void setItemToggle(ItemToggleType itemToggleType, boolean value) {
        toggles.setValue(itemToggleType, value);
        CobaltCore.getInstance().getLogger().info("Set item toggle " + itemToggleType + " for item " + internalName);
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = new ArrayList<>();
        for (IObjectProperty<ItemCreationContext, AbstractCobaltItem> property : properties) {
            info.addAll(property.getLocalizedInfo());
        }
        return info;
    }
}