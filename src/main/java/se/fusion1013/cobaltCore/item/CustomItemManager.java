package se.fusion1013.cobaltCore.item;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.events.PlayerHeldItemTickEvent;
import se.fusion1013.cobaltCore.item.loaders.ItemLoader;
import se.fusion1013.cobaltCore.item.section.ItemSection;
import se.fusion1013.cobaltCore.item.section.ItemSectionManager;
import se.fusion1013.cobaltCore.item.toggles.ItemToggleType;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemManager extends Manager<CobaltCore> implements Listener {

    // ----- VARIABLES -----

    private static final Map<String, ICustomItem> INBUILT_CUSTOM_ITEMS = new HashMap<>(); // Holds all custom items CUSTOMITEMS
    private static final Map<ItemSection, Map<String, ICustomItem>> ITEMS_SORTED_CATEGORY = new HashMap<>(); // Holds all custom items sorted by IItemCategory

    // ----- CONSTRUCTOR -----

    public CustomItemManager(CobaltCore plugin) {
        super(plugin);
        INSTANCE = this;
    }


    // ----- REGISTER -----

    /**
     * Registers a new <code>CustomItem</code>.
     *
     * @param item the <code>CustomItem</code> to register.
     * @return the <code>CustomItem</code>.
     */
    public static ICustomItem register(ICustomItem item) {
        INBUILT_CUSTOM_ITEMS.put(item.getInternalName(), item);
        if (item.getItemCategory() != null)
            ITEMS_SORTED_CATEGORY.computeIfAbsent(item.getItemCategory(), k -> new HashMap<>()).put(item.getInternalName(), item);
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

        return new ICustomItem[]{itemMainHand, itemOffHand};
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
        List<String> itemNames = new ArrayList<>(INBUILT_CUSTOM_ITEMS.keySet());
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

        ICustomItem customItem = INBUILT_CUSTOM_ITEMS.get(name);
        if (customItem == null) return new ItemStack(Material.valueOf(name.toUpperCase()));
        ItemStack stack = customItem.getItemStack();
        if (stack == null && isMaterial(name)) stack = new ItemStack(Material.valueOf(name.toUpperCase()));

        return stack;
    }

    public static boolean isMaterial(String name) {
        for (Material m : Material.values()) {
            if (m.name().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public static ICustomItem getCustomItem(String name) {
        return INBUILT_CUSTOM_ITEMS.get(name);
    }

    public static ItemStack getCustomItemStack(String name) {
        ICustomItem customItem = INBUILT_CUSTOM_ITEMS.get(name);
        if (customItem == null) return null;
        return customItem.getItemStack();
    }

    public static String[] getCustomItemNames() {
        String[] names = new String[INBUILT_CUSTOM_ITEMS.size()];
        List<String> keys = new ArrayList<>(INBUILT_CUSTOM_ITEMS.keySet());
        for (int i = 0; i < keys.size(); i++) {
            names[i] = keys.get(i);
        }
        return names;
    }

    public static String[] getCgiveSuggestions() {
        List<String> keys = new ArrayList<>();
        INBUILT_CUSTOM_ITEMS.forEach((key, value) -> {
            if (!value.getItemToggles().getValue(ItemToggleType.CgiveHide)) keys.add(key);
        });
        return keys.toArray(new String[0]);
    }

    public static JSONArray getCustomItemJson() {
        JSONArray jsonArray = new JSONArray();
        for (ICustomItem item : INBUILT_CUSTOM_ITEMS.values()) {
            JSONObject itemJson = item.toJson();
            jsonArray.add(itemJson);
        }
        return jsonArray;
    }

    public static YamlConfiguration getCustomItemYaml() {
        YamlConfiguration configuration = new YamlConfiguration();
        for (ICustomItem item : INBUILT_CUSTOM_ITEMS.values()) {
            configuration.set(item.getInternalName(), item.toYaml());
        }
        return configuration;
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
                return ItemLoader.loadItem(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return ItemLoader.loadItem(json);
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
        createItemTickHandler();
    }

    private void createItemTickHandler() {
        // Runnable
        Bukkit.getScheduler().runTaskTimer(CobaltCore.getInstance(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ICustomItem[] items = getPlayerHeldCustomItem(p);

                Map<String, Object> context = new HashMap<>();
                context.put("default_entity", p);
                context.put("default_location", p.getLocation());

                if (items[0] != null) {
                    items[0].activatorTriggeredSync(ItemActivator.HELD_TICK, new PlayerHeldItemTickEvent(p), EquipmentSlot.HAND, context);
                    checkItemHeavy(p, items[0]);
                }
                if (items[1] != null)
                    items[1].activatorTriggeredSync(ItemActivator.HELD_TICK, new PlayerHeldItemTickEvent(p), EquipmentSlot.OFF_HAND, context);

                for (ICustomItem item : getPlayerCustomItems(p))
                    if (item != null)
                        item.activatorTriggeredSync(ItemActivator.TICK, new PlayerHeldItemTickEvent(p), null);
            }
        }, 0, 1);
    }

    private void checkItemHeavy(Player player, ICustomItem item) {
        if (!item.getItemToggles().getValue(ItemToggleType.Heavy)) return;
        if (player.hasPotionEffect(PotionEffectType.STRENGTH) && player.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier() >= 2)
            return;

        if (!player.hasPotionEffect(PotionEffectType.SLOWNESS))
            player.sendMessage(Component.text("You don't feel strong enough to wield this..").decoration(TextDecoration.ITALIC, true).color(NamedTextColor.GRAY));

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 4 * 20, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 9));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 9));
    }

    @Override
    public void disable() {

    }

    // ----- EVENTS -----

    @EventHandler
    public void inventoryEvent(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        if (clickedInventory.getType() == InventoryType.PLAYER) return;

        ItemStack item = event.getCursor();
        if (item.getType() == Material.AIR) return;

        ICustomItem customItem = getCustomItem(item);
        if (customItem == null) return;

        if (!customItem.getItemToggles().getValue(ItemToggleType.DenyContainer)) return;

        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player player) {
            LocaleManager.getInstance().sendMessage("", player, "core.custom_item.deny_container");
        }
    }

    @EventHandler
    public void playerItemHeldEvent(PlayerItemHeldEvent event) {
        ItemUtil.fixItems(event.getPlayer().getInventory());
    }

    @EventHandler
    public void itemDestroyEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item item) {
            ItemStack itemStack = item.getItemStack();
            ICustomItem customItem = getCustomItem(itemStack);
            if (customItem != null && customItem.getItemToggles().getValue(ItemToggleType.Invulnerable)) {
                item.setHealth(20);
            }
        }
    }

    @EventHandler
    public void itemSpawnEvent(ItemSpawnEvent event) {
        // Fix the item
        ItemUtil.fixItem(event.getEntity());

        ICustomItem customItem = getCustomItem(event.getEntity().getItemStack());
        if (customItem == null) return;

        // Set attributes of item entity
        if (customItem.getItemToggles().getValue(ItemToggleType.Glowing)) event.getEntity().setGlowing(true);
        if (customItem.getItemToggles().getValue(ItemToggleType.Persistent)) event.getEntity().setWillAge(false);
        if (customItem.getItemToggles().getValue(ItemToggleType.Invulnerable)) event.getEntity().setInvulnerable(true);
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
    public static CustomItemManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomItemManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
