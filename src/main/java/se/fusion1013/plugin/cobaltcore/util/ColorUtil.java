package se.fusion1013.plugin.cobaltcore.util;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;

public class ColorUtil {

    public static TextColor fade(float ratio, TextColor far, TextColor close) {
        int red = (int)Math.abs((ratio * far.red()) + ((1 - ratio) * close.red()));
        int green = (int)Math.abs((ratio * far.green()) + ((1 - ratio) * close.green()));
        int blue = (int)Math.abs((ratio * far.blue()) + ((1 - ratio) * close.blue()));

        return TextColor.color(red, green, blue);
    }
}
