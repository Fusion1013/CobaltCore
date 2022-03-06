package se.fusion1013.plugin.cobaltcore.util;

import org.bukkit.plugin.Plugin;

import java.io.File;

public class FileUtil {

    /**
     * Gets a file from the specified path. If the file has not been created, create it from the plugin's resource folder if it exists.
     * @param plugin the plugin that is getting/creating the file.
     * @param filePath the path to the file.
     * @return the file, or null if file does not exist.
     */
    public static File getOrCreateFileFromResource(Plugin plugin, String filePath) {
        File file = new File(plugin.getDataFolder(), filePath);
        if (!file.exists() && plugin.getResource(filePath) != null) {
            file.getParentFile().mkdirs();
            plugin.saveResource(filePath, false);
        }
        return file;
    }

}
