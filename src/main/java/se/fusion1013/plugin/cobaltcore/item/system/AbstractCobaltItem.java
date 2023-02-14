package se.fusion1013.plugin.cobaltcore.item.system;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.item.IItemActivatorExecutor;
import se.fusion1013.plugin.cobaltcore.item.IItemMetaEditor;
import se.fusion1013.plugin.cobaltcore.item.ItemActivator;
import se.fusion1013.plugin.cobaltcore.item.category.IItemCategory;
import se.fusion1013.plugin.cobaltcore.item.category.ItemCategory;
import se.fusion1013.plugin.cobaltcore.item.components.IItemComponent;
import se.fusion1013.plugin.cobaltcore.item.components.AbstractItemComponent;
import se.fusion1013.plugin.cobaltcore.item.enchantment.EnchantmentWrapper;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.ItemUtil;

import java.util.*;

public abstract class AbstractCobaltItem implements ICustomItem {

    // ----- VARIABLES -----

    // NOTE: All things that can be set through a builder must have a default value.

    // -- INTERNALS
    private final String internalName; // Should be unique to every item. Used to generate the NamespacedKey
    private final NamespacedKey key; // Unique for each item. Generated using the internal name

    // -- GENERIC
    protected int amount = 1;

    // -- ITEM VISUALS
    protected Material material = Material.CLOCK;
    protected int modelData = 0;

    // -- ITEM NAME
    protected String itemName = "default item name";

    // -- ENCHANTMENTS
    protected List<EnchantmentWrapper> enchantmentWrappers = new ArrayList<>();

    // -- RARITY
    protected IItemRarity rarity = ItemRarity.NONE;
    protected List<String> rarityExtraLore = new ArrayList<>(); // Lore to put under the rarity

    // -- EXTRA LORE
    protected List<String> extraLore = new ArrayList<>();

    // -- SPECIAL ABILITIES
    protected Map<Attribute, AttributeModifier> attributes = new HashMap<>();

    // -- ITEM CATEGORIES
    protected IItemCategory itemCategory = ItemCategory.NONE;

    // -- ITEM ATTRIBUTES

    // -- ITEM META
    protected IItemMetaEditor metaEditor; // Allows for full control over the item's meta

    // -- ITEM ACTIVATORS
    protected final Map<ItemActivator, IItemActivatorExecutor> itemActivatorExecutorsSync = new HashMap<>();
    protected final Map<ItemActivator, IItemActivatorExecutor> itemActivatorExecutorsAsync = new HashMap<>();

    // -- ITEM TAGS
    protected String[] tags = new String[0];

    // -- ITEM COMPONENTS
    protected final Map<String, IItemComponent> itemComponents = new HashMap<>();

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
        ItemStack stack = new ItemStack(material, amount);

        // -- ENCHANTMENTS // NOTE: Must be set before getting item meta from itemstack
        for (EnchantmentWrapper wrapper : enchantmentWrappers) stack = wrapper.add(stack); // TODO: If there is a high number of enchantments, add them in a compact list in the lore (like hypixel)

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        List<String> lore = new ArrayList<>();

        // -- INTERNALS
        persistentDataContainer.set(key, PersistentDataType.INTEGER, 1);

        // -- ITEM VISUALS
        meta.setCustomModelData(modelData);

        // -- ITEM NAME
        meta.setDisplayName(itemName);

        // -- ITEM COMPONENT LORE
        itemComponents.values().forEach(k -> lore.addAll(k.getLore()));
        for (IItemComponent component : itemComponents.values()) component.onItemConstruction(stack, meta, persistentDataContainer);

        // -- RARITY
        if (rarity != ItemRarity.NONE) {
            persistentDataContainer.set(rarity.getNamespacedKey(), PersistentDataType.BYTE, (byte) 1); // Set rarity key

            // Add rarity lore
            lore.add(""); // Add a new line
            lore.add(LegacyComponentSerializer.legacyAmpersand().serialize(rarity.getFormattedRarity()));
            lore.addAll(rarityExtraLore);
        } else {
            persistentDataContainer.set(ItemRarity.NONE.getNamespacedKey(), PersistentDataType.BYTE, (byte) 1); // Set to NONE rarity. Does not impact item visuals
        }

