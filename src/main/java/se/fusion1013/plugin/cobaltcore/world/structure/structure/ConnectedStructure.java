package se.fusion1013.plugin.cobaltcore.world.structure.structure;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.IStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModuleType;

import java.util.*;

public class ConnectedStructure extends SimpleStructure implements IStructure {

    // ----- VARIABLES -----

    int depth;
    IStructure[] structures;

    // Extra
    IStructure initialStructure;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>ConnectedStructure</code>.
     *
     * @param ownerPlugin the plugin that owns the structure.
     * @param id the id of the structure.
     * @param structureName the name of the structure.
     * @param depth the size of the <code>ConnectedStructure</code>. Higher values increases the size of the structure exponentially.
     * @param structures the structures that can generate in the <code>ConnectedStructure</code>.
     */
    public ConnectedStructure(Plugin ownerPlugin, int id, String structureName, int depth, IStructure... structures) {
        super(ownerPlugin, id, structureName, "");

        List<IStructure> rotatedStructures = new ArrayList<>();
        for (IStructure s : structures) {

            rotatedStructures.add(s);
            IStructure rotated = s.getRotatedClone();
            rotatedStructures.add(rotated);
            rotated = s.getRotatedClone();
            rotatedStructures.add(rotated);
            rotated = s.getRotatedClone();
            rotatedStructures.add(rotated);

        }
        this.structures = rotatedStructures.toArray(new IStructure[0]);

        this.depth = depth;
    }

    // ----- STRUCTURE PLACING -----

    private void placeStructure(Location location, IStructure structure, long seed) {
        // Run connected structure pre-processing
        for (IStructureModule module : structureModules) if (module.getModuleType() == StructureModuleType.PRE) module.executeWithSeed(location, structure.getStructureHolder(), seed);

        structure.softForceGenerate(location); // Individual room processing

        // Run connected structure post-processing
        for (IStructureModule module : structureModules) if (module.getModuleType() == StructureModuleType.POST) module.executeWithSeed(location, structure.getStructureHolder(), seed);
    }

    // ----- GENERATION -----

    @Override
    public boolean softForceGenerate(Location location) {
        return attemptGenerate(location, 1);
    }

    @Override
    public boolean attemptGenerate(Location location, double threshold) {
        return attemptGenerate(location, threshold, depth);
    }

    @Override
    public boolean attemptGenerate(Location location, double threshold, int depth) {
        if (!super.canGenerate(location, threshold)) return false;

        // TODO: Move into common method
        // If onGround, shift structure down to ground
        if (onGround) {
            for (int y = location.getBlockY(); y > location.getWorld().getMinHeight(); y--) {
                location.setY(y);

                boolean isGround = true;
                for (Material mat : nonGroundBlocks) {
                    if (location.getBlock().getType() == mat) isGround = false;
                }
                if (isGround) break;
            }
        }

        // Offset
        location.add(offset);

        if (initialStructure == null) generatePart(location, new Vector(), null, new ComplexStructureLayout(), depth);
        else {
            placeStructure(location, initialStructure, location.toBlockKey()); // Place the initial structure

            // Create the layout and insert the initial structure
            ComplexStructureLayout layout = new ComplexStructureLayout();
            layout.insertStructure(initialStructure.getStructureHolder(), new Vector());

            generatePart(location, new Vector(), initialStructure, layout, depth);
        }
        return true;
    }

    private void generatePart(Location startLocation, Vector deltaPos, IStructure previousStructure, ComplexStructureLayout layout, int depth) {

        Random r = new Random();

        // Select first structure if none specified
        // Hard coded for now
        if (previousStructure == null) {

            previousStructure = structures[r.nextInt(structures.length)];

            // Place the structure
            placeStructure(startLocation.clone().add(deltaPos.clone()), previousStructure, startLocation.toBlockKey());

            // Add the structure to the layout
            layout.insertStructure(previousStructure.getStructureHolder(), deltaPos);
        }

        // Loop through all connections in previous structure
        for (StructureUtil.StructureHolder.StructureConnection connection : previousStructure.getStructureHolder().structureConnections) {

            // Attempt to find structures with matching connection in other structures
            List<IStructure> validStructures = getValidStructures(connection, layout, deltaPos);
            if (validStructures.size() <= 0) continue; // TODO: Add option for filling in connections that were not able to be connected with blocks

            // Select random structure from the list
            IStructure newStructure = validStructures.get(r.nextInt(validStructures.size()));

            // Get all valid generation offsets for the structure
            List<Vector> validGenerationOffsets = getValidOffsets(connection, newStructure.getStructureHolder(), layout, deltaPos);
            if (validGenerationOffsets.size() <= 0) continue;

            // Select random generation offset from the list
            Vector generationOffset = validGenerationOffsets.get(r.nextInt(validGenerationOffsets.size()));

            // Create the new deltaPos
            Vector newDeltaPos = generationOffset.clone().add(deltaPos.clone());

            // Generate the structure at the location
            placeStructure(startLocation.clone().add(newDeltaPos), newStructure, startLocation.toBlockKey()); // TODO: Add support for individual room settings
            // newStructure.placeStructure(startLocation.clone().add(newDeltaPos));

            // Add the new structure to the layout
            layout.insertStructure(newStructure.getStructureHolder(), newDeltaPos);

            if (depth > 0) {
                Bukkit.getScheduler().runTaskLater(CobaltCore.getInstance(), () -> {
                    generatePart(startLocation, newDeltaPos, newStructure, layout, depth - 1); // TODO: Generate all connections for this structure before moving on to the next structure
                }, 1);
            }
        }
    }

