package se.fusion1013.plugin.cobaltcore.item;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.category.IItemCategory;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.Constants;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;

import java.util.*;

public class CustomItemManager extends Manager implements Listener {

    // ----- VARIABLES -----

    private static final Map<String, ItemStack> INBUILT_ITEMS = new HashMap<>(); // Holds all custom items ITEMSTACKS
    private static final Map<String, CustomItem> INBUILT_CUSTOM_ITEMS = new HashMap<>(); // Holds all custom items CUSTOMITEMS
    private static final Map<IItemCategory, Map<String, CustomItem>> ITEMS_SORTED_CATEGORY = new HashMap<>(); // Holds all custom items sorted by IItemCategory

    // ----- ITEM REGISTERING -----

    // Item used for testing the CustomItem system.
    public static final CustomItem TEST_ITEM = register(new CustomItem.CustomItemBuilder("test_item", Material.DIRT, 1)
            .setCustomName(ChatColor.RESET + "Test Item")
            .addLoreLine("This item is only used to test the CustomItem system.")
            .addShapedRecipe("-*-", "*%*", "-*-", new AbstractCustomItem.ShapedIngredient('*', Material.DIAMOND), new AbstractCustomItem.ShapedIngredient('%', Material.NETHER_STAR))
            .addItemActivator(ItemActivator.PLAYER_ACTIVATE_SNEAK, (item, event, slot) -> {
                PlayerToggleSneakEvent sneakEvent = (PlayerToggleSneakEvent) event;
                sneakEvent.getPlayer().sendMessage("U DO DE SNEAK");
            })
            .build());

    // Item used for testing the CustomBlock system.
    public static final CustomItem MIXING_CAULDRON = register(new CustomItem.CustomItemBuilder("test_block", Material.CLOCK, 1)
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
    public static CustomItem register(CustomItem item){
        INBUILT_ITEMS.put(item.getInternalName(), item.getItemStack());
        INBUILT_CUSTOM_ITEMS.put(item.getInternalName(), item);
        ITEMS_SORTED_CATEGORY.computeIfAbsent(item.getItemCategory(), k -> new HashMap<>()).put(item.getInternalName(), item);
        return item;
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
