package se.fusion1013.plugin.cobaltcore.item.loot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;

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
            List<ItemStack> items = new ArrayList<>();

            for (LootPool pool : pools) {
                for (int i = 0; i < pool.rolls(); i++) {
                    if (items.size() >= container.getInventory().getSize()) continue;
                    items.add(pool.entries()[r.nextInt(pool.entries().length)].getStack(r)); // TODO: Add weights
                }
            }

            // Fill the inventory
            for (ItemStack item : items) {
                int index = r.nextInt(container.getInventory().getSize());
                container.getInventory().setItem(index, item);
            }
        }
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