    private List<IStructure> getValidStructures(StructureUtil.StructureHolder.StructureConnection connection1, ComplexStructureLayout layout, Vector deltaPos) {

        List<IStructure> validStructures = new ArrayList<>();

        // Loop through all structures and check for valid connections
        for (IStructure possibleStructure : structures) {

            // Check if there are valid offsets, if so add the structure to the list
            if (getValidOffsets(connection1, possibleStructure.getStructureHolder(), layout, deltaPos).size() > 0) validStructures.add(possibleStructure);

        }

        return validStructures;
    }

    // These offsets are relative to the previous structure, not the origin location
    private List<Vector> getValidOffsets(StructureUtil.StructureHolder.StructureConnection connection1, StructureUtil.StructureHolder newStructure, ComplexStructureLayout layout, Vector deltaPos) {

        List<Vector> offsets = new ArrayList<>();

        // Loop through all connections in the structure that is to be generated
        for (StructureUtil.StructureHolder.StructureConnection connection2 : newStructure.structureConnections) {

            // Check if the direction of the connection is correct
            BlockFace face1 = connection1.getFace();
            BlockFace face2 = connection2.getFace();

            if (!face1.getDirection().multiply(-1).equals(face2.getDirection())) continue;

            // Check if the size of the connection is correct
            if (!connection2.getDimensions().equals(connection1.getDimensions())) continue;

            // Calculate the offset
            Vector offset = connection1.getRelativePosition().clone().subtract(connection2.getRelativePosition());
            offset.add(face1.getDirection());

            // Check if the structure overlaps
            if (layout.validateStructure(newStructure, offset.clone().add(deltaPos))) offsets.add(offset);
        }

        return offsets;
    }


    private static class ComplexStructureLayout {

        Map<Integer, Map<Integer, Map<Integer, Integer>>> layout = new HashMap<>();

        public ComplexStructureLayout() {}

        public boolean exists(int x, int y, int z) {
            Map<Integer, Map<Integer, Integer>> yz = layout.get(x);
            if (yz == null) return false;

            Map<Integer, Integer> map = yz.get(y);
            if (map == null) return false;

            if (map.get(z) == null) return false;

            return true;
        }

        public void insertPosition(int x, int y, int z) {
            layout.computeIfAbsent(x, k -> new HashMap<>());
            Map<Integer, Map<Integer, Integer>> yz = layout.get(x);

            yz.computeIfAbsent(y, k -> new HashMap<>());
            Map<Integer, Integer> map = yz.get(y);

            map.put(z, 1);
        }

        /**
         * Returns false if the structure can not be inserted without overlap.
         *
         * @param holder
         * @param delta
         * @return
         */
        public boolean validateStructure(StructureUtil.StructureHolder holder, Vector delta) {
            for (int x = 0; x < holder.width; x++) {
                for (int y = 0; y < holder.height; y++) {
                    for (int z = 0; z < holder.depth; z++) {
                        if (exists(x + delta.getBlockX(), y + delta.getBlockY(), z + delta.getBlockZ())) return false;
                    }
                }
            }
            return true;
        }

        /**
         * Inserts a structure into the layout.
         *
         * @param holder
         * @param delta
         */
        public void insertStructure(StructureUtil.StructureHolder holder, Vector delta) {
            for (int x = 0; x < holder.width; x++) {
                for (int y = 0; y < holder.height; y++) {
                    for (int z = 0; z < holder.depth; z++) {
                        if (holder.getMaterialAt(x, y, z) != Material.STRUCTURE_VOID) insertPosition(x + delta.getBlockX(), y + delta.getBlockY(), z + delta.getBlockZ());
                    }
                }
            }
        }
    }

    // ----- BUILDER -----

    public static class ConnectedStructureBuilder extends AbstractSimpleStructureBuilder<ConnectedStructure, ConnectedStructureBuilder> {

        // ----- VARIABLES -----

        int depth;
        IStructure[] structures;

        // Extra
        IStructure initialStructure;

        // ----- CONSTRUCTORS -----

        public ConnectedStructureBuilder(Plugin ownerPlugin, int id, String structureName, int depth, IStructure... structures) {
            super(ownerPlugin, id, structureName, "");

            this.depth = depth;
            this.structures = structures;
        }

        @Override
        public ConnectedStructure build() {
            super.build();

            obj.setInitialStructure(initialStructure);

            return obj;
        }

        // ----- GETTERS / SETTERS -----


        public ConnectedStructureBuilder setInitialStructure(IStructure initialStructure) {
            this.initialStructure = initialStructure;
            return getThis();
        }

        @Override
        protected ConnectedStructure createObj() {
            return new ConnectedStructure(plugin, id, structureName, depth, structures);
        }

        @Override
        protected ConnectedStructureBuilder getThis() {
            return this;
        }
    }

    // ----- GETTERS / SETTERS -----


    public void setInitialStructure(IStructure initialStructure) {
        this.initialStructure = initialStructure;
    }

    @Override
    public String getName() {
        return structureName;
    }

    @Override
    public String getStructureFilePath() {
        return "";
    }
}
