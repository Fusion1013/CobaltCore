package se.fusion1013.plugin.cobaltcore.manager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;

import java.util.*;

public class CustomItemManager extends Manager {

    // ----- VARIABLES -----

    private static final Map<String, ItemStack> INBUILT_ITEMS = new HashMap<>(); // Holds all custom items ITEMSTACKS
    private static final Map<String, CustomItem> INBUILT_CUSTOM_ITEMS = new HashMap<>(); // Holds all custom items CUSTOMITEMS

    // ----- ITEM REGISTERING -----

    // Item used for testing the CustomItem system.
    public static final CustomItem TEST_ITEM = register(new CustomItem.CustomItemBuilder("test_item", Material.NETHER_STAR, 1)
            .setCustomName(ChatColor.RESET + "Test Item")
            .addLoreLine("This item is only used to test the CustomItem system.")
            .build());

    // ----- CONSTRUCTORS -----

    public CustomItemManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- Logic -----

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

    private static boolean isMaterial(String name) {
        for (Material m : Material.values()) {
            if (m.name().equals(name)) return true;
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
