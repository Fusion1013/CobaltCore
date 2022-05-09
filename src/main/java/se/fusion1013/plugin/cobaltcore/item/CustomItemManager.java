package se.fusion1013.plugin.cobaltcore.item;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.Constants;

import java.util.*;

public class CustomItemManager extends Manager implements Listener {

    // ----- VARIABLES -----

    private static final Map<String, ItemStack> INBUILT_ITEMS = new HashMap<>(); // Holds all custom items ITEMSTACKS
    private static final Map<String, CustomItem> INBUILT_CUSTOM_ITEMS = new HashMap<>(); // Holds all custom items CUSTOMITEMS

    // ----- ITEM REGISTERING -----

    // Item used for testing the CustomItem system.
    public static final CustomItem TEST_ITEM = register(new CustomItem.CustomItemBuilder("test_item", Material.DIRT, 1)
            .setCustomName(ChatColor.RESET + "Test Item")
            .addLoreLine("This item is only used to test the CustomItem system.")
            .addShapedRecipe("-*-", "*%*", "-*-", new AbstractCustomItem.ShapedIngredient('*', Material.DIAMOND), new AbstractCustomItem.ShapedIngredient('%', Material.NETHER_STAR))
            .build());

    // Item used for testing the CustomBlock system.
    public static final CustomItem TEST_BLOCK = register(new CustomItem.CustomItemBuilder("test_block", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "Test Block")
            .setCustomModel(10001)
            .build());

    // ----- CONSTRUCTORS -----

    public CustomItemManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RECIPES -----

    @EventHandler
    private void onHangingPlace(HangingPlaceEvent event) { // TODO: Move somewhere else
        if (event.getItemStack().getItemMeta().getPersistentDataContainer().has(Constants.INVISIBLE_KEY)) {
            ItemFrame itemFrame = (ItemFrame) event.getEntity();
            itemFrame.setVisible(false);
            event.getEntity().getPersistentDataContainer().set(Constants.INVISIBLE_KEY, PersistentDataType.BYTE, (byte) 1);
        }
    }

    ShapedRecipe INVISIBLE_ITEM_FRAME = addShapedRecipe(
            getInvisibleItemFrame(),
            "---", "-*-", "---",
            new AbstractCustomItem.ShapedIngredient('-', Material.GLOWSTONE_DUST),
            new AbstractCustomItem.ShapedIngredient('*', Material.ITEM_FRAME)
    );

    private static ItemStack getInvisibleItemFrame() {
        ItemStack stack = new ItemStack(Material.ITEM_FRAME);
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.setDisplayName(ChatColor.WHITE + "Invisible Item Frame");
        meta.getPersistentDataContainer().set(Constants.INVISIBLE_KEY, PersistentDataType.BYTE, (byte) 1);
        stack.setItemMeta(meta);
        return stack;
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
    public static ShapedRecipe addShapedRecipe(ItemStack result, String row1, String row2, String row3, AbstractCustomItem.ShapedIngredient... ingredients) {
        StringBuilder keyString = new StringBuilder("custom.shapeless.");
        for (AbstractCustomItem.ShapedIngredient ingredient : ingredients) keyString.append(ingredient.item.getType().name());

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(CobaltCore.getInstance(), keyString.toString()), result);
        recipe.shape(row1, row2, row3);
        for (AbstractCustomItem.ShapedIngredient ingredient : ingredients) recipe.setIngredient(ingredient.key, ingredient.item);
        CobaltCore.getInstance().getServer().addRecipe(recipe);

        return recipe;
    }

    /**
     * Registers a new <code>ShapelessRecipe</code> for the given <code>ItemStack</code>.
     * @param result the <code>ItemStack</code> to add a recipe for.
     * @param ingredients recipe ingredients.
     * @return the recipe.
     */
    public static ShapelessRecipe addShapelessRecipe(ItemStack result, AbstractCustomItem.ShapelessIngredient... ingredients) {
        StringBuilder keyString = new StringBuilder("custom.shapeless.");
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
        return item;
    }

    // ----- GETTERS / SETTERS -----

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
