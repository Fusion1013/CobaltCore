package se.fusion1013.cobaltCore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import se.fusion1013.cobaltCore.util.EasingUtil;

import java.util.Arrays;
import java.util.Collection;

public class EaseCommand {

    public static void register() {
        new CommandAPICommand("ease")
                .withPermission("commands.core.ease")
                .withSubcommand(createEaseBetweenCommand())
                .register();
    }

    private static CommandAPICommand createEaseBetweenCommand() {
        return new CommandAPICommand("between")
                .withPermission("commands.core.ease.between")
                .withArguments(new EntitySelectorArgument.ManyEntities("entities"))
                .withArguments(new LocationArgument("location_1"))
                .withArguments(new LocationArgument("location_2"))
                .withArguments(new DoubleArgument("speed"))
                .withArguments(new StringArgument("easing_method").replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(EasingUtil.EasingMethod.values()).map(Enum::toString).toList())))
                .executes(EaseCommand::easeBetween);
    }

    private static void easeBetween(CommandSender sender, CommandArguments args) {
        Collection<Entity> entities = (Collection<Entity>) args.get("entities");
        if (entities.isEmpty()) return;

        Location startLocation = (Location) args.get("location_1");
        Location endLocation = (Location) args.get("location_2");
        double speed = (double) args.get("speed");
        String easingMethodString = (String) args.get("easing_method");
        EasingUtil.EasingMethod easingMethod = EasingUtil.EasingMethod.valueOf(easingMethodString);

        long currentTime = System.currentTimeMillis();
        double time = currentTime * speed;
        double eased = easingMethod.ease(time);

        Location newLocation = interpolate(startLocation, endLocation, eased);

        for (Entity entity : entities) {
            entity.teleport(newLocation);
        }
    }

    private static Location interpolate(Location start, Location end, double t) {
        if (t < 0) t = 0;
        if (t > 1) t = 1;

        double x = start.getX() + (end.getX() - start.getX()) * t;
        double y = start.getY() + (end.getY() - start.getY()) * t;
        double z = start.getZ() + (end.getZ() - start.getZ()) * t;

        float yaw = start.getYaw() + (end.getYaw() - start.getYaw()) * (float) t;
        float pitch = start.getPitch() + (end.getPitch() - start.getPitch()) * (float) t;

        return new Location(start.getWorld(), x, y, z, yaw, pitch);
    }

}
