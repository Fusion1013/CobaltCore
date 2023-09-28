package se.fusion1013.plugin.cobaltcore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

public class CobaltSummonCommand {

    // ----- REGISTER -----

    /**
     * Registers the csummon command.
     */
    public static void register() {
        createSummonManyAtLocationCommand().register();
        createSummonManyCommand().register();
        createSummonOneAtLocationCommand().register();
        createSummonOneCommand().register();
    }

    // ----- SUMMON MANY AT LOCATION COMMAND -----

    private static CommandAPICommand createSummonManyAtLocationCommand() {
        return new CommandAPICommand("csummon")
                .withPermission("cobalt.core.command.csummon")
                .withArguments(new StringArgument("entity").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomEntityManager.getInternalEntityNames())))
                .withArguments(new LocationArgument("location"))
                .withArguments(new IntegerArgument("count"))
                .executes(((sender, args) -> {
                    summonManyAtLocation(sender, (String)args.args()[0], (Location)args.args()[1], (int)args.args()[2]);
                }));
    }

    // ----- SUMMON MANY COMMAND -----

    /**
     * Creates the summon command that summons many entities at the players position.
     *
     * @return the command.
     */
    private static CommandAPICommand createSummonManyCommand() {
        return new CommandAPICommand("csummon")
                .withPermission("cobalt.core.command.csummon")
                .withArguments(new StringArgument("entity").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomEntityManager.getInternalEntityNames())))
                .withArguments(new IntegerArgument("count"))
                .executesPlayer(((sender, args) -> {
                    summonManyAtLocation(sender, (String)args.args()[0], sender.getLocation(), (int)args.args()[1]);
                }));
    }

    // ----- SUMMON ONE AT LOCATION COMMAND -----

    /**
     * Creates the summon command that summons one entity at the specified location.
     *
     * @return the command.
     */
    private static CommandAPICommand createSummonOneAtLocationCommand() {
        return new CommandAPICommand("csummon")
                .withPermission("cobalt.core.command.csummon")
                .withArguments(new StringArgument("entity").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomEntityManager.getInternalEntityNames())))
                .withArguments(new LocationArgument("location"))
                .executes((sender, args) -> {
                    summonManyAtLocation(sender, (String)args.args()[0], (Location)args.args()[1], 1);
                });
    }

    // ----- SUMMON ONE COMMAND -----

    /**
     * Creates the summon command that summons one entity at the executors' location.
     *
     * @return the command.
     */
    private static CommandAPICommand createSummonOneCommand() {
        return new CommandAPICommand("csummon")
                .withPermission("cobalt.core.command.csummon")
                .withArguments(new StringArgument("entity").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomEntityManager.getInternalEntityNames())))
                .executesPlayer(((sender, args) -> {
                    summonManyAtLocation(sender, (String)args.args()[0], sender.getLocation(), 1);
                }));
    }

    // ----- LOGIC -----

    /**
     * Summons the specified number of <code>CustomEntity</code>'s at the <code>Location</code>.
     *
     * @param sender the sender that is summoning the entities.
     * @param entityName the name of the <code>CustomEntity</code>.
     * @param location the <code>Location</code> to summon the <code>CustomEntity</code> at.
     * @param count the number of <code>CustomEntity</code>'s to summon.
     */
    private static void summonManyAtLocation(CommandSender sender, String entityName, Location location, int count) {

        for (int i = 0; i < count; i++) CustomEntityManager.forceSummonEntity(entityName, location);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("name", entityName)
                .addPlaceholder("location", location)
                .addPlaceholder("count", count)
                .build();

        if (sender instanceof Player player) {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.csummon.summoned_entity", placeholders);
        }
    }
}
