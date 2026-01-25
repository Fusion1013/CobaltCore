package se.fusion1013.cobaltCore.commands.entity;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.entity.CustomEntityManager;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

public class CSummonCommand {

    public static void register() {
        new CommandAPICommand("csummon")
                .withPermission("commands.core.item")
                .withSubcommand(createSummonItemCommand())
                .withSubcommand(createSummonEntityCommand())
                .register();
    }

    public static CommandAPICommand createSummonEntityCommand() {
        return new CommandAPICommand("entity")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "csummon.entity"))
                .withArguments(new StringArgument("entity").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomEntityManager.getCustomEntityNames())))
                .withOptionalArguments(new IntegerArgument("count", 1))
                .executesPlayer(CSummonCommand::summonEntity);
    }

    private static void summonEntity(Player player, CommandArguments commandArguments) {
        World world = player.getWorld();
        Location location = player.getLocation();
        String entity = (String) commandArguments.get("entity");
        int count = commandArguments.get("count") != null ? (int) commandArguments.get("count") : 1;
        for (int i = 0; i < count; i++) CustomEntityManager.getInstance().spawnEntity(entity, world, location);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("entity", entity)
                .addPlaceholder("cound", count)
                .build();

        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.csummon.entity.success", placeholders);
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
