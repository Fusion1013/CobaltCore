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
                .withSubcommand(createExtraSettingsCommand())
                .withSubcommand(createRotationCommand())
                .withSubcommand(createEditParticleCommand())
                .withSubcommand(createInfoCommand());
    }

    // ----- CREATE EDIT PARTICLE COMMAND -----

    private static CommandAPICommand createEditParticleCommand() {
        return new CommandAPICommand("particle")
                .withPermission("cobalt.core.commands.cparticle.style")
                .withArguments(new StringArgument("styleName").replaceSuggestions(info -> ParticleStyleManager.getParticleStyleNames()))
                .withArguments(new ParticleArgument("particle"))
                .withArguments(new DoubleArgument("offsetX"))
                .withArguments(new DoubleArgument("offsetY"))
                .withArguments(new DoubleArgument("offsetZ"))
                .withArguments(new IntegerArgument("count"))
                .withArguments(new DoubleArgument("speed"))
                .executes(ParticleStyleCommand::editParticle);
    }

    private static void editParticle(CommandSender sender, Object[] args) {
        String styleName = (String) args[0];
        Particle particle = (Particle) args[1];
        double offsetX = (double) args[2];
        double offsetY = (double) args[3];
        double offsetZ = (double) args[4];

        int count = (int) args[5];
        double speed = (double) args[6];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("style_name", styleName)
                .addPlaceholder("particle", particle.name())
                .addPlaceholder("offset_x", offsetX)
                .addPlaceholder("offset_y", offsetY)
                .addPlaceholder("offset_z", offsetZ)
                .addPlaceholder("count", count)
                .addPlaceholder("speed", speed)
                .build();

        ParticleStyle style = ParticleStyleManager.getParticleStyle(styleName);
        if (style == null) {
            if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style_does_not_exist", placeholders);
            return;
        }

        style.setParticle(particle);
        style.setOffset(new Vector(offsetX, offsetY, offsetZ));
        style.setCount(count);
        style.setSpeed(speed);
        if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.particle.set", placeholders);
    }

    // ----- CREATE CREATE COMMAND -----

    private static CommandAPICommand createCreateCommand() {
        return new CommandAPICommand("create")
                .withPermission("cobalt.core.commands.cparticle.style")
                .withArguments(new StringArgument("name"))
                .withArguments(new StringArgument("styleName").replaceSuggestions(info -> ParticleStyleManager.getInternalParticleStyleNames()))
                .withArguments(new ParticleArgument("particle"))
                .withArguments(new DoubleArgument("offset_x"))
                .withArguments(new DoubleArgument("offset_y"))
                .withArguments(new DoubleArgument("offset_z"))
                .withArguments(new IntegerArgument("count"))
                .withArguments(new DoubleArgument("speed"))
                .executes(ParticleStyleCommand::createParticleStyle);
    }

    private static void createParticleStyle(CommandSender sender, Object[] args) {
        // Get variables.
        String name = (String) args[0];
        String internalStyleName = (String) args[1];
        Particle particle = (Particle) args[2];
        double offsetX = (double) args[3];
        double offsetY = (double) args[4];
        double offsetZ = (double) args[5];
        int count = (Integer) args[6];
        double speed = (Double) args[7];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("name", name)
                .build();

        boolean created = ParticleStyleManager.createParticleStyle(internalStyleName, name, particle, new Vector(offsetX, offsetY, offsetZ), count, speed, null);

        // Send message
        if (sender instanceof Player player) {
            if (created) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.create.success", placeholders);
            else LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.create.error.already_exists", placeholders);
        }
    }

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
                                .addPlaceholder("style_name", particleStyle.getName())
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

    // ----- CREATE ROTATION COMMAND -----

    private static CommandAPICommand createRotationCommand() {
        return new CommandAPICommand("rotation")
                .withPermission("cobalt.core.commands.cparticle.style.rotation")
                .withArguments(new StringArgument("name").replaceSuggestions(suggestionInfo -> ParticleStyleManager.getParticleStyleNames()))
                .withArguments(new DoubleArgument("x_rotation"))
                .withArguments(new DoubleArgument("y_rotation"))
                .withArguments(new DoubleArgument("z_rotation"))
                .withArguments(new DoubleArgument("angular_velocity_x"))
                .withArguments(new DoubleArgument("angular_velocity_y"))
                .withArguments(new DoubleArgument("angular_velocity_z"))
                .executes(ParticleStyleCommand::rotateStyle);
    }

    /**
     * Applies rotation to a <code>ParticleStyles</code>.
     * @param sender the <code>CommandSender</code>.
     * @param args command arguments.
     */
    private static void rotateStyle(CommandSender sender, Object[] args) {
        String name = (String)args[0];
        Vector rotation = new Vector((double)args[1], (double)args[2], (double)args[3]);
        double angularVelocityX = (double) args[4];
        double angularVelocityY = (double) args[5];
        double angularVelocityZ = (double) args[6];

        // Create Placeholders
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("style_name", name)
                .build();

        // Update particle style
        ParticleStyle style = ParticleStyleManager.getParticleStyle(name);
        if (style == null) {
            if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style_does_not_exist", placeholders);
            return;
        }

        style.setRotation(rotation);
        style.setAngularVelocity(angularVelocityX, angularVelocityY, angularVelocityZ);
        if (sender instanceof Player player) {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.extra.set", placeholders);
            for (String s : style.getInfoStrings()) player.sendMessage(s);
        }
    }

    // ----- CREATE INFO COMMAND -----

    private static CommandAPICommand createInfoCommand() {
        return new CommandAPICommand("info")
                .withPermission("cobalt.core.command.cparticle.style.info")
                .withArguments(new StringArgument("style_name").replaceSuggestions(suggestionInfo -> ParticleStyleManager.getParticleStyleNames()))
                .executesPlayer(ParticleStyleCommand::printInfo);
    }

    /**
     * Prints info about a <code>ParticleStyle</code>.
     *
     * @param player the player to send the info to.
     * @param args the command arguments.
     */
    private static void printInfo(Player player, Object[] args) {
        String name = (String) args[0];
        ParticleStyle style = ParticleStyleManager.getParticleStyle(name);
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("style_name", name)
                .build();
        if (style == null) {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style_does_not_exist", placeholders);
            return;
        }

        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.info", placeholders);
        for (String s : style.getInfoStrings()) player.sendMessage(s);
    }

    // ----- CREATE REMOVE COMMAND -----

    private static CommandAPICommand createRemoveCommand() {
        return new CommandAPICommand("remove")
                .withPermission("cobalt.core.commands.cparticle.style.remove")
                .withArguments(new StringArgument("name").replaceSuggestions(suggestionInfo -> ParticleStyleManager.getParticleStyleNames()))
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
                    .addPlaceholder("style_name", name)
                    .build();

            if (removed) {
                LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style.remove.success", placeholders);
            } else {
                LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cparticle.style_does_not_exist", placeholders);
            }
        }
    }

}
