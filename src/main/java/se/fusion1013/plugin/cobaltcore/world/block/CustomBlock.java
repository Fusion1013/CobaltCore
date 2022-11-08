package se.fusion1013.plugin.cobaltcore.world.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;

public class CustomBlock {

    // ----- VARIABLES -----

    final String internalName;
    final ICustomItem blockItem;
    final Material blockMaterial;

    // ----- CONSTRUCTORS -----

    public CustomBlock(ICustomItem blockItem, Material blockMaterial) {
        this.internalName = blockItem.getInternalName();
        this.blockItem = blockItem;
        this.blockMaterial = blockMaterial;
    }

    // ----- LOGIC -----

    /**
     * Places the <code>CustomBlock</code> at the <code>Location</code>.
     *
     * @param location the <code>Location</code> to place the <code>CustomBlock</code> at.
     * @return the <code>ArmorStand</code> that was placed with the block model on it.
     */
    public ArmorStand placeBlock(Location location) {
        if (location == null) return null;

        World world = location.getWorld();
        if (world == null) return null;

        // Place the "fake" block, and play a place sound
        location.getBlock().setType(blockMaterial);
        world.playSound(location, location.getBlock().getBlockSoundGroup().getPlaceSound(), 1, 1);

        // Create a new location at the center of the block to make sure the armor stand is centered
        return world.spawn(location.toBlockLocation().add(new Vector(.5, 0, .5)), ArmorStand.class, armorStand -> {
            armorStand.setMarker(true);
            armorStand.setInvisible(true);
            armorStand.setItem(EquipmentSlot.HEAD, blockItem.getItemStack());
        });
    }

    // ----- GETTERS / SETTERS -----

    public String getInternalName() {
        return internalName;
    }

    public ICustomItem getBlockItem() {
        return blockItem;
    }

    public Material getBlockMaterial() {
        return blockMaterial;
    }
}
