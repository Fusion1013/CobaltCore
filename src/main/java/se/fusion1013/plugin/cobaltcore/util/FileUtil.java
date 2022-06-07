package se.fusion1013.plugin.cobaltcore.util;

import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    /**
     * Saves a <code>JSONObject</code> to the specified path.
     *
     * @param object the <code>JSONObject</code> to save.
     * @param path the path to save it at.
     * @return whether the file was created or not.
     */
    public static boolean saveJson(JSONObject object, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) file.getParentFile().mkdirs();

            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(object.toJSONString());
            fileWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Gets a file from the specified path. If the file has not been created, create it from the plugin's resource folder if it exists.
     *
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
