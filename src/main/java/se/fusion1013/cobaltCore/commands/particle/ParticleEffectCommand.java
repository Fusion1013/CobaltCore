package se.fusion1013.cobaltCore.commands.particle;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.particle.IParticleEffect;
import se.fusion1013.cobaltCore.particle.ParticleEffectManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

import java.util.List;

import static se.fusion1013.cobaltCore.commands.particle.ParticleCommand.CUSTOM_PARTICLE_EFFECT_ARGUMENT;
import static se.fusion1013.cobaltCore.commands.particle.ParticleCommand.PARTICLE_EFFECT_ARGUMENT;

public class ParticleEffectCommand {

    public static CommandAPICommand createParticleEffectCommand() {
        return new CommandAPICommand("effect")
                .withPermission("commands.core.cparticle.effect")
                .withSubcommand(createCreateCommand())
                .withSubcommand(createListCommand())
                .withSubcommand(createDisplayCommand())
                .withSubcommand(createModifyCommand());
    }

    private static CommandAPICommand createCreateCommand() {
        return new CommandAPICommand("create")
                .withPermission("commands.core.cparticle.effect.create")
                .withArguments(new StringArgument("name"))
                .withArguments(PARTICLE_EFFECT_ARGUMENT)
                .executes((commandSender, commandArguments) -> {
                    String effectName = (String) commandArguments.args()[0];
                    String effectType = (String) commandArguments.args()[1];

                    ParticleEffectManager.createParticleEffect(effectType, effectName);
                });
    }

    private static CommandAPICommand createListCommand() {
        return new CommandAPICommand("list")
                .withPermission("commands.core.cparticle.effect.list")
                .executesPlayer((commandSender, commandArguments) -> {
                    LocaleManager.getInstance().sendMessage("", commandSender, "commands.cobalt.particle.effect.list.header");

                    String[] customEffects = ParticleEffectManager.getCustomParticleEffectNames();
                    for (int i = 0; i < customEffects.length; i++) {
                        String effectName = customEffects[i];
                        IParticleEffect effect = ParticleEffectManager.getCustomEffect(effectName);

                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("name", effectName)
                                .addPlaceholder("id", i)
                                .addPlaceholder("type", effect.getName())
                                .build();
                        LocaleManager.getInstance().sendMessage("", commandSender, "commands.cobalt.particle.effect.list.item", placeholders);
                    }
                });
    }

    private static CommandAPICommand createDisplayCommand() {
        return new CommandAPICommand("display")
                .withPermission("commands.core.cparticle.effect.display")
                .withArguments(CUSTOM_PARTICLE_EFFECT_ARGUMENT)
                .withArguments(new LocationArgument("position", LocationType.PRECISE_POSITION, false))
                .executes((commandSender, commandArguments) -> {
                    String effectName = (String) commandArguments.args()[0];
                    Location location = (Location) commandArguments.args()[1];

                    IParticleEffect effect = ParticleEffectManager.getCustomEffect(effectName);
                    effect.display(location);
                });
    }

    private static CommandAPICommand createModifyCommand() {
        CommandAPICommand command = new CommandAPICommand("modify")
                .withPermission("commands.core.cparticle.effect.modify");

        String[] effects = ParticleEffectManager.getDefaultParticleEffectNames();
        for (String s : effects) {
            command.withSubcommand(generateExtraSettingsSubcommand(ParticleEffectManager.getParticleEffect(s)));
        }

        return command;
    }

    private static CommandAPICommand generateExtraSettingsSubcommand(IParticleEffect effect) {
        List<Argument> arguments = effect.getModifyArguments();
        CommandAPICommand command = new CommandAPICommand(effect.getName());

        for (Argument argument : arguments) {
            CommandAPICommand node = new CommandAPICommand(argument.getNodeName());
            node.withArguments(CUSTOM_PARTICLE_EFFECT_ARGUMENT);
            node.withArguments(argument);
            node.executes((commandSender, commandArguments) -> {
                // Get arguments
                String effectName = (String) commandArguments.args()[0];
                Object setting = commandArguments.args()[1];
                String commandPath = argument.getNodeName();
                IParticleEffect particleEffect = ParticleEffectManager.getCustomEffect(effectName);

                // Set the value
                particleEffect.modify(commandPath, setting);

                // Send feedback if sender is a player
                if (commandSender instanceof Player player) {
                    List<String> info = particleEffect.getInfoStrings();

                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("name", particleEffect.getName())
                            .build();
                    LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cobalt.particle.effect.modify.set", placeholders);
                    for (String s : info) player.sendMessage(s);
                }
            });
            command.withSubcommand(node);
        }
        return command;
    }

}
