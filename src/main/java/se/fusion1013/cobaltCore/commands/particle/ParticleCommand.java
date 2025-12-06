package se.fusion1013.cobaltCore.commands.particle;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.particle.effects.IParticleEffect;
import se.fusion1013.cobaltCore.particle.effects.ParticleEffectManager;
import se.fusion1013.cobaltCore.particle.transformation.RotationYTransformation;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;

public class ParticleCommand {

    public static final Argument PARTICLE_EFFECT_ARGUMENT = new StringArgument("effect").replaceSuggestions(ArgumentSuggestions.strings(info -> ParticleEffectManager.getDefaultParticleEffectNames()));
    public static final Argument CUSTOM_PARTICLE_EFFECT_ARGUMENT = new StringArgument("effect").replaceSuggestions(ArgumentSuggestions.strings(info -> ParticleEffectManager.getCustomParticleEffectNames()));

    public static void register() {
        new CommandAPICommand("cparticle")
                .withPermission("commands.core.cparticle")
                .withSubcommand(createDisplayCommand())
//                .withSubcommand(createTestMorph())
                .withSubcommand(ParticleEffectCommand.createParticleEffectCommand())
                .register();
    }

    private static CommandAPICommand createDisplayCommand() {
        CommandAPICommand displayCommand = new CommandAPICommand("display")
                .withPermission("commands.core.cparticle");

        for (String particleEffectName : ParticleEffectManager.getDefaultParticleEffectNames()) {
            IParticleEffect effect = ParticleEffectManager.getParticleEffect(particleEffectName);
            if (effect == null) continue;

            ICommandValue[] commandValues = effect.getCommandIntegration().getValues();
            displayCommand.withSubcommand(createParticleDisplaySubcommand(effect, commandValues));
        }

        return displayCommand;
    }

    private static CommandAPICommand createParticleDisplaySubcommand(IParticleEffect particleEffect, ICommandValue[] commandValues) {
        CommandAPICommand command = new CommandAPICommand(particleEffect.getName());
        for (ICommandValue commandValue : commandValues) {
            command.withArguments(commandValue.getArgument());
        }
        command.executesNative((s, a) -> {
            displayParticle(particleEffect, a, s.getLocation());
        });
        return command;
    }

    private static void displayParticle(IParticleEffect particleEffect, CommandArguments args, Location location) {
        ICommandValue[] commandValues = particleEffect.getCommandIntegration().getValues();
        for (ICommandValue commandValue : commandValues) {
            commandValue.setValue(args);
        }

        TransformationPipeline pipeline = new TransformationPipeline();
        RotationYTransformation rotationYTransformation = new RotationYTransformation(Math.toRadians(location.getRotation().yaw()));
        pipeline.add(rotationYTransformation);
        particleEffect.display(location, null, pipeline);
    }






    /*

    private static CommandAPICommand createTestMorph() {
        return new CommandAPICommand("test_morph")
                .withPermission("commands.core.cparticle.test_morph")
                .withArguments(new LocationArgument("position", LocationType.PRECISE_POSITION, false))
                .withArguments(new ParticleArgument("particle"))
                .withArguments(new StringArgument("style_1").replaceSuggestions(ArgumentSuggestions.strings(info -> ParticleEffectManager.getDefaultParticleEffectNames())))
                .withArguments(new StringArgument("style_2").replaceSuggestions(ArgumentSuggestions.strings(info -> ParticleEffectManager.getDefaultParticleEffectNames())))
                .executes((commandSender, commandArguments) -> {
                   Location location = (Location) commandArguments.args()[0];
                   ParticleData particleData = (ParticleData) commandArguments.args()[1];
                   String style1 = (String) commandArguments.args()[2];
                   String style2 = (String) commandArguments.args()[3];

                   IParticleEffect effect1 = ParticleEffectManager.getParticleEffect(style1);
                   IParticleEffect effect2 = ParticleEffectManager.getParticleEffect(style2);

                    List<Vector> fromPoints = effect1.getPoints();
                    List<Vector> toPoints = effect2.getPoints();

                    startMorph(fromPoints, toPoints, particleData.particle(), location, 10);
                });
    }

    private static void startMorph(List<Vector> fromPoints, List<Vector> toPoints, Particle particle, Location location, int depth) {
        if (depth == 0) return;

        ParticleEffectMorph morph = new ParticleEffectMorph(fromPoints, toPoints, particle);

        Bukkit.getScheduler().runTaskTimer(CobaltCore.getInstance(), task -> {
            morph.setProgress(morph.getProgress() + 0.025); // 0 → 1 over 20 ticks
            morph.display(location);

            if (morph.getProgress() >= 1.0) {
                task.cancel();

                startMorph(toPoints, fromPoints, particle, location, depth-1);
            }
        }, 0L, 1L);
    }
     */

}
