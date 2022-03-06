package se.fusion1013.plugin.cobaltcore.commands.particle;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParticleStyleCommand {

    // ----- CREATE PARTICLE STYLE COMMAND -----

    /**
     * Creates the main style command.
     *
     * @return the style command.
     */
    public static CommandAPICommand createParticleStyleCommand() {
        return new CommandAPICommand("style")
                .withPermission("cobalt.core.commands.cparticle.style")
                .withSubcommand(createCreateCommand())
                .withSubcommand(createListCommand())
                .withSubcommand(createRemoveCommand())
                .withSubcommand(createExtraSettingsCommand());
    }

    // ----- CREATE CREATE COMMAND -----

    private static CommandAPICommand createCreateCommand() {
        return new CommandAPICommand("create")
                .withPermission("cobalt.core.commands.cparticle.style")
                .withArguments(new StringArgument("name"))
                .withArguments(new StringArgument("styleName").replaceSuggestions(info -> ParticleStyleManager.getInternalParticleStyleNames()))
                .withArguments(new ParticleArgument("particle"))
                .withArguments(new LocationArgument("offset"))
                .withArguments(new IntegerArgument("count"))
                .withArguments(new DoubleArgument("speed"))
                .executes(ParticleStyleCommand::createParticleStyle);
    }

    private static void createParticleStyle(CommandSender sender, Object[] args) {
        // Get variables.
        String name = (String) args[0];
        String internalStyleName = (String) args[1];
        Particle particle = (Particle) args[2];
        Location offset = (Location) args[3];
        int count = (Integer) args[4];
        double speed = (Double) args[5];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("name", name)
                .build();

        boolean created = false;

        // Check if the name already exists, if so, do not create new style.
        if (!ParticleStyleManager.styleExists(name)) {
            // Create ParticleStyle.
            // Extra will be added through a separate command.
            ParticleStyleManager.createParticleStyle(internalStyleName, name, particle, new Vector(offset.getBlockX(), offset.getY(), offset.getZ()), count, speed, null);
            created = true;
        }

        // Send message
        if (sender instanceof Player player) {
            if (created) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.create.success", placeholders);
            else LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.create.error.already_exists", placeholders);
        }
    }

    // ----- CREATE MODIFY COMMAND -----

    // ----- CREATE SET EXTRA SETTINGS COMMAND ----

    private static CommandAPICommand createExtraSettingsCommand() {
        CommandAPICommand command = new CommandAPICommand("extra")
                .withPermission("cobalt.core.commands.cparticle.style.extra");

        String[] styles = ParticleStyleManager.getInternalParticleStyleNames();
        for (String s : styles) {
            command.withSubcommand(setExtraSettingsSubcommand(ParticleStyleManager.getDefaultParticleStyle(s)));
        }

        return command;
    }

    private static CommandAPICommand setExtraSettingsSubcommand(ParticleStyle style) {
        return new CommandAPICommand(style.getInternalName())
                .withArguments(new StringArgument("name").replaceSuggestions(info -> ParticleStyleManager.getParticleStyleNames(style.getInternalName())))
                .withArguments(style.getExtraSettingsArguments())
                .executes(((sender, args) -> {
                    String styleName = (String) args[0];
                    List<Object> settings = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
                    ParticleStyle particleStyle = ParticleStyleManager.getParticleStyle(styleName);

                    particleStyle.setExtraSettings(settings.toArray());

                    if (sender instanceof Player player) {
                        List<String> info = particleStyle.getInfoStrings();

                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("name", particleStyle.getName())
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.extra.set", placeholders);
                        for (String s : info) player.sendMessage(s);
                    }
                }));
    }

    // ----- CREATE LIST COMMAND -----

    private static CommandAPICommand createListCommand() {
        return new CommandAPICommand("list")
                .withPermission("cobalt.core.commands.cparticle.style.list")
                .executesPlayer(ParticleStyleCommand::listStyles);
    }

    /**
     * Lists all particle styles to the executing player.
     *
     * @param player the player to send the information to.
     */
    private static void listStyles(Player player, Object[] args) {

        String[] stylesNames = ParticleStyleManager.getParticleStyleNames();

        // If there are more than 0 styles, send a header. Else send error.
        if (stylesNames.length > 0) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("header", "Particle Styles")
                    .build();
            LocaleManager.getInstance().sendMessage("", player, "list-header", placeholders);
        } else {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.list.error.no_styles_found");
        }

        // List all particle styles
        for (String s : stylesNames) {
            ParticleStyle style = ParticleStyleManager.getParticleStyle(s);

            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("name", s)
                    .addPlaceholder("internal_style", style.getInternalName())
                    .addPlaceholder("particle", style.getParticle().name().toLowerCase())
                    .build();
            LocaleManager.getInstance().sendMessage("", player, "commands.cparticle.style.list.item", placeholders);
        }

    }

    // ----- CREATE INFO COMMAND -----

    // ----- CREATE REMOVE COMMAND -----

    private static CommandAPICommand createRemoveCommand() {
        return new CommandAPICommand("remove")
                .withPermission("cobalt.core.commands.cparticle.style.remove")
                .withArguments(new StringArgument("name"))
                .executes(ParticleStyleCommand::removeStyle);
    }

    /**
     * Removes a style.
     *
     * @param sender the sender that is removing the style.
     * @param args the style to remove.
     */
    private static void removeStyle(CommandSender sender, Object[] args) {
        String name = (String) args[0];
        boolean removed = ParticleStyleManager.deleteStyle(name);

        // Send message depending on if the style was removed or not.
        if (sender instanceof Player player) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("name", name)
                    .build();

            if (removed) {
                LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.remove.success", placeholders);
            } else {
                LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.remove.fail", placeholders);
            }
        }
    }

}
