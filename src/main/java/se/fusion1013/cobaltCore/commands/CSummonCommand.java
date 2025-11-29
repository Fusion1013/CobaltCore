package se.fusion1013.cobaltCore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;

public class CSummonCommand {

    public static void register() {
        new CommandAPICommand("csummon")
                .withPermission("commands.core.item")
                .withSubcommand(createSummonItemCommand())
                .register();
    }

    private static CommandAPICommand createSummonItemCommand() {
        return new CommandAPICommand("item")
                .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomItemManager.getCustomItemNames())))
                .withArguments(new LocationArgument("position"))
                .executes((sender, args) -> {
                    String itemName = (String) args.get("item");
                    ICustomItem customItem = CustomItemManager.getCustomItem(itemName);
                    Location location = (Location) args.get("position");
                    World world = location.getWorld();
                    world.dropItemNaturally(location, customItem.getItemStack());
                });
    }

}