        // -- EXTRA LORE
        if (!extraLore.isEmpty()) {
            lore.add(""); // Add a new line
            lore.addAll(extraLore);
        }

        // -- ITEM CATEGORY
        if (itemCategory != ItemCategory.NONE) {
            persistentDataContainer.set(itemCategory.getNamespacedKey(), PersistentDataType.BYTE, (byte) 1);

            // Add item category lore
            lore.add(""); // Add a new line
            lore.add(
                    LegacyComponentSerializer.legacyAmpersand().serialize(
                            itemCategory.getFormattedName().color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)
                    )
            );
        } else {
            persistentDataContainer.set(ItemCategory.NONE.getNamespacedKey(), PersistentDataType.BYTE, (byte) 1);
        }

        // -- ATTRIBUTES
        for (Attribute attribute : attributes.keySet()) {
            meta.addAttributeModifier(attribute, attributes.get(attribute));
        }

        // -- META EDITOR
        if (metaEditor != null) meta = metaEditor.editMeta(meta);

        // Set meta
        stack.setItemMeta(meta);

        // Colorize lore
        for (int i = 0; i < lore.size(); i++) lore.set(i, HexUtils.colorify(lore.get(i)));

        // Set lore
        if (stack.getLore() != null) {
            List<String> mergedLore = new ArrayList<>(stack.getLore());
            mergedLore.addAll(lore);
            stack.setLore(mergedLore);
        } else {
            stack.setLore(lore);
        }

        // Set enchantment glint if there are enchantments
        boolean addGlint = enchantmentWrappers.size() > 0;
        for (EnchantmentWrapper wrapper : enchantmentWrappers)
            if (wrapper.getEnchantment() != null) {
                addGlint = false;
                break;
            }
        if (addGlint) stack = ItemUtil.addEnchantmentGlint(stack);

        return stack;
    }

    // ----- ITEM COMPARISON -----

    @Override
    public boolean compareTo(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
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

        T obj;
        String internalName;

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

        public B modelData(int modelData) {
            obj.modelData = modelData;
            return getThis();
        }

        // -- GENERIC

        public B amount(int amount) {
            obj.amount = amount;
            return getThis();
        }

        // -- ITEM NAME

        public B itemName(String itemName) {
            obj.itemName = itemName;
            return getThis();
        }

        public B itemName(Component itemName) {
            obj.itemName = LegacyComponentSerializer.legacyAmpersand().serialize(itemName);
            return getThis();
        }

        // -- ENCHANTMENTS

        public B enchantments(EnchantmentWrapper... enchantmentWrappers) {
            obj.enchantmentWrappers.addAll(List.of(enchantmentWrappers));
            return getThis();
        }

        // -- RARITY

        public B rarity(IItemRarity rarity) {
            obj.rarity = rarity;
            return getThis();
        }

        public B rarityLore(String... rarityLore) {
            obj.rarityExtraLore.addAll(Arrays.asList(rarityLore));
            return getThis();
        }

        public B rarityLore(Component... rarityLore) {
            for (Component component : rarityLore) obj.rarityExtraLore.add(LegacyComponentSerializer.legacyAmpersand().serialize(component));
            return getThis();
        }

        // -- EXTRA LORE

        public B extraLore(String... extraLore) {
            obj.extraLore.addAll(Arrays.asList(extraLore));
            return getThis();
        }

        public B extraLore(Component... extraLore) {
            for (Component component : extraLore) obj.extraLore.add(LegacyComponentSerializer.legacyAmpersand().serialize(component));
            return getThis();
        }

        // -- SPECIAL ABILITIES

        // -- ITEM CATEGORIES

        public B category(IItemCategory category) {
            obj.itemCategory = category;
            return getThis();
        }

        // -- ITEM ATTRIBUTES

        public B attribute(Attribute attribute, AttributeModifier modifier) {
            obj.attributes.put(attribute, modifier);
            return getThis();
        }

        // -- ITEM META

        public B editMeta(IItemMetaEditor editor) {
            obj.metaEditor = editor;
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

        // -- ITEM TAGS

        public B tags(String... tags) {
            obj.tags = tags;
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

    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    @Override
    public IItemCategory getItemCategory() {
        return itemCategory;
    }

    @Override
    public String[] getTags() {
        return tags;
    }
}
