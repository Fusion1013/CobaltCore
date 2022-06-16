package se.fusion1013.plugin.cobaltcore.world.block;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandHandler;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.*;

public class CustomBlockManager extends Manager implements CommandExecutor, Listener {

    // ----- VARIABLES -----

    private static Map<String, CustomBlock> registeredBlocks = new HashMap<>();
    private static Map<Location, PlacedBlock> placedBlocks = new HashMap<>();

    // ----- REGISTER -----

    public static CustomBlock TEST_BLOCK = register(new CustomBlock(CustomItemManager.TEST_BLOCK, Material.END_ROD));

    /**
     * Registers a new <code>CustomBlock</code>.
     *
     * @param block the <code>CustomBlock</code> to register.
     * @return the <code>CustomBlock</code>.
     */
    public static CustomBlock register(CustomBlock block) {
        return registeredBlocks.put(block.getInternalName(), block);
    }

    // ----- BLOCK PLACING -----

    /**
     * Places a <code>CustomBlock</code> at the <code>Location</code>.
     *
     * @param blockName the internal name of the <code>CustomBlock</code> to place.
     * @param location the <code>Location</code> to place the <code>CustomBlock</code>.
     */
    @CommandHandler(
            parameterNames = {"name", "location"},
            commandSuggestionMethods = {"getInternalNames", ""}
    )
    public static CommandResult place(String blockName, Location location) {
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("block", blockName)
                .addPlaceholder("location", location)
                .build();

        CustomBlock block = registeredBlocks.get(blockName);
        if (block == null || location == null) {
            return CommandResult.FAILED.setDescription(LocaleManager.getInstance().getLocaleMessage("block.could_not_place", placeholders));
        }

        // Check the map to see if there already is a custom block at the location. If there is, remove it.
        if (placedBlocks.get(location.toBlockLocation()) != null) placedBlocks.remove(location.toBlockLocation()).stand.remove();

        // Place the block and add it to the map
        ArmorStand stand = block.placeBlock(location);
        placedBlocks.put(location.toBlockLocation(), new PlacedBlock(stand, block));
        return CommandResult.SUCCESS.setDescription(LocaleManager.getInstance().getLocaleMessage("block.placed", placeholders));
    }


    List<UUID> placeDelay = new ArrayList<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            // Check if the item in their hand is a custom block

            ItemStack item = event.getItem();
            if (item == null) return;

            String itemName = CustomItemManager.getItemName(item);
            if (CustomItemManager.isMaterial(itemName)) return;

            CustomBlock block = registeredBlocks.get(itemName);
            if (block != null) {
                Block clickedBlock = event.getClickedBlock();
                if (clickedBlock == null) return;

                Location placeLocation = clickedBlock.getLocation();
                BlockFace face = event.getBlockFace();

                // Check if the place location is buildable, if not shift the position according to the clicked block face
                if (!placeLocation.getBlock().isBuildable()) {
                    switch (face) {
                        case DOWN -> placeLocation.add(new Vector(0, -1, 0));
                        case UP -> placeLocation.add(new Vector(0, 1, 0));
                        case NORTH -> placeLocation.add(new Vector(0, 0, -1));
                        case SOUTH -> placeLocation.add(new Vector(0, 0, 1));
                        case WEST -> placeLocation.add(new Vector(-1, 0, 0));
                        case EAST -> placeLocation.add(new Vector(1, 0, 0));
                    }
                }

                // Check if the place location is buildable, if not return
                if (!placeLocation.getBlock().isBuildable()) return;

                // Check if the player is on place delay
                if (placeDelay.contains(event.getPlayer().getUniqueId())) return;

                // Remove block from players inventory if player is not in creative, and then place the block
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) PlayerUtil.reduceHeldItemStack(event.getPlayer(), 1);
                place(itemName, placeLocation);

                // Add the player to the place delay list and schedule an event to remove them after the delay
                placeDelay.add(event.getPlayer().getUniqueId());
                CobaltCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltCore.getInstance(), () -> placeDelay.remove(event.getPlayer().getUniqueId()), 1);

                event.setCancelled(true);
            }
        }
    }

    // ----- BLOCK BREAKING -----

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        World world = location.getWorld();
        if (world == null) return;

        if (placedBlocks.get(location.toBlockLocation()) != null) {
            // Remove the Custom Block
            PlacedBlock block = placedBlocks.remove(location.toBlockLocation());
            block.stand.remove();

            // Determine if the block should drop as an item
            if (event.isDropItems() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                world.dropItem(location.toBlockLocation().clone().add(new Vector(.5, .5, .5)), block.block.getBlockItem().getItemStack());
            }

            // Remove normal block drop
            event.setDropItems(false);
        }
    }

    // ----- BLOCK INFO -----

    /**
     * Gets an array of registered <code>CustomBlock</code> internal names.
     *
     * @return an array of internal names.
     */
    public static String[] getInternalNames() {
        return registeredBlocks.keySet().toArray(new String[0]);
    }

    // ----- CONSTRUCTORS -----

    public CustomBlockManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CommandManager.getInstance().registerCommandModule("cblock", this);
        Bukkit.getPluginManager().registerEvents(this, CobaltCore.getInstance());
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static CustomBlockManager INSTANCE = null;
    /**
     * Returns the object representing this <code>BlockManager</code>.
     *
     * @return The object of this class.
     */
    public static CustomBlockManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CustomBlockManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    // ----- PLACED BLOCK STRUCT -----

    private static class PlacedBlock {
        ArmorStand stand;
        CustomBlock block;

        PlacedBlock(ArmorStand stand, CustomBlock block) {
            this.stand = stand;
            this.block = block;
        }
    }

}
