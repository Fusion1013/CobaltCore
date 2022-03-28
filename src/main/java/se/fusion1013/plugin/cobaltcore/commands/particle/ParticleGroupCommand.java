package se.fusion1013.plugin.cobaltcore.commands.particle;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
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
                .withSubcommand(createDisplayCommand());
    }

    // ----- CREATE CREATE COMMAND -----

    private static CommandAPICommand createCreateCommand() {
        return new CommandAPICommand("create")
                .withPermission("cobalt.core.commands.cparticle.group")
                .withArguments(new StringArgument("name"))
                .executes(ParticleGroupCommand::createParticleGroup);
    }

    private static void createParticleGroup(CommandSender sender, Object[] args) {
        String name = (String)args[0];
        boolean created = false;

        if (!ParticleGroupManager.groupExists(name)) {
            ParticleGroupManager.createParticleGroup(name);
            created = true;
        }

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
                .withArguments(new StringArgument("groupName").replaceSuggestions(info -> ParticleGroupManager.getParticleGroupNames()))
                .withArguments(new StringArgument("styleName").replaceSuggestions(info -> ParticleStyleManager.getParticleStyleNames()))
                .executes(ParticleGroupCommand::addStyle);
    }

    private static void addStyle(CommandSender sender, Object[] args) {
        String groupName = (String) args[0];
        String styleName = (String) args[1];

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

        ParticleGroup group = ParticleGroupManager.getParticleGroup(groupName);
        ParticleStyle style = ParticleStyleManager.getParticleStyle(styleName);

        group.addParticleStyle(style);

        if (sender instanceof Player player) {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.group.add_style", placeholders);
        }
    }

    // ----- CREATE DISPLAY COMMAND -----

    private static CommandAPICommand createDisplayCommand() {
        return new CommandAPICommand("display")
                .withPermission("cobalt.core.commands.cparticle.group")
                .withArguments(new StringArgument("groupName").replaceSuggestions(info -> ParticleGroupManager.getParticleGroupNames()))
                .withArguments(new LocationArgument("location"))
                .executes(ParticleGroupCommand::displayGroup);
    }

    private static void displayGroup(CommandSender sender, Object[] args) {
        String name = (String)args[0];
        Location location = (Location)args[1];
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

    private static void listGroups(Player player, Object[] args) {
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

}
