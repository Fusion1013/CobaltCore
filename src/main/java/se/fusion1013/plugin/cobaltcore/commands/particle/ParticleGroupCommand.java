package se.fusion1013.plugin.cobaltcore.commands.particle;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleGroupManager;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

public class ParticleGroupCommand {

    // ----- CREATE PARTICLE GROUP COMMAND -----

    /**
     * Creates the Particle Group command.
     *
     * @return the command.
     */
    public static CommandAPICommand createParticleGroupCommand() {
        return new CommandAPICommand("group")
                .withPermission("cobalt.core.commands.cparticle.group")
                .withSubcommand(createCreateCommand())
                .withSubcommand(createListCommand())
                .withSubcommand(createAddStyleCommand())
                .withSubcommand(createDisplayCommand())
                .withSubcommand(createEditStyleCommand())
                .withSubcommand(createRemoveCommand())
                .withSubcommand(editIntegrityCommand());
    }

    // ----- CREATE EDIT INTEGRITY COMMAND -----

    private static CommandAPICommand editIntegrityCommand() {
        return new CommandAPICommand("integrity")
                .withPermission("cobalt.core.commands.cparticle.group")
                .withArguments(new StringArgument("groupName").replaceSuggestions(ArgumentSuggestions.strings(info -> ParticleGroupManager.getParticleGroupNames())))
                .withArguments(new DoubleArgument("integrity"))
                .executes(ParticleGroupCommand::editIntegrity);
    }

    private static void editIntegrity(CommandSender sender, CommandArguments args) {
        String name = (String) args.args()[0];
        double integrity = (double) args.args()[1];

        ParticleGroup group = ParticleGroupManager.getParticleGroup(name);
        if (group != null) group.setIntegrity(integrity);
    }

    // ----- CREATE CREATE COMMAND -----

    private static CommandAPICommand createCreateCommand() {
        return new CommandAPICommand("create")
                .withPermission("cobalt.core.commands.cparticle.group")
                .withArguments(new StringArgument("name"))
                .executes(ParticleGroupCommand::createParticleGroup);
    }

    private static void createParticleGroup(CommandSender sender, CommandArguments args) {
        String name = (String)args.args()[0];
        boolean created = ParticleGroupManager.createParticleGroup(name);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("name", name)
                .build();

        if (sender instanceof Player player) {
            if (created) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group.create.success", placeholders);
            else LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group.create.error.already_exists", placeholders);
        }
    }

    // ----- CREATE ADD STYLE COMMAND -----

    private static CommandAPICommand createAddStyleCommand() {
        return new CommandAPICommand("add")
                .withPermission("cobalt.core.commands.cparticle.group")
                .withArguments(new StringArgument("groupName").replaceSuggestions(ArgumentSuggestions.strings(info -> ParticleGroupManager.getParticleGroupNames())))
                .withArguments(new StringArgument("styleName").replaceSuggestions(ArgumentSuggestions.strings(info -> ParticleStyleManager.getParticleStyleNames())))
                .executes(ParticleGroupCommand::addStyle);
    }

    private static void addStyle(CommandSender sender, CommandArguments args) {
        String groupName = (String) args.args()[0];
        String styleName = (String) args.args()[1];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("group_name", groupName)
                .addPlaceholder("style_name", styleName)
                .build();

        if (!ParticleGroupManager.groupExists(groupName)) {
            if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group_does_not_exist");
            return;
        }
        if (!ParticleStyleManager.styleExists(styleName)) {
            if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style_does_not_exist");
            return;
        }

        ParticleStyle style = ParticleStyleManager.getParticleStyle(styleName);
        boolean added = ParticleGroupManager.addParticleStyle(groupName, style);

        if (sender instanceof Player player) {
            if (added) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group.add_style.success", placeholders);
            else LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group.add_style.already_has_style", placeholders);
        }
    }

    // ----- CREATE DISPLAY COMMAND -----

    private static CommandAPICommand createDisplayCommand() {
        return new CommandAPICommand("display")
                .withPermission("cobalt.core.commands.cparticle.group")
                .withArguments(new StringArgument("groupName").replaceSuggestions(ArgumentSuggestions.strings(info -> ParticleGroupManager.getParticleGroupNames())))
                .withArguments(new LocationArgument("location"))
                .executes(ParticleGroupCommand::displayGroup);
    }

    private static void displayGroup(CommandSender sender, CommandArguments args) {
        String name = (String)args.args()[0];
        Location location = (Location)args.args()[1];
        ParticleGroup group = ParticleGroupManager.getParticleGroup(name);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("group_name", name)
                .addPlaceholder("location", location)
                .build();

        if (group == null) {
            if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group_does_not_exist", placeholders);
            return;
        }

        group.display(location);

        if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group.display", placeholders);
    }

