package se.fusion1013.cobaltCore.util;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class BlockUtil {

    // ----- COLOR RELATED BLOCK METHODS -----

    public static Material getColoredShulkerBox(NamedTextColor color) {
        return switch (color.toString()) {
            case "white" -> Material.WHITE_SHULKER_BOX;
            case "orange" -> Material.ORANGE_SHULKER_BOX;
            case "magenta" -> Material.MAGENTA_SHULKER_BOX;
            case "aqua" -> Material.LIGHT_BLUE_SHULKER_BOX;
            case "yellow" -> Material.YELLOW_SHULKER_BOX;
            case "lime" -> Material.LIME_SHULKER_BOX;
            case "pink" -> Material.PINK_SHULKER_BOX;
            case "gray" -> Material.GRAY_SHULKER_BOX;
            case "light_gray" -> Material.LIGHT_GRAY_SHULKER_BOX;
            case "cyan" -> Material.CYAN_SHULKER_BOX;
            case "purple" -> Material.PURPLE_SHULKER_BOX;
            case "blue" -> Material.BLUE_SHULKER_BOX;
            case "brown" -> Material.BROWN_SHULKER_BOX;
            case "green" -> Material.GREEN_SHULKER_BOX;
            case "red" -> Material.RED_SHULKER_BOX;
            case "black" -> Material.BLACK_SHULKER_BOX;
            default -> Material.SHULKER_BOX;
        };
    }

    public static Color getBlockColor(Material material) {
        return switch (material) {
            case WHITE_WOOL, WHITE_CONCRETE, WHITE_STAINED_GLASS_PANE, WHITE_STAINED_GLASS, WHITE_TERRACOTTA -> Color.WHITE;
            case ORANGE_WOOL, ORANGE_CONCRETE, ORANGE_STAINED_GLASS_PANE, ORANGE_STAINED_GLASS, ORANGE_TERRACOTTA -> Color.ORANGE;
            case MAGENTA_WOOL, MAGENTA_CONCRETE, MAGENTA_STAINED_GLASS_PANE, MAGENTA_STAINED_GLASS, MAGENTA_TERRACOTTA -> Color.fromBGR(151, 21, 173);
            case LIGHT_BLUE_WOOL, LIGHT_BLUE_CONCRETE, LIGHT_BLUE_STAINED_GLASS_PANE, LIGHT_BLUE_STAINED_GLASS, LIGHT_BLUE_TERRACOTTA -> Color.AQUA;
            case YELLOW_WOOL, YELLOW_CONCRETE, YELLOW_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS, YELLOW_TERRACOTTA -> Color.YELLOW;
            case LIME_WOOL, LIME_CONCRETE, LIME_STAINED_GLASS_PANE, LIME_STAINED_GLASS, LIME_TERRACOTTA -> Color.LIME;
            case PINK_WOOL, PINK_CONCRETE, PINK_STAINED_GLASS_PANE, PINK_STAINED_GLASS, PINK_TERRACOTTA -> Color.fromBGR(248, 3, 252);
            case GRAY_WOOL, GRAY_CONCRETE, GRAY_STAINED_GLASS_PANE, GRAY_STAINED_GLASS, GRAY_TERRACOTTA -> Color.fromBGR(46, 46, 46);
            case LIGHT_GRAY_WOOL, LIGHT_GRAY_CONCRETE, LIGHT_GRAY_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS, LIGHT_GRAY_TERRACOTTA -> Color.fromBGR(128, 128, 128);
            case CYAN_WOOL, CYAN_CONCRETE, CYAN_STAINED_GLASS_PANE, CYAN_STAINED_GLASS, CYAN_TERRACOTTA -> Color.fromBGR(16, 146, 163);
            case PURPLE_WOOL, PURPLE_CONCRETE, PURPLE_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS, PURPLE_TERRACOTTA -> Color.PURPLE;
            case BLUE_WOOL, BLUE_CONCRETE, BLUE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS, BLUE_TERRACOTTA -> Color.BLUE;
            case BROWN_WOOL, BROWN_CONCRETE, BROWN_STAINED_GLASS_PANE, BROWN_STAINED_GLASS, BROWN_TERRACOTTA -> Color.fromBGR(99, 57, 25);
            case GREEN_WOOL, GREEN_CONCRETE, GREEN_STAINED_GLASS_PANE, GREEN_STAINED_GLASS, GREEN_TERRACOTTA -> Color.GREEN;
            case RED_WOOL, RED_CONCRETE, RED_STAINED_GLASS_PANE, RED_STAINED_GLASS, RED_TERRACOTTA -> Color.RED;
            case BLACK_WOOL, BLACK_CONCRETE, BLACK_STAINED_GLASS_PANE, BLACK_STAINED_GLASS, BLACK_TERRACOTTA -> Color.BLACK;
            default -> Color.fromBGR(10, 10,10);
        };
    }
}
