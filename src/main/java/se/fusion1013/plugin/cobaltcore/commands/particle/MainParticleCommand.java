package se.fusion1013.plugin.cobaltcore.commands.particle;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ParticleArgument;
import org.bukkit.Particle;

public class MainParticleCommand {

    // ----- REGISTER -----

    public static void register() {


        new CommandAPICommand("dparticle")
                .withPermission("cobalt.core.command.dparticle")
                .withArguments(new ParticleArgument("particle"))
                .executesPlayer(((sender, args) -> {
                    Particle particle = (Particle) args.args()[0];
                    sender.getWorld().spawnParticle(particle, sender.getLocation(), 1);
                }))
                .register();


        new CommandAPICommand("cparticle")
                .withPermission("cobalt.core.command.cparticle")
                .withSubcommand(ParticleStyleCommand.createParticleStyleCommand())
                .withSubcommand(ParticleGroupCommand.createParticleGroupCommand())
                .register();
    }

}
