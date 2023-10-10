package se.fusion1013.plugin.cobaltcore.item;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.*;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.event.PlayerHeldItemTickEvent;
import se.fusion1013.plugin.cobaltcore.item.loaders.ItemLoader;
import se.fusion1013.plugin.cobaltcore.item.section.ItemSection;
import se.fusion1013.plugin.cobaltcore.item.section.ItemSectionManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.*;

import java.util.*;

public class CustomItemManager extends Manager implements Listener {

    // ----- VARIABLES -----

    private static final Map<String, ItemStack> INBUILT_ITEMS = new HashMap<>(); // Holds all custom items ITEMSTACKS
    private static final Map<String, ICustomItem> INBUILT_CUSTOM_ITEMS = new HashMap<>(); // Holds all custom items CUSTOMITEMS
    private static final Map<ItemSection, Map<String, ICustomItem>> ITEMS_SORTED_CATEGORY = new HashMap<>(); // Holds all custom items sorted by IItemCategory

    // ----- CONSTRUCTORS -----

    public CustomItemManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
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
        if (item.getItemCategory() != null) ITEMS_SORTED_CATEGORY.computeIfAbsent(item.getItemCategory(), k -> new HashMap<>()).put(item.getInternalName(), item);
        return item;
    }

    public static ICustomItem register(INameProvider item) {
        return register((ICustomItem) item);
    }

    // ----- GETTERS / SETTERS -----

    public static ItemSection[] getCustomItemCategories() {
        return ITEMS_SORTED_CATEGORY.keySet().toArray(new ItemSection[0]);
    }

    public static String[] getItemNamesInCategory(ItemSection category) {
        Map<String, ICustomItem> items = ITEMS_SORTED_CATEGORY.get(category);
        return items.keySet().toArray(new String[0]);
    }

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
    public static String getInternalItemName(ItemStack stack) {
        for (ICustomItem i : INBUILT_CUSTOM_ITEMS.values()) {
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

    public static ItemStack[] getCustomItemStacks() {
        return INBUILT_ITEMS.values().toArray(new ItemStack[0]);
    }
    public static ICustomItem getCustomItem(String name) {
        return INBUILT_CUSTOM_ITEMS.get(name);
    }
    public static ItemStack getCustomItemStack(String name) {
        return INBUILT_ITEMS.get(name);
    }

    public static String[] getCustomItemNames() {
        String[] names = new String[INBUILT_ITEMS.size()];
        List<String> keys = new ArrayList<>(INBUILT_ITEMS.keySet());
        for (int i = 0; i < keys.size(); i++) {
            names[i] = keys.get(i);
        }
        return names;
    }

    // ----- ITEM FILE LOADING -----

    public static void loadItemFiles(CobaltPlugin plugin, boolean overwrite) {

        FileUtil.loadFilesInto(plugin, "items/", new IProviderStorage() {
            @Override
            public void put(String key, INameProvider provider) {
                register(provider);
            }

            @Override
            public boolean has(String key) {
                return getCustomItem(key) != null;
            }

            @Override
            public INameProvider get(String key) {
                return getCustomItem(key);
            }
        }, new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return ItemLoader.Load(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return ItemLoader.Load(json);
            }
        }, overwrite);
    }

    // ----- RELOADING / DISABLING -----

    public static void reloadItems() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            ItemSectionManager.load(plugin, true);
            loadItemFiles(plugin, true);
        }
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltCore.getInstance());
        Bukkit.getPluginManager().registerEvents(new ItemEventHandler(), CobaltCore.getInstance());

        // loadItemFiles(CobaltCore.getInstance(), false);

        // Runnable
        Bukkit.getScheduler().runTaskTimer(CobaltCore.getInstance(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ICustomItem[] items = getPlayerHeldCustomItem(p);
                if (items[0] != null) items[0].activatorTriggeredSync(ItemActivator.HELD_TICK, new PlayerHeldItemTickEvent(p), EquipmentSlot.HAND);
                if (items[1] != null) items[1].activatorTriggeredSync(ItemActivator.HELD_TICK, new PlayerHeldItemTickEvent(p), EquipmentSlot.OFF_HAND);

                for (ICustomItem item : getPlayerCustomItems(p))
                    if (item != null)
                        item.activatorTriggeredSync(ItemActivator.TICK, new PlayerHeldItemTickEvent(p), null);
            }
        }, 0, 1);
    }

    @Override
    public void disable() {

    }

    // ----- EVENTS -----

    @EventHandler
    public void inventoryEvent(PlayerItemHeldEvent event) {
        ItemUtil.fixItems(event.getPlayer().getInventory());
    }

    @EventHandler
    public void chestOpenEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block.getState() instanceof Container container) ItemUtil.fixItems(container);
        }
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
