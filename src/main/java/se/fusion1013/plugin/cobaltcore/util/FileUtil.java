package se.fusion1013.plugin.cobaltcore.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

    public static String[] getResources(Class clazz, String path) {

        // gets the resource path for the jar file
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL == null) return new String[0];

        // create the return list of resource filenames inside path provided
        ArrayList<String> result = new ArrayList<String>();

        // get the path of the jar file
        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file

        // decode the compiled jar for iteration
        JarFile jar = null;
        try {
            jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            CobaltCore.getInstance().getServer().getConsoleSender().sendMessage("ERROR - getResources() - couldn't decode the Jar file to index resources.");
        } catch (IOException ex) {
            CobaltCore.getInstance().getServer().getConsoleSender().sendMessage("ERROR - getResources() - couldn't perform IO operations on jar file");
        }

        // gets all the elements in a jar file for iterating through
        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar

        // iterate through and add elements inside the structures folder to the resources to be moved.
        while(entries.hasMoreElements()) {
            String name = entries.nextElement().getName();
            // check that element starts with path
            if (name.startsWith(path)) {
                String entry = name.substring(path.length() + 1);
                String last = name.substring(name.length()- 1);

                // discard if it is a directory
                if (last != File.separator){
                    // resource contains at least one character or number
                    if (entry.matches(".*[a-zA-Z0-9].*")) {
                        //getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Found an element that starts with the correct path: " + name);
                        //getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Chopped off just the resource name: " + entry);
                        result.add(entry);
                    }
                }
            }
        }

        // return the array of strings of filenames inside path.
        return result.toArray(new String[result.size()]);

    }

    public static void loadFilesInto(CobaltPlugin plugin, String folderName, IProviderStorage providerStorage, IFileConstructor constructor, boolean overwrite) {
        File dataFolder = plugin.getDataFolder();
        File fileFolder = new File(dataFolder, folderName);
        plugin.getLogger().info("Loading files from folder '" + folderName + "'...");
        int files = loadFromFolders(plugin, fileFolder, providerStorage, constructor, overwrite);
        files += loadFromResources(plugin, folderName, providerStorage, constructor, overwrite);
        plugin.getLogger().info("Loaded " + files + " files from folder '" + folderName + "'");
    }

    private static int loadFromFolders(CobaltPlugin plugin, File rootFolder, IProviderStorage providerStorage, IFileConstructor constructor, boolean overwrite) {
        // If folder does not exist, create it and return
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
            return 0;
        }

        int filesLoaded = 0;
        File[] files = rootFolder.listFiles();
        if (files == null) return filesLoaded;
        for (File file : files) {
            if (file.isDirectory()) filesLoaded += loadFromFolders(plugin, file, providerStorage, constructor, overwrite);
            else {
                if (tryLoadFile(plugin, file, providerStorage, constructor, overwrite)) filesLoaded++;
            }
        }

        return filesLoaded;
    }

    private static int loadFromResources(CobaltPlugin plugin, String folderName, IProviderStorage providerStorage, IFileConstructor constructor, boolean overwrite) {
        int filesLoaded = 0;
        String[] fileNames = getResources(plugin.getClass(), folderName);
        for (String s : fileNames) {
            File file = getOrCreateFileFromResource(plugin, folderName + s);
            if (file.exists()) if (tryLoadFile(plugin, file, providerStorage, constructor, overwrite)) filesLoaded++;
        }
        return filesLoaded;
    }

    private static boolean tryLoadFile(CobaltPlugin plugin, File file, IProviderStorage providerStorage, IFileConstructor constructor, boolean overwrite) {
        var provider = loadFile(file, constructor);
        if (provider == null) return false;

        // Check if the provider is already in the map
        var oldProvider = providerStorage.get(provider.getInternalName());
        if (oldProvider != null) {
            if (overwrite) {
                oldProvider.onDisabled();
            } else {
                plugin.getLogger().warning("A provider with the name '" + provider.getInternalName() + "' has already been registered, skipping");
                return false;
            }
        }

        providerStorage.put(provider.getInternalName(), provider);
        provider.onEnabled();

        return true;
    }

    private static INameProvider loadFile(File file, IFileConstructor constructor) {
        if (fileEnding(file).equalsIgnoreCase("yml")) return loadFileYml(file, constructor);
        if (fileEnding(file).equalsIgnoreCase("json")) return loadFileJson(file, constructor);
        return null;
    }

    private static INameProvider loadFileYml(File file, IFileConstructor constructor) {
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }

        return constructor.createFrom(yaml);
    }

    private static INameProvider loadFileJson(File file, IFileConstructor constructor) {
        return null; // TODO
    }

    public static String fileEnding(File file) {
        if (file.isDirectory()) return "";
        String extension = "";
        int i = file.getName().lastIndexOf('.');
        if (i >= 0) {
            extension = file.getName().substring(i+1);
        }
        return extension;
    }

}
