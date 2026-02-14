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
import se.fusion1013.cobaltCore.components.IComponent;
import se.fusion1013.cobaltCore.item.components.IItemComponent;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.item.properties.ItemPropertyManager;
import se.fusion1013.cobaltCore.item.section.ItemSection;
import se.fusion1013.cobaltCore.item.toggles.IItemToggles;
import se.fusion1013.cobaltCore.item.toggles.ItemToggleType;
import se.fusion1013.cobaltCore.item.toggles.ItemToggles;
import se.fusion1013.cobaltCore.loader.IObjectProperty;

import java.util.*;

public abstract class AbstractCobaltItem implements ICustomItem {

    // ----- VARIABLES -----

    // NOTE: All things that can be set through a builder must have a default value.

    // -- INTERNALS
    private final String internalName; // Should be unique to every item. Used to generate the NamespacedKey
    private final NamespacedKey key; // Unique for each item. Generated using the internal name

    protected Material material = Material.CLOCK;

    // -- ITEM ACTIVATORS
    protected final Map<ItemActivator, List<IComponent>> itemActivatorExecutorsSync = new HashMap<>();
    protected final Map<ItemActivator, List<IComponent>> itemActivatorExecutorsAsync = new HashMap<>();

    // -- ITEM COMPONENTS
    protected final Map<String, IItemComponent> itemComponents = new HashMap<>();

    // -- TOGGLES
    protected final IItemToggles toggles = new ItemToggles();

    protected final Collection<IObjectProperty<ItemCreationContext, AbstractCobaltItem>> properties;

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
        properties = ItemPropertyManager.getProperties();
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
    public <T extends Event> void activatorTriggeredAsync(ItemActivator activator, T event, EquipmentSlot slot, Map<String, Object> context) {
        List<IComponent> components = itemActivatorExecutorsAsync.get(activator);
        if (components == null || components.isEmpty()) return;
        components.forEach(c -> c.execute(context));
    }

    @Override
    public <T extends Event> void activatorTriggeredAsync(ItemActivator activator, T event, Map<String, Object> context) {
        activatorTriggeredAsync(activator, event, null, context);
    }

    @Override
    public <T extends Event> void activatorTriggeredSync(ItemActivator activator, T event, EquipmentSlot slot, Map<String, Object> context) {
        List<IComponent> components = itemActivatorExecutorsSync.get(activator);
        if (components == null || components.isEmpty()) return;
        components.forEach(c -> c.execute(context));
    }

    @Override
    public <T extends Event> void activatorTriggeredSync(ItemActivator activator, T event, Map<String, Object> context) {
        activatorTriggeredSync(activator, event, null, context);
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
    }

    public void addSyncItemActivator(ItemActivator event, IComponent component) {
        itemActivatorExecutorsSync.computeIfAbsent(event, k -> new ArrayList<>()).add(component);
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