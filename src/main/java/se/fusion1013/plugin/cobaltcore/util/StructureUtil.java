package se.fusion1013.plugin.cobaltcore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.world.block.BlockPlacementManager;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StructureUtil {

    /**
     * Loads a structure from the plugins resources folder and stores it into a <code>StructureHolder</code>.
     *
     * @param plugin the <code>Plugin</code> that is loading the structure.
     * @param filePath the path to the structure json file in the resource folder.
     * @return a new <code>StructureHolder</code>.
     */
    public static StructureHolder preLoadStructure(Plugin plugin, String filePath) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(FileUtil.getOrCreateFileFromResource(plugin, filePath)));
            JSONObject jsonObject = (JSONObject) obj;

            int width = Math.toIntExact((long) jsonObject.get("width"));
            int height = Math.toIntExact((long) jsonObject.get("height"));
            int depth = Math.toIntExact((long) jsonObject.get("depth"));

            StructureHolder holder = new StructureHolder(width, height, depth);
            holder.loadBlocks(jsonObject);
            holder.generateConnections();

            return holder;

        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Holds a preloaded structure
     */
    public static class StructureHolder {

        // ----- VARIABLES -----

        final BlockData[][][] data;

        public final int width;
        public final int height;
        public final int depth;

        public final List<StructureConnection> structureConnections = new ArrayList<>();

        // ----- CONSTRUCTORS -----

        public StructureHolder(int width, int height, int depth) {
            this.width = width;
            this.height = height;
            this.depth = depth;

            data = new BlockData[width][height][depth];
        }

        // ----- BLOCK LOAD METHODS -----

        // TODO: Make sure size is correct of arrays

        /**
         * Loads a <code>JSONArray</code> of blocks into the <code>BlockData</code> array.
         *
         * @param structure structure data.
         */
        public void loadBlocks(JSONObject structure) {
            JSONArray blockData = (JSONArray) structure.get("blocks");
            for (int x = 0; x < width; x++) {
                JSONArray layer = (JSONArray) blockData.get(x);

                for (int y = 0; y < height; y++) {
                    JSONArray row = (JSONArray) layer.get(y);

                    for (int z = 0; z < depth; z++) {

                        long id = (long) row.get(z);
                        String currentData = (String) structure.get(String.valueOf(id));

                        data[x][y][z] = Bukkit.createBlockData(currentData);
                    }
                }
            }
        }


        // TODO: Structure rotation


        // ----- STRUCTURE PLACING -----

        /**
         * Places the structure at the <code>Location</code>.
         *
         * @param location the <code>Location</code> to place the structure at.
         */
        public void placeStructure(Location location) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        BlockData data = this.data[x][y][z];

                        Material material = data.getMaterial();

                        if (material == Material.STRUCTURE_VOID) continue; // Don't replace blocks at structure voids

                        Location placementLocation = location.clone().add(new Vector(x, y, z));

                        // Don't place commands blocks, as they are used for connections. Place air instead
                        if (material == Material.COMMAND_BLOCK) {
                            BlockPlacementManager.addBlock(Material.AIR, placementLocation);
                            continue;
                        }

                        BlockPlacementManager.addBlock(material, data, placementLocation);
                    }
                }
            }
        }

        // ----- STRUCTURE CONNECTION GENERATION -----

        public void generateConnections() {

            Material connectionMaterial = Material.COMMAND_BLOCK;

            boolean[][][] blacklist = new boolean[width][height][depth];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        BlockData currentData = data[x][y][z];
                        Material currentMaterial = currentData.getMaterial();

                        if (connectionMaterial == currentMaterial && !blacklist[x][y][z]) { // Found start of connection
                            Vector connectionDimensions = getConnectionDimensions(connectionMaterial, x, y, z);

                            if (currentData instanceof Directional directional) {

                                StructureConnection connection = new StructureConnection(new Vector(x, y, z), connectionDimensions, directional.getFacing());
                                structureConnections.add(connection);

                                blacklistConnectionBlocks(x, y, z, connectionDimensions, blacklist);
                            }
                        }
                    }
                }
            }
        }

        private void blacklistConnectionBlocks(int xPos, int yPos, int zPos, Vector dimensions, boolean[][][] blacklist) {
            for (int x = xPos; x < xPos+dimensions.getBlockX(); x++) {
                for (int y = yPos; y < yPos+dimensions.getBlockY(); y++) {
                    for (int z = zPos; z < zPos+dimensions.getBlockZ(); z++) {
                        blacklist[x][y][z] = true;
                    }
                }
            }
        }

        private void removeConnectionBlocks(int xPos, int yPos, int zPos, Vector dimensions) {
            for (int x = xPos; x < xPos+dimensions.getBlockX(); x++) {
                for (int y = yPos; y < yPos+dimensions.getBlockY(); y++) {
                    for (int z = zPos; z < zPos+dimensions.getBlockZ(); z++) {
                        data[x][y][z] = Material.AIR.createBlockData();
                    }
                }
            }
        }

        private Vector getConnectionDimensions(Material connectionMaterial, int currentX, int currentY, int currentZ) {
            Vector dimensions = new Vector();

            for (int x = currentX; x < width; x++) {
                if (data[x][currentY][currentZ].getMaterial() != connectionMaterial) {
                    dimensions.setX(x - currentX);
                    break;
                }

                if (x+1>=width) dimensions.setX(x-currentX+1);
            }
            for (int y = currentY; y < height; y++) {
                if (data[currentX][y][currentZ].getMaterial() != connectionMaterial) {
                    dimensions.setY(y - currentY);
                    break;
                }

                if (y+1>=height) dimensions.setY(y-currentY+1);
            }
            for (int z = currentZ; z < depth; z++) {
                if (data[currentX][currentY][z].getMaterial() != connectionMaterial) {
                    dimensions.setZ(z - currentZ);
                    break;
                }

                if (z+1>=depth) dimensions.setZ(z-currentZ+1);
            }

            return dimensions;
        }

        // ----- GETTERS / SETTERS -----

        public Material getMaterialAt(int x, int y, int z) {
            BlockData data = this.data[x][y][z];
            return data.getMaterial();
        }

        public Vector getSize() {
            return new Vector(width, height, depth);
        }

        /**
         * Gets a copy that is rotated 90 degrees.
         *
         * @return a copy that is rotated 90 degrees.
         */
        public StructureHolder getRotatedCopy() {
            StructureHolder holder = new StructureHolder(depth, height, width);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        holder.data[depth-1-z][y][x] = data[x][y][z].clone();
                        BlockData currentData = holder.data[depth-1-z][y][x];

                        if (currentData instanceof Directional directional) {
                            switch (directional.getFacing()) {
                                case NORTH -> directional.setFacing(BlockFace.EAST);
                                case EAST -> directional.setFacing(BlockFace.SOUTH);
                                case SOUTH -> directional.setFacing(BlockFace.WEST);
                                case WEST -> directional.setFacing(BlockFace.NORTH);
                            }
                        }
                        holder.data[depth-1-z][y][x] = currentData;
                    }
                }
            }

            holder.generateConnections();

            return holder;
        }

        // ----- STRUCTURE CONNECTION HOLDER -----

        public static class StructureConnection {

            Vector relativePosition;
            Vector dimensions;
            BlockFace face;

            public StructureConnection(Vector relativePosition, Vector dimensions, BlockFace face) {
                this.relativePosition = relativePosition;
                this.dimensions = dimensions;
                this.face = face;
            }

            public Vector getRelativePosition() {
                return relativePosition;
            }

            public Vector getDimensions() {
                return dimensions;
            }

            public BlockFace getFace() {
                return face;
            }
        }

    }

}
