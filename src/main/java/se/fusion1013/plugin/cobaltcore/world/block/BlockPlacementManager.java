package se.fusion1013.plugin.cobaltcore.world.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.IStructureModule;

import java.util.LinkedList;
import java.util.Queue;

public class BlockPlacementManager extends Manager implements Runnable {

    // ----- VARIABLES -----

    private static final Queue<IBlockQueueWrapper> QUEUE = new LinkedList<>();

    int blockPlacementLimit = 5000; // TODO: Load from config
    int ticksWithExceedingTasks = 0; // The number of consecutive tasks where the number of blocks to place have exceeded the limit.

    // ----- CONSTRUCTORS -----

    public BlockPlacementManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- GETTERS / SETTERS -----

    public static void addQueueItem(IBlockQueueWrapper wrapper) {
        QUEUE.add(wrapper);
    }

    /**
     * Add a <code>IStructureModule</code> to the queue.
     *
     * @param location the <code>Location</code> to execute the module at.
     * @param holder the <code>StructureHolder</code>.
     * @param seed the seed.
     * @param module the <code>IStructureModule</code> to add to the queue.
     */
    public static void addStructureModule(Location location, StructureUtil.StructureHolder holder, long seed, IStructureModule module) {
        QUEUE.add(new StructureModuleWrapper(location, holder, seed, module));
    }

    /**
     * Adds a new block to the queue.
     *
     * @param material the material of the block.
     * @param data the data of the block.
     * @param location the location to place the block at.
     */
    public static void addBlock(Material material, BlockData data, Location location) {
        QUEUE.add(new BlockPlacementWrapper(material, data, location));
    }

    /**
     * Adds a new block to the queue.
     *
     * @param material the material of the block.
     * @param location the location to place the block at.
     */
    public static void addBlock(Material material, Location location) {
        QUEUE.add(new BlockPlacementWrapper(material, location));
    }

    // ----- TASK -----

    @Override
    public void run() {
        executeQueue();
    }

    /**
     * Executes as many objects in the queue as possible.
     */
    private void executeQueue() {
        if (QUEUE.isEmpty()) {
            ticksWithExceedingTasks = 0;
            return;
        }

        if (QUEUE.size() > blockPlacementLimit) ticksWithExceedingTasks += 1;

        for (int i = 0; i < blockPlacementLimit; i++) {
            IBlockQueueWrapper wrapper = QUEUE.poll(); // TODO: Check size of task and increase i by that amount
            if (wrapper == null) return;
            wrapper.execute();
            i += Math.max(0, wrapper.getTaskSize() - 1);
        }
    }


    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        // Schedule the block placing task
        CobaltCore.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(CobaltCore.getInstance(), this, 1, 1);
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static BlockPlacementManager INSTANCE = null;
    /**
     * Returns the object representing this <code>BlockPlacementManager</code>.
     *
     * @return The object of this class.
     */
    public static BlockPlacementManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new BlockPlacementManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    // ----- WRAPPERS -----

    public static class BlockPlacementWrapper implements IBlockQueueWrapper {

        // ----- VARIABLES -----

        Material material;
        BlockData data;
        Location location;

        // ----- CONSTRUCTORS -----

        public BlockPlacementWrapper(Material material, BlockData data, Location location) {
            this.material = material;
            this.data = data;
            this.location = location;
        }

        public BlockPlacementWrapper(Material material, Location location) {
            this.material = material;
            this.data = material.createBlockData();
            this.location = location;
        }

        @Override
        public void execute() {
            location.getBlock().setType(material);
            location.getBlock().setBlockData(data);
        }

        @Override
        public int getTaskSize() {
            return 1;
        }
    }

    public static class StructureModuleWrapper implements IBlockQueueWrapper {

        // ----- VARIABLES -----

        Location location;
        StructureUtil.StructureHolder holder;
        long seed;
        IStructureModule module;

        // ----- CONSTRUCTORS -----

        public StructureModuleWrapper(Location location, StructureUtil.StructureHolder holder, long seed, IStructureModule module) {
            this.location = location;
            this.holder = holder;
            this.seed = seed;
            this.module = module;
        }

        // ----- METHODS -----

        @Override
        public void execute() {
            module.executeWithSeed(location, holder, seed);
        }

        @Override
        public int getTaskSize() {
            return holder.width * holder.height * holder.width;
        }
    }

}
