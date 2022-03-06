package se.fusion1013.plugin.cobaltcore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

public class CobaltSummonCommand {

    // ----- REGISTER -----

    public static void register() {

        new CommandAPICommand("csummon")
                .withPermission("cobalt.core.command.csummon")
                .withArguments(new StringArgument("entity").replaceSuggestions(info -> CustomEntityManager.getInternalEntityNames()))
                .executesPlayer(((sender, args) -> {
                    String name = (String)args[0];
                    Location location = sender.getLocation();

                    boolean summoned = CustomEntityManager.forceSummonEntity(name, location);
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("name", name)
                            .addPlaceholder("location", location)
                            .build();

                    if (summoned) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.csummon.summoned_entity", placeholders);
                    else LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.csummon.could_not_summon", placeholders);
                })).register();

        new CommandAPICommand("csummon")
                .withPermission("cobalt.core.command.csummon")
                .withArguments(new StringArgument("entity").replaceSuggestions(info -> CustomEntityManager.getInternalEntityNames()))
                .withArguments(new LocationArgument("location"))
                .executes(((sender, args) -> {
                    String name = (String) args[0];
                    Location location = (Location) args[1];

                    boolean summoned = CustomEntityManager.forceSummonEntity(name, location);

                    if (sender instanceof Player player) {
                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("name", name)
                                .addPlaceholder("location", location)
                                .build();

                        if (summoned)
                            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.csummon.summoned_entity", placeholders);
                        else
                            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.csummon.could_not_summon", placeholders);
                    }
                })).register();

    }

}
