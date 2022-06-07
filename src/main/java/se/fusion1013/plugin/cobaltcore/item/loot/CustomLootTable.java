package se.fusion1013.plugin.cobaltcore.item.loot;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomLootTable {

    // ----- VARIABLES -----

    String type;
    LootPool[] pools;

    // ----- CONSTRUCTORS -----

    public CustomLootTable(String type, LootPool... pools) {
        this.type = type;
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

}
