package se.fusion1013.cobaltCore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.util.FileUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestCommand {

    public static void register() {
        new CommandAPICommand("testglyph")
                .withArguments(new LocationArgument("location"))
                .withArguments(new DoubleArgument("scale"))
                .executes((sender, args) -> {
                    Location location = (Location) args.get("location");
                    double scale = (double) args.get("scale");

                    File glyph = FileUtil.getOrCreateFileFromResource(CobaltCore.getInstance(), "glyphs/aether_rune/glyph_a.png");
                    displayGlyph(location, glyph, scale);
                }).register();
    }

    public static void displayGlyph(Location origin, File imageFile, double scale) {
        try {
            BufferedImage img = ImageIO.read(imageFile);

            // Image dimensions
            int width = img.getWidth();
            int height = img.getHeight();

            // Scaling factor – how far apart particles are

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    int argb = img.getRGB(x, y);
                    int alpha = (argb >> 24) & 0xff;

                    // Skip transparent pixels
                    if (alpha < 10) continue;

                    // Compute world position
                    double px = origin.getX() + (x - width / 2.0) * scale;
                    double py = origin.getY() + (height - y) * scale;
                    double pz = origin.getZ();

                    Location particleLoc = new Location(origin.getWorld(), px, py, pz);

                    // Spawn particle (example: FLAME)
                    origin.getWorld().spawnParticle(
                            Particle.FLAME,
                            particleLoc,
                            1,
                            0, 0, 0,
                            0
                    );
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
