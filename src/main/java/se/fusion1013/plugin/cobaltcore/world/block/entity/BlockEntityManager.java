package se.fusion1013.plugin.cobaltcore.world.block.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandHandler;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.world.block.BlockPlacementManager;

import java.util.*;

public class BlockEntityManager extends Manager implements CommandExecutor {

    // ----- VARIABLES -----

    Map<UUID, BlockEntityCollection> blockEntityCollectionMap = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public BlockEntityManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- CREATION -----

    @CommandHandler(
            parameterNames = {"location","width","height","depth"}
    )
    public CommandResult createBlockEntityCollection(Location location, int width, int height, int depth) {
        BlockEntityCollection collection = new BlockEntityCollection(location, width, height, depth);
        blockEntityCollectionMap.put(collection.getUuid(), collection);
        return CommandResult.SUCCESS;
    }

    /**
     * Creates a new <code>BlockEntityCollection</code> at the location from the given materials.
     *
     * @param location the <code>Location</code> of the corner.
     * @param materials the materials the <code>BlockEntityCollection</code> should be made out of.
     */
    public BlockEntityCollection createBlockEntityCollection(Location location, Material[][][] materials) {
        BlockEntityCollection collection = new BlockEntityCollection(location, materials);
        blockEntityCollectionMap.put(collection.getUuid(), collection);
        return collection;
    }

    public void removeBlockEntityCollection(UUID uuid) {
        BlockEntityCollection collection = blockEntityCollectionMap.get(uuid);
        collection.removeBlockEntities();
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        // Resets the tick of all currently active block entities
        Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltCore.getInstance(), () -> {

            for (BlockEntityCollection collection : blockEntityCollectionMap.values()) collection.resetTicks();

        }, 0, 1);

        CommandManager.getInstance().registerCommandModule("block_entity", this);
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static BlockEntityManager INSTANCE = null;
    /**
     * Returns the object representing this <code>BlockEntityManager</code>.
     *
     * @return The object of this class.
     */
    public static BlockEntityManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new BlockEntityManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

}
