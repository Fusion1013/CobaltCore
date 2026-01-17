package se.fusion1013.cobaltCore.commands.entity;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.entity.CustomEntityManager;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

import java.util.UUID;

public class CustomEntityCommand {

    public static void register() {
        new CommandAPICommand("centity")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "centity"))
                .withSubcommand(CustomEntityCommand.createListCommand())
                .register();
    }

    public static CommandAPICommand createSummonEntityCommand() {
        return new CommandAPICommand("entity")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "csummon.entity"))
                .withArguments(new StringArgument("entity").replaceSuggestions(ArgumentSuggestions.strings(CustomEntityManager.getCustomEntityNames())))
                .executesPlayer(CustomEntityCommand::summonEntity);
    }

    private static void summonEntity(Player player, CommandArguments commandArguments) {
        World world = player.getWorld();
        Location location = player.getLocation();
        String entity = (String) commandArguments.get("entity");
        CustomEntityManager.getInstance().spawnEntity(entity, world, location);
    }

    private static CommandAPICommand createListCommand() {
        return new CommandAPICommand("list")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "centity.list"))
                .executesPlayer(CustomEntityCommand::listEntities);
    }

    private static void listEntities(Player player, CommandArguments commandArguments) {
        UUID[] uuids = CustomEntityManager.getCustomEntityInstanceUUIDS();
        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.centity.list.header");
        for (UUID uuid : uuids) {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.centity.list.item", StringPlaceholders.builder().addPlaceholder("uuid", uuid.toString()).build());
        }
    }

}
