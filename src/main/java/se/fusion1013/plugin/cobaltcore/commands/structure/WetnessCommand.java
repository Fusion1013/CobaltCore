package se.fusion1013.plugin.cobaltcore.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import se.fusion1013.plugin.cobaltcore.world.structure.modifier.WetnessStructureModifier;

public class WetnessCommand {

    public static void register() {
        new CommandAPICommand("wetness")
                .withPermission("cobalt.core.command.wetness")
                .withArguments(new LocationArgument("corner", LocationType.BLOCK_POSITION))
                .withArguments(new IntegerArgument("width"))
                .withArguments(new IntegerArgument("height"))
                .withArguments(new IntegerArgument("depth"))
                .executes(WetnessCommand::executeColorize)
                .register();
    }

    private static void executeColorize(CommandSender sender, CommandArguments args) {
        Location corner = (Location) args.args()[0];
        int width = (int) args.args()[1];
        int height = (int) args.args()[2];
        int depth = (int) args.args()[3];

        var wetness = new WetnessStructureModifier(corner, width, depth, height);
        wetness.colorize();
    }

}
