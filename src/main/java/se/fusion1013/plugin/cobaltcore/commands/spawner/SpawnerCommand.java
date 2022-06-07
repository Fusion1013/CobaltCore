package se.fusion1013.plugin.cobaltcore.commands.spawner;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.world.spawner.SpawnerManager;

public class SpawnerCommand {

    public static void register() {
        new CommandAPICommand("spawner")
                .withPermission("commands.core.spawner")
                .withSubcommand(createContinuousCommand())
                .withSubcommand(createInstantCommand())
                .register();
    }

    private static CommandAPICommand createContinuousCommand() {
        return new CommandAPICommand("create")
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("entity").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomEntityManager.getInternalEntityNames())))
                .withArguments(new IntegerArgument("spawn_count"))
                .withArguments(new DoubleArgument("activation_range"))
                .withArguments(new IntegerArgument("spawn_radius"))
                .withArguments(new IntegerArgument("cooldown"))
                .executesPlayer(((sender, args) -> {
                    Location location = (Location) args[0];
                    String entity = (String) args[1];
                    int spawnCount = (int) args[2];
                    double activationRange = (double) args[3];
                    int spawnRadius = (int) args[4];
                    int cooldown = (int) args[5];
                    SpawnerManager.getInstance().placeSpawner(location, entity, spawnCount, activationRange, spawnRadius, cooldown);
                }));
    }

    private static CommandAPICommand createInstantCommand() {
        return new CommandAPICommand("create")
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("entity").replaceSuggestions(ArgumentSuggestions.strings(CustomEntityManager.getInternalEntityNames())))
                .withArguments(new IntegerArgument("spawn_count"))
                .withArguments(new DoubleArgument("activation_range"))
                .withArguments(new IntegerArgument("spawn_radius"))
                .executesPlayer(((sender, args) -> {
                    Location location = (Location) args[0];
                    String entity = (String) args[1];
                    int spawnCount = (int) args[2];
                    double activationRange = (double) args[3];
                    int spawnRadius = (int) args[4];
                    SpawnerManager.getInstance().placeSpawner(location, entity, spawnCount, activationRange, spawnRadius);
                }));
    }

}
