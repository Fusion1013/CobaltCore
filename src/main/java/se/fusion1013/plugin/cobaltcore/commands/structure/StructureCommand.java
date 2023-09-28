package se.fusion1013.plugin.cobaltcore.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.util.FileUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.IStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.StructureManager;

import java.util.HashMap;
import java.util.Map;

public class StructureCommand {

    public static void register() {
        new CommandAPICommand("structure")
                .withPermission("cobalt.core.command.structure")
                .withSubcommand(createSaveSubcommand())
                .withSubcommand(createPastSubcommand())
                .register();
    }

    // ----- PASTE SUBCOMMAND -----

    private static CommandAPICommand createPastSubcommand() {
        return new CommandAPICommand("paste")
                .withPermission("cobalt.core.command.structure.paste")
                .withArguments(new TextArgument("name"))
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .executes(StructureCommand::executePaste);
    }

    private static void executePaste(CommandSender sender, CommandArguments args) {
        String structure = (String) args.args()[0];
        Location location = (Location) args.args()[1];

        IStructure structure1 = StructureManager.getRegisteredStructure(structure);
        structure1.forceGenerate(location);
    }

    // ----- SAVE SUBCOMMAND -----

    private static CommandAPICommand createSaveSubcommand() {
        return new CommandAPICommand("save")
                .withPermission("cobalt.core.command.structure.save")
                .withArguments(new LocationArgument("location1", LocationType.BLOCK_POSITION))
                .withArguments(new LocationArgument("location2", LocationType.BLOCK_POSITION))
                .withArguments(new TextArgument("name"))
                .executes(StructureCommand::executeSave);
    }

    @SuppressWarnings("unchecked")
    private static void executeSave(CommandSender sender, CommandArguments args) {
        Location corner1 = (Location) args.args()[0];
        Location corner2 = (Location) args.args()[1];
        String name = (String) args.args()[2];

        // Get structure location info
        int cornerX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int cornerY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int cornerZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        Location corner = new Location(corner1.getWorld(), cornerX, cornerY, cornerZ);

        int width = Math.abs(corner1.getBlockX() - corner2.getBlockX())+1;
        int height = Math.abs(corner1.getBlockY() - corner2.getBlockY())+1;
        int depth = Math.abs(corner1.getBlockZ() - corner2.getBlockZ())+1;

        // Add generic info to json
        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("width", width);
        object.put("height", height);
        object.put("depth", depth);

        // Save block data

        long currentBlockId = 0;
        Map<BlockData, Long> dataIds = new HashMap<>();

        JSONArray blocks = new JSONArray(); // Will store all blocks

        for (int x = 0; x < width; x++) {
            JSONArray layer = new JSONArray(); // Will store one layer of blocks

            for (int y = 0; y < height; y++) {
                JSONArray row = new JSONArray(); // Will store one row of blocks

                for (int z = 0; z < depth; z++) {
                    Block block = corner.clone().add(new Vector(x, y, z)).getBlock();

                    // JSONObject blockJson = new JSONObject();

                    if (dataIds.get(block.getBlockData()) == null) {
                        currentBlockId++;

                        dataIds.put(block.getBlockData(), currentBlockId);

                        row.add(currentBlockId);
                        object.put(currentBlockId, block.getBlockData().getAsString());
                    } else {
                        row.add(dataIds.get(block.getBlockData()));
                    }
                }
                layer.add(row);
            }
            blocks.add(layer);
        }
        object.put("blocks", blocks);

        // Save the file
        String path = CobaltCore.getInstance().getDataFolder() + "/structures/" + name + ".json";
        FileUtil.saveJson(object, path);
    }
}