    // ----- CREATE LIST COMMAND -----

    private static CommandAPICommand createListCommand() {
        return new CommandAPICommand("list")
                .withPermission("cobalt.core.commands.cparticle.group")
                .executesPlayer(ParticleGroupCommand::listGroups);
    }

    private static void listGroups(Player player, CommandArguments args) {
        String[] groupNames = ParticleGroupManager.getParticleGroupNames();

        // If there are more than 0 styles, send a header. Else send error.
        if (groupNames.length > 0) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("header", "Particle Groups")
                    .build();
            LocaleManager.getInstance().sendMessage("", player, "list-header", placeholders);
        } else {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group.list.error.no_groups_found");
        }

        // List all particle groups
        for (String s : groupNames) {
            ParticleGroup group = ParticleGroupManager.getParticleGroup(s);

            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("name", s)
                    .addPlaceholder("style_count", group.getParticleStyleCount())
                    .build();
            LocaleManager.getInstance().sendMessage("", player, "commands.cparticle.group.list.item", placeholders);
        }
    }

    // ----- CREATE REMOVE COMMAND -----

    private static CommandAPICommand createRemoveCommand() {
        return new CommandAPICommand("remove")
                .withPermission("cobalt.core.commands.cparticle.group")
                .withArguments(new StringArgument("groupName").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> ParticleGroupManager.getParticleGroupNames())))
                .executes(ParticleGroupCommand::removeGroup);
    }

    private static void removeGroup(CommandSender sender, CommandArguments args) {
        String groupName = (String) args.args()[0];
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("group_name", groupName)
                .build();

        boolean removed = ParticleGroupManager.removeGroup(groupName);

        if (sender instanceof Player player) {
            if (removed) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group.remove", placeholders);
            else LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group_does_not_exist", placeholders);
        }
    }

    // ----- CREATE EDIT STYLE COMMAND -----

    private static CommandAPICommand createEditStyleCommand() {
        return new CommandAPICommand("edit_style")
                .withPermission("cobalt.core.commands.cparticle.group")
                .withSubcommand(new CommandAPICommand("offset")
                        .withArguments(new StringArgument("groupName").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> ParticleGroupManager.getParticleGroupNames())))
                        .withArguments(new StringArgument("styleName").replaceSuggestions(ArgumentSuggestions.strings(ParticleGroupCommand::getStyleNames)))
                        .withArguments(new DoubleArgument("x_offset"))
                        .withArguments(new DoubleArgument("y_offset"))
                        .withArguments(new DoubleArgument("z_offset"))
                        .executes(ParticleGroupCommand::editOffset))
                .withSubcommand(new CommandAPICommand("rotation")
                        .withArguments(new StringArgument("groupName").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> ParticleGroupManager.getParticleGroupNames())))
                        .withArguments(new StringArgument("styleName").replaceSuggestions(ArgumentSuggestions.strings(ParticleGroupCommand::getStyleNames)))
                        .withArguments(new DoubleArgument("x_rotation"))
                        .withArguments(new DoubleArgument("y_rotation"))
                        .withArguments(new DoubleArgument("z_rotation"))
                        .withArguments(new DoubleArgument("x_rotation_speed"))
                        .withArguments(new DoubleArgument("y_rotation_speed"))
                        .withArguments(new DoubleArgument("z_rotation_speed"))
                        .executes(ParticleGroupCommand::editRotation));
    }

    private static String[] getStyleNames(SuggestionInfo info) {
        ParticleGroup currentGroup = ParticleGroupManager.getParticleGroup((String)info.previousArgs().args()[info.previousArgs().args().length-1]);
        if (currentGroup == null) return new String[0];
        return currentGroup.getParticleStyleNames();
    }

    private static void editOffset(CommandSender sender, CommandArguments args) {
        String groupName = (String)args.args()[0];
        String styleName = (String)args.args()[1];

        ParticleGroup group = ParticleGroupManager.getParticleGroup(groupName);
        if (group == null) return;
        group.setStyleOffset(styleName, new Vector((double)args.args()[2], (double)args.args()[3], (double)args.args()[4]));
    }

    private static void editRotation(CommandSender sender, CommandArguments args) {
        String groupName = (String)args.args()[0];
        String styleName = (String)args.args()[1];

        ParticleGroup group = ParticleGroupManager.getParticleGroup(groupName);
        if (group == null) return;
        Vector rotation = new Vector(Math.toRadians((double)args.args()[2]), Math.toRadians((double)args.args()[3]), Math.toRadians((double)args.args()[4]));
        Vector velocity = new Vector(Math.toRadians((double)args.args()[5]), Math.toRadians((double)args.args()[6]), Math.toRadians((double)args.args()[7]));

        group.setStyleRotation(styleName, rotation, velocity);
    }

}
