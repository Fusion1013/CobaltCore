package se.fusion1013.plugin.cobaltcore.commands.particle;

import dev.jorel.commandapi.CommandAPICommand;

public class MainParticleCommand {

    // ----- REGISTER -----

    public static void register() {
        new CommandAPICommand("cparticle")
                .withPermission("cobalt.core.command.cparticle")
                .withSubcommand(ParticleStyleCommand.createParticleStyleCommand())
                .register();
    }

}
