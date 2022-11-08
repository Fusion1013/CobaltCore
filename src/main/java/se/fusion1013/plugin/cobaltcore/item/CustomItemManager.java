package se.fusion1013.plugin.cobaltcore.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.event.PlayerHeldItemTickEvent;
import se.fusion1013.plugin.cobaltcore.item.category.IItemCategory;
import se.fusion1013.plugin.cobaltcore.item.category.ItemCategory;
import se.fusion1013.plugin.cobaltcore.item.components.ActionbarComponent;
import se.fusion1013.plugin.cobaltcore.item.components.ChargeComponent;
import se.fusion1013.plugin.cobaltcore.item.components.ComponentManager;
import se.fusion1013.plugin.cobaltcore.item.components.IItemComponent;
import se.fusion1013.plugin.cobaltcore.item.crafting.RecipeManager;
import se.fusion1013.plugin.cobaltcore.item.enchantment.CobaltEnchantment;
import se.fusion1013.plugin.cobaltcore.item.enchantment.EnchantmentManager;
import se.fusion1013.plugin.cobaltcore.item.enchantment.EnchantmentWrapper;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;
import se.fusion1013.plugin.cobaltcore.item.system.IItemRarity;
import se.fusion1013.plugin.cobaltcore.item.system.ItemRarity;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CustomItemManager extends Manager implements Listener {

    // ----- VARIABLES -----

    private static final Map<String, ItemStack> INBUILT_ITEMS = new HashMap<>(); // Holds all custom items ITEMSTACKS
    private static final Map<String, ICustomItem> INBUILT_CUSTOM_ITEMS = new HashMap<>(); // Holds all custom items CUSTOMITEMS
    private static final Map<IItemCategory, Map<String, ICustomItem>> ITEMS_SORTED_CATEGORY = new HashMap<>(); // Holds all custom items sorted by IItemCategory

    private static final List<IItemCategory[]> REGISTERED_CATEGORIES = new ArrayList<>();
    private static final List<IItemRarity[]> REGISTERED_RARITIES = new ArrayList<>();

    private static final List<IItemComponent> REGISTERED_COMPONENTS = new ArrayList<>();

    // ----- REGISTER HOLDERS -----

    private static final Class<ItemRarity> ITEM_RARITY = registerRarity(ItemRarity.class);
    private static final Class<ItemCategory> ITEM_CATEGORY = registerCategory(ItemCategory.class);

    private static final IItemComponent ACTIONBAR_COMPONENT = registerComponent(new ActionbarComponent(""));
    private static final IItemComponent CHARGE_COMPONENT = registerComponent(new ChargeComponent(""));

    // ----- ITEM REGISTERING -----

    // Item used for testing the CustomItem system.
    public static final ICustomItem TEST_ITEM = register(new CobaltItem.Builder("test_item")
            .material(Material.CLOCK).modelData(10) // Item Visuals
            .itemName(HexUtils.colorify("<r:0.8:1.0>Test Rainbow Item&3&o Not Rainbow")) // Item name
            .enchantments(new EnchantmentWrapper(CobaltEnchantment.WITHER, 2, false)) // Enchantment
            .rarity(ItemRarity.LEGENDARY) // Rarity
            .rarityLore(HexUtils.colorify("&8This item has a pretty high rarity huh,"), HexUtils.colorify("&8it is almost like you should not have it!")) // Rarity lore
            .extraLore(
                    Component.text("This is some extra lore").color(NamedTextColor.DARK_GRAY),
                    Component.text("This is more extra lore").color(NamedTextColor.DARK_GRAY),
                    Component.text("[").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                            .append(Component.keybind("key.sneak").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                            .append(Component.text("]: Do thing").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            ) // Extra lore
            .category(ItemCategory.TESTING) // Item category
            .editMeta(meta -> { // Edit meta
                meta.addEnchant(Enchantment.MENDING, 1, true);
                return meta;
            })
            .itemActivatorAsync(ItemActivator.PLAYER_ACTIVATE_SNEAK, (item, event, slot) -> {
                PlayerToggleSneakEvent sneakEvent = (PlayerToggleSneakEvent) event;
                sneakEvent.getPlayer().sendMessage("U DO DE SNEAK");
            })
            // Item Components
            .component(new ActionbarComponent.Builder()
                    .setActionbarComponent(Component.text("This is a test ab component")))
            .build());

    // Item used for testing the CustomBlock system.
    public static final ICustomItem MIXING_CAULDRON = register(new CustomItem.CustomItemBuilder("test_block", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "Mixing Cauldron")
            .setCustomModel(10005)
            .build());

    // ----- CONSTRUCTORS -----

    public CustomItemManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RECIPES -----

    // TODO: Move Recipes to CustomItem or AbstractCustomItem
    /**
     * WARNING: STONECUTTING RECIPES MUST BE REGISTERED IN ALPHABETICAL ORDER
    */
    public static StonecuttingRecipe addStoneCuttingRecipe(StonecuttingRecipe recipe) {
        CobaltCore.getInstance().getServer().addRecipe(recipe);
        return recipe;
    }

    public static ShapedRecipe addShapedRecipe(ItemStack result, String row1, String row2, String row3, AbstractCustomItem.ShapedIngredient... ingredients) {
        return addShapedRecipe("internal", result, row1, row2, row3, ingredients);
    }

    /**
     * Registers a new <code>ShapedRecipe</code> for the given <code>ItemStack</code>.
     * @param result the <code>ItemStack</code> to add a recipe for.
     * @param row1 recipe pattern.
     * @param row2 recipe pattern.
     * @param row3 recipe pattern.
     * @param ingredients recipe ingredients.
     * @return the recipe.
     */
    public static ShapedRecipe addShapedRecipe(String recipeName, ItemStack result, String row1, String row2, String row3, AbstractCustomItem.ShapedIngredient... ingredients) {
        StringBuilder keyString = new StringBuilder("custom.shapeless." + recipeName + ".");
        for (AbstractCustomItem.ShapedIngredient ingredient : ingredients) keyString.append(ingredient.item.getType().name());

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(CobaltCore.getInstance(), keyString.toString()), result);
        recipe.shape(row1, row2, row3);
        for (AbstractCustomItem.ShapedIngredient ingredient : ingredients) recipe.setIngredient(ingredient.key, ingredient.item);
        CobaltCore.getInstance().getServer().addRecipe(recipe);

        return recipe;
    }

    public static ShapelessRecipe addShapelessRecipe(ItemStack result, AbstractCustomItem.ShapelessIngredient... ingredients) {
        return addShapelessRecipe("internal", result, ingredients);
    }

    /**
     * Registers a new <code>ShapelessRecipe</code> for the given <code>ItemStack</code>.
     * @param result the <code>ItemStack</code> to add a recipe for.
     * @param ingredients recipe ingredients.
     * @return the recipe.
     */
    public static ShapelessRecipe addShapelessRecipe(String recipeName, ItemStack result, AbstractCustomItem.ShapelessIngredient... ingredients) {
        StringBuilder keyString = new StringBuilder("custom.shapeless." + recipeName + ".");
        for (AbstractCustomItem.ShapelessIngredient ingredient : ingredients) keyString.append(ingredient.item.getType().name());

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(CobaltCore.getInstance(), keyString.toString()), result);
        for (AbstractCustomItem.ShapelessIngredient ingredient : ingredients) recipe.addIngredient(ingredient.count, ingredient.item);
        CobaltCore.getInstance().getServer().addRecipe(recipe);

        return recipe;
    }

    // ----- REGISTER -----

    /**
     * Registers a new <code>CustomItem</code>.
     *
     * @param item the <code>CustomItem</code> to register.
     * @return the <code>CustomItem</code>.
     */
    public static ICustomItem register(ICustomItem item){
        INBUILT_ITEMS.put(item.getInternalName(), item.getItemStack());
        INBUILT_CUSTOM_ITEMS.put(item.getInternalName(), item);
        ITEMS_SORTED_CATEGORY.computeIfAbsent(item.getItemCategory(), k -> new HashMap<>()).put(item.getInternalName(), item);
        return item;
    }

    public static IItemComponent registerComponent(IItemComponent component) {
        REGISTERED_COMPONENTS.add(component);
        return component;
    }

    /**
     * Registers a new <code>IItemCategory</code>.
     *
     * @param category the <code>IItemCategory</code> to register.
     * @return the <code>IItemCategory</code>.
     */
    public static <T extends Enum<T>> Class<T> registerCategory(Class<T> category) {
        IItemCategory[] categories = new IItemCategory[category.getEnumConstants().length];
        for (int i = 0; i < categories.length; i++) {
            categories[i] = (IItemCategory) category.getEnumConstants()[i];
        }
        REGISTERED_CATEGORIES.add(categories);
        return category;
    }

    /**
     * Registers a new <code>IItemRarity</code>.
     *
     * @param rarity the <code>IItemRarity</code> to register.
     * @return the <code>IItemRarity</code>.
     */
    public static <T extends Enum<T>> Class<T> registerRarity(Class<T> rarity) {
        IItemRarity[] categories = new IItemRarity[rarity.getEnumConstants().length];
        for (int i = 0; i < categories.length; i++) {
            categories[i] = (IItemRarity) rarity.getEnumConstants()[i];
        }
        REGISTERED_RARITIES.add(categories);
        return rarity;
    }

    // ----- GETTERS / SETTERS -----

    /**
     * Gets all <code>IItemCategory</code> of registered <code>ICustomItem</code>'s
     *
     * @return an array of <code>IItemCategory</code>.
     */
    public static IItemCategory[] getCustomItemCategories() {
        return ITEMS_SORTED_CATEGORY.keySet().toArray(new IItemCategory[0]);
    }

    /**
     * Get all <code>CustomItem</code>'s in the given <code>IItemCategory</code>.
     *
     * @param category the <code>IItemCategory</code> to get <code>CustomItem</code>'s from.
     * @return an array of <code>CustomItem</code> names.
     */
    public static String[] getItemNamesInCategory(IItemCategory category) {
        Map<String, CustomItem> items = ITEMS_SORTED_CATEGORY.get(category);
        return items.keySet().toArray(new String[0]);
    }

    /**
     * Gets a <code>ICustomItem</code> from an <code>ItemStack</code>.
     *
     * @param item the <code>ItemStack</code>.
     * @return the <code>ICustomItem</code>, or null if it was not found.
     */
    public static ICustomItem getCustomItem(ItemStack item) { // Get persistent data container of item
        for (ICustomItem customItem : INBUILT_CUSTOM_ITEMS.values()) {
            if (customItem.compareTo(item)) return customItem;
        }
        return null;
    }

    /**
     * Get all <code>ICustomItem</code>'s from a <code>Player</code>'s inventory.
     *
     * @param player the <code>Player</code>.
     * @return an array of <code>ICustomItem</code>'s.
     */
    public static ICustomItem[] getPlayerCustomItems(Player player) {
        List<ICustomItem> items = new ArrayList<>();

        for (ItemStack item : player.getInventory()) {
            ICustomItem customItem = getCustomItem(item);
            if (customItem != null) items.add(customItem);
        }

        return items.toArray(new ICustomItem[0]);
    }

    /**
     * Get all <code>ICustomItem</code>'s from a <code>Player</code>'s main hand and offhand.
     *
     * @param player the <code>Player</code>.
     * @return an array of <code>CustomItem</code>'s.
     */
    public static ICustomItem[] getPlayerHeldCustomItem(Player player) {
        ICustomItem itemMainHand = getCustomItem(player.getInventory().getItemInMainHand());
        ICustomItem itemOffHand = getCustomItem(player.getInventory().getItemInOffHand());

        return new ICustomItem[] {itemMainHand, itemOffHand};
    }

    /**
     * Creates a player head from a give command.
     *
     * @param giveCommand the /give command to generate a player head from. Use https://minecraft-heads.com/ to get /give commands.
     * @return the created player head.
     */
    public static ItemStack createPlayerHead(String giveCommand) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        head = Bukkit.getUnsafe().modifyItemStack(head, giveCommand.substring(30, giveCommand.length() - 2));

        return head;
    }

    /**
     * Gets the internal name of an item.
     *
     * @param stack the item.
     * @return the internal name of the item.
     */
    public static String getItemName(ItemStack stack) {
        for (CustomItem i : INBUILT_CUSTOM_ITEMS.values()) {
            if (i.compareTo(stack)) return i.getInternalName();
        }

        return stack.getType().name().toLowerCase();
    }

    /**
     * Gets an array of all item names, including vanilla items.
     *
     * @return an array of item names.
     */
    public static String[] getItemNames() {
        List<String> itemNames = new ArrayList<>(INBUILT_ITEMS.keySet());
        for (Material m : Material.values()) itemNames.add(m.name().toLowerCase());
        return itemNames.toArray(new String[0]);
    }

    /**
     * Gets an <code>ItemStack</code> from the registered <code>CustomItem</code>'s and vanilla items.
     *
     * @param name the name of the item.
     * @return an <code>ItemStack</code>.
     */
    public static ItemStack getItemStack(String name) {
        if (name.startsWith("minecraft:")) name = name.substring(0, 9);
        if (name.startsWith("cobalt:")) name = name.substring(0, 7);

        ItemStack stack = INBUILT_ITEMS.get(name);
        if (stack == null && isMaterial(name)) stack = new ItemStack(Material.valueOf(name.toUpperCase()));

        return stack;
    }

    public static boolean isMaterial(String name) {
        for (Material m : Material.values()) {
            if (m.name().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    /**
     * Gets an array of all registered <code>CustomItem</code>'s <code>ItemStack</code>'s.
     *
     * @return an array of <code>ItemStack</code>'s.
     */
    public static ItemStack[] getCustomItemStacks() {
        return INBUILT_ITEMS.values().toArray(new ItemStack[0]);
    }

    /**
     * Gets an <code>CustomItem</code> from the registered <code>CustomItems</code>'s.
     *
     * @param name the name of the item to get.
     * @return the <code>CustomItem</code>.
     */
    public static CustomItem getCustomItem(String name) {
        return INBUILT_CUSTOM_ITEMS.get(name);
    }

    /**
     * Gets an <code>ItemStack</code> from the registered <code>CustomItem</code>'s.
     *
     * @param name the name of the item to get.
     * @return the <code>ItemStack</code>.
     */
    public static ItemStack getCustomItemStack(String name) {
        return INBUILT_ITEMS.get(name);
    }

    /**
     * Gets an array of all registered item names.
     *
     * @return an array of names.
     */
    public static String[] getCustomItemNames() {
        String[] names = new String[INBUILT_ITEMS.size()];
        List<String> keys = new ArrayList<>(INBUILT_ITEMS.keySet());
        for (int i = 0; i < keys.size(); i++) {
            names[i] = keys.get(i);
        }
        return names;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltCore.getInstance());
        Bukkit.getPluginManager().registerEvents(new ItemEventHandler(), CobaltCore.getInstance());
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static CustomItemManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CustomItemManager</code>.
     *
     * @return The object of this class.
     */
    public static CustomItemManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CustomItemManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
