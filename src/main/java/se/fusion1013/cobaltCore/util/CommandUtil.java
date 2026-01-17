package se.fusion1013.cobaltCore.util;

import se.fusion1013.cobaltCore.CobaltPlugin;

public class CommandUtil {

    public static String getPermissionString(CobaltPlugin plugin, String commandPath) {
        return "cobalt." + plugin.getPrefix() + ".commands." + commandPath;
    }

}
