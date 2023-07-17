package se.fusion1013.plugin.cobaltcore.commands.encounter;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.action.encounter.Encounter;
import se.fusion1013.plugin.cobaltcore.action.encounter.EncounterEvent;
import se.fusion1013.plugin.cobaltcore.action.encounter.EncounterManager;
import se.fusion1013.plugin.cobaltcore.action.system.IAction;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

public class EncounterCommand {

    public static CommandAPICommand createEncounterCommand() {
        CommandAPICommand encounterCommand = new CommandAPICommand("encounter")
                .withPermission("commands.core.encounter")
                .withSubcommand(createListCommand())
                .withSubcommand(createPlayCommand())
                .withSubcommand(createInfoCommand())
                .withSubcommand(createCancelCommand());
        encounterCommand.register();

        return encounterCommand;
    }

    //region PLAY

    private static CommandAPICommand createPlayCommand() {
        return new CommandAPICommand("play")
                .withPermission("commands.core.encounter.play")
                .withArguments(new TextArgument("encounter_name").replaceSuggestions(ArgumentSuggestions.strings(info -> EncounterManager.getEncounterNames())))
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .executes(EncounterCommand::playEncounter);
    }

    private static void playEncounter(CommandSender sender, Object[] args) {
        String name = (String) args[0];
        Location location = (Location) args[1];

        boolean executed = EncounterManager.playEncounter(name, location);

        if (sender instanceof Player player) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("name", name)
                    .addPlaceholder("location", location)
                    .build();
            if (executed) LocaleManager.getInstance().sendMessage(player, "commands.core.encounter.play.success", placeholders);
            else LocaleManager.getInstance().sendMessage(player, "commands.core.encounter.play.failed", placeholders);
        }
    }

    //endregion

    //region CANCEL

    private static CommandAPICommand createCancelCommand() {
        return new CommandAPICommand("cancel")
                .withPermission("commands.core.encounter.cancel")
                .withArguments(new TextArgument("encounter_name").replaceSuggestions(ArgumentSuggestions.strings(info -> EncounterManager.getRunningEncounterNames())))
                .executes(EncounterCommand::cancelEncounter);
    }

    private static void cancelEncounter(CommandSender sender, Object[] args) {
        String encounterName = (String) args[0];
        boolean canceled = EncounterManager.cancelEncounter(encounterName);

        if (sender instanceof Player player) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("id", encounterName)
                    .build();
            if (canceled) LocaleManager.getInstance().sendMessage(player, "commands.core.encounter.cancel.success", placeholders);
            else LocaleManager.getInstance().sendMessage(player, "commands.core.encounter.cancel.failed", placeholders);
        }
    }

    //endregion

    //region INFO

    private static CommandAPICommand createInfoCommand() {
        return new CommandAPICommand("info")
                .withPermission("commands.core.encounter.info")
                .withArguments(new TextArgument("encounter_name").replaceSuggestions(ArgumentSuggestions.strings(info -> EncounterManager.getEncounterNames())))
                .executesPlayer(EncounterCommand::displayInfo);
    }

    private static void displayInfo(Player player, Object[] args) {
        String encounterName = (String) args[0];
        Encounter encounter = EncounterManager.getEncounter(encounterName);

        StringPlaceholders headerPlaceholder = StringPlaceholders.builder()
                .addPlaceholder("header", encounter.getInternalName())
                .build();
        LocaleManager.getInstance().sendMessage("", player, "list-header", headerPlaceholder);

        LocaleManager.getInstance().sendMessage("", player, "commands.core.encounter.info.events.header");
        // Display events
        for (EncounterEvent event : encounter.getEvents()) {
            StringPlaceholders eventPlaceholder = StringPlaceholders.builder()
                    .addPlaceholder("event_name", event.getName())
                    .addPlaceholder("start_time", event.getStartTime() / 1000.0)
                    .addPlaceholder("end_time", event.getEndTime() / 1000.0)
                    .build();
            LocaleManager.getInstance().sendMessage("", player, "commands.core.encounter.info.events.event_header", eventPlaceholder);
            LocaleManager.getInstance().sendMessage("", player, "commands.core.encounter.info.events.time", eventPlaceholder);

            // Display actions
            LocaleManager.getInstance().sendMessage("", player, "commands.core.encounter.info.events.action.header");
            for (IAction action : event.getActions()) {
                StringPlaceholders actionPlaceholder = StringPlaceholders.builder()
                        .addPlaceholder("action", action.getInternalName())
                        .build();
                LocaleManager.getInstance().sendMessage("", player, "commands.core.encounter.info.events.action", actionPlaceholder);
            }
        }
    }

    //endregion

    //region LIST

    private static CommandAPICommand createListCommand() {
        return new CommandAPICommand("list")
                .withPermission("commands.core.encounter.list")
                .executesPlayer(EncounterCommand::listEncounters);
    }

    private static void listEncounters(Player player, Object[] args) {

        StringPlaceholders headerPlaceholder = StringPlaceholders.builder()
                .addPlaceholder("header", "Encounters")
                .build();
        LocaleManager.getInstance().sendMessage("", player, "list-header", headerPlaceholder);

        for (Encounter encounter : EncounterManager.getEncounters()) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("name", encounter.getInternalName())
                    .addPlaceholder("event_count", encounter.getEventCount())
                    .build();

            LocaleManager.getInstance().sendMessage("", player, "commands.core.encounter.list.item", placeholders);
        }
    }

    //endregion

}
