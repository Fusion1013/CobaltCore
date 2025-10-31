package se.fusion1013.cobaltCore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Optional;

public class RotateAround {

    public static void register() {
        new CommandAPICommand("revolve")
                .withPermission("commands.core.revolve")
                .withArguments(new EntitySelectorArgument.ManyEntities("entities"))
                .withArguments(new LocationArgument("center"))
                .withArguments(new DoubleArgument("distance"))
                .withOptionalArguments(new DoubleArgument("speed"))
                .withOptionalArguments(new DoubleArgument("x_rotation"))
                .withOptionalArguments(new DoubleArgument("y_rotation"))
                .withOptionalArguments(new DoubleArgument("z_rotation"))
                .executes(RotateAround::revolve)
                .register();
    }

    private static void revolve(CommandSender sender, CommandArguments args) {
        Collection<Entity> entities = (Collection<Entity>) args.get("entities");
        if (entities == null || entities.isEmpty()) return;

        Location center = (Location) args.get("center");
        double distance = (double) args.get("distance");
        Optional<Object> optionalSpeed = args.getOptional("speed");
        double speed = optionalSpeed.map(o -> (double) o).orElse(1.0);

        Optional<Object> optionalRotationX = args.getOptional("x_rotation");
        Optional<Object> optionalRotationY = args.getOptional("y_rotation");
        Optional<Object> optionalRotationZ = args.getOptional("z_rotation");

        double xRotation = optionalRotationX.map(o -> (double) o).orElse(0.0);
        double yRotation = optionalRotationY.map(o -> (double) o).orElse(0.0);
        double zRotation = optionalRotationZ.map(o -> (double) o).orElse(0.0);

        revolveAround(entities, center, distance, speed, Math.toRadians(yRotation), Math.toRadians(xRotation), Math.toRadians(zRotation));
    }

    private static void revolveAround(Collection<Entity> entities, Location center, double distance, double speed) {


//        int nEntities = entities.size();
//        if (nEntities == 0) return;
//
//        long time = System.currentTimeMillis();
//        double angleOffset = (time * speed / 1000.0);
//
//        int i = 0;
//        for (Entity entity : entities) {
//            double angle = (2 * Math.PI * i / nEntities) + angleOffset;
//
//            double x = center.getX() + distance * Math.cos(angle);
//            double z = center.getZ() + distance * Math.sin(angle);
//            double y = center.getY();
//
//            Location newLocation = new Location(center.getWorld(), x, y, z);
//            newLocation.setYaw(entity.getLocation().getYaw());
//            newLocation.setPitch(entity.getLocation().getPitch());
//            entity.teleport(newLocation);
//
//            i++;
//        }
    }

    private static void revolveAround(Collection<Entity> entities, Location center, double distance, double speed,
                              double yaw,   // rotation around Y axis
                              double pitch, // rotation around X axis
                              double roll)  // rotation around Z axis
    {
        int size = entities.size();
        if (size == 0) return;

        long time = System.currentTimeMillis();
        double angleOffset = (time * speed / 1000.0);

        int i = 0;
        for (Entity entity : entities) {
            // Base angle for even distribution
            double angle = (2 * Math.PI * i / size) + angleOffset;

            // Default circle (in XZ plane, radius = distance)
            double x = distance * Math.cos(angle);
            double y = 0;
            double z = distance * Math.sin(angle);

            // Apply rotations (order: roll -> pitch -> yaw)
            // Rotation around Z (roll)
            double x1 = x * Math.cos(roll) - y * Math.sin(roll);
            double y1 = x * Math.sin(roll) + y * Math.cos(roll);
            double z1 = z;

            // Rotation around X (pitch)
            double y2 = y1 * Math.cos(pitch) - z1 * Math.sin(pitch);
            double z2 = y1 * Math.sin(pitch) + z1 * Math.cos(pitch);
            double x2 = x1;

            // Rotation around Y (yaw)
            double x3 = x2 * Math.cos(yaw) + z2 * Math.sin(yaw);
            double z3 = -x2 * Math.sin(yaw) + z2 * Math.cos(yaw);
            double y3 = y2;

            // Final position relative to center
            Location newLoc = new Location(center.getWorld(),
                    center.getX() + x3,
                    center.getY() + y3,
                    center.getZ() + z3);

            // Preserve facing direction
            newLoc.setYaw(entity.getLocation().getYaw());
            newLoc.setPitch(entity.getLocation().getPitch());

            entity.teleport(newLoc);
            i++;
        }
    }

}
