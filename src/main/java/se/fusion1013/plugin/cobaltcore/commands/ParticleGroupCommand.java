package se.fusion1013.plugin.cobaltcore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;

public class ParticleGroupCommand {

    // ----- REGISTER -----

    public static void register() {
        new CommandAPICommand("particlegroup")
                .withPermission("commands.core.particlegroup")
                .withSubcommand(createParticleGroupCreateCommand())
                .register();
    }

    // ----- CREATE COMMAND -----

    private static CommandAPICommand createParticleGroupCreateCommand() {
        return new CommandAPICommand("create")
                .withPermission("commands.core.particlegroup.create")
                .withArguments(new LocationArgument("location"))
                .executes(((sender, args) -> {
                }));
    }

}
