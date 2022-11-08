package se.fusion1013.plugin.cobaltcore.item.loot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomLootTable {

    // ----- VARIABLES -----

    LootTarget[] targets;
    LootPool[] pools;

    // ----- CONSTRUCTORS -----

    public CustomLootTable(LootTarget[] targets, LootPool... pools) {
        this.targets = targets;
        this.pools = pools;
    }

    // ----- LOOT GETTING -----

    public List<ItemStack> getLoot(int maxItems) {
        Random r = new Random();

        // Get all items to populate the inventory with
        List<ItemStack> items = new ArrayList<>();

        for (LootPool pool : pools) {
            for (int i = 0; i < pool.rolls(); i++) {
                if (items.size() >= maxItems) continue;
                items.add(pool.entries()[r.nextInt(pool.entries().length)].getStack(r)); // TODO: Add weights
            }
        }
        return items;
    }


    // ----- LOOT INSERTION -----

    /**
     * Drops loot from the <code>CustomLootTable</code> at the specified <code>Location</code>.
     *
     * @param location the <code>Location</code> to drop the loot at.
     */
    public void dropLoot(Location location) {
        for (ItemStack stack : getLoot(Integer.MAX_VALUE)) location.getWorld().dropItemNaturally(location, stack);
    }

    /**
     * Inserts loot into a loot container at the specified <code>Location</code>.
     * The container must match one of the <code>CustomLootTable</code>'s <code>LootTarget</code>'s.
     *
     * @param location the <code>Location</code> to insert the loot at.
     */
    public void insertLoot(Location location) {
        Random r = new Random();

        Block block = location.getBlock();
        if (block.getState() instanceof Container container) {

            // Check if the block type matches with one of the insertion types
            boolean match = false;
            for (LootTarget target : targets) {
                for (Material mat : target.getMaterials()) {
                    if (mat == block.getType()) {
                        match = true;
                        break;
                    }
                }
                if (match) break;
            }
            if (!match) return;

            // Get all items to populate the inventory with
            List<ItemStack> items = getLoot(container.getInventory().getSize());

            // Fill the inventory
            insertItems(r, container, items);
        }
    }

    public static void insertItemsFromNames(Random r, Container container, List<String> itemStrings) {
        for (String item : itemStrings) {
            insertItem(CustomItemManager.getItemStack(item), container, r);
        }
    }

    public static void insertItems(Random r, Container container, List<ItemStack> items) {
        // Fill the inventory
        for (ItemStack item : items) {
            insertItem(item, container, r);
        }
    }

    // Inserts an item without replacing current items, instead increasing item stack size
    private static void insertItem(ItemStack item, Container container, Random r) {
        int index = r.nextInt(container.getInventory().getSize());
        ItemStack currentItem = container.getInventory().getItem(index);
        ICustomItem customItem = CustomItemManager.getCustomItem(item);

        if (currentItem == null) container.getInventory().setItem(index, item);
        else if (customItem != null) if (customItem.compareTo(currentItem)) currentItem.setAmount(currentItem.getAmount() + item.getAmount());
        else if (item.getType() == currentItem.getType()) currentItem.setAmount(currentItem.getAmount() + item.getAmount());
        else insertItem(item, container, r);
    }

    // ----- GETTERS / SETTERS -----

    public LootPool[] getPools() {
        return pools;
    }

    // ----- TYPE ENUM -----

    public enum LootTarget {
        CHEST(Material.CHEST),
        BARREL(Material.BARREL),
        BREWERY(Material.BREWING_STAND),
        SHULKER_BOX(Material.SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.BLACK_SHULKER_BOX),
        DROP(Material.AIR),
        OTHER(Material.AIR);

        final Material[] materials;

        LootTarget(Material... materials) {
            this.materials = materials;
        }

        public Material[] getMaterials() {
            return materials;
        }
    }

}
