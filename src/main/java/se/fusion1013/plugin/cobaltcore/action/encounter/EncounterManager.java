package se.fusion1013.plugin.cobaltcore.action.encounter;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EncounterManager extends Manager {

    //region FIELDS

    private static final Map<String, Encounter> ENCOUNTERS = new HashMap<>();
    private static final Map<String, BukkitTask> RUNNING_ENCOUNTERS = new HashMap<>();

    //endregion

    //region FILE LOADING

    public static void loadEncounterFiles(CobaltPlugin plugin, boolean overwrite) {
        File dataFolder = plugin.getDataFolder();
        File encounterFolder = new File(dataFolder, "encounters/");
        loadEncountersFromFolders(plugin, encounterFolder, overwrite, "");
    }

    private static void loadEncountersFromFolders(CobaltPlugin plugin, File rootFolder, boolean overwrite, String prefix) {
        // Load from item files folder
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
            return;
        }

        plugin.getLogger().info("Loading encounters from folder '" + rootFolder.getName() + "'...");

        int encountersLoaded = 0;
        File[] files = rootFolder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) loadEncountersFromFolders(plugin, file, overwrite, file.getName() + "/");
            else {
                loadEncounter(plugin, file, overwrite, prefix);
                encountersLoaded ++;
            }
        }

        plugin.getLogger().info("Loaded " + encountersLoaded + " items from folder " + rootFolder.getName());
    }

    private static void loadEncounter(CobaltPlugin plugin, File file, boolean overwrite, String prefix) {
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }

        String internalName = yaml.getString("internal_name");
        if (internalName == null) return;

        String encounterPath = prefix + internalName;

        if (ENCOUNTERS.containsKey(encounterPath) && !overwrite) return;

        try {
            Encounter encounter = new Encounter(encounterPath, yaml);
            ENCOUNTERS.put(encounterPath, encounter);
        } catch (Exception ex) {
            CobaltCore.getInstance().getLogger().info("Error Loading Encounter: " + encounterPath + ". Stacktrace: " + ex.getMessage());
        }
    }

    //endregion

    public EncounterManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    //region RELOADING/DISABLING

    @Override
    public void reload() {

    }

    public static void reloadEncounters() {
        for (String id : RUNNING_ENCOUNTERS.keySet()) cancelEncounter(id);

        ENCOUNTERS.clear();
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadEncounterFiles(plugin, false);
        }
    }

    @Override
    public void disable() {

    }

    //endregion

    //region ENCOUNTER INTERACTIONS

    public static boolean playEncounter(String name, Location location) {
        UUID uuid = UUID.randomUUID();
        String id = name + "_" + uuid;

        if (ENCOUNTERS.containsKey(name)) RUNNING_ENCOUNTERS.put(id, ENCOUNTERS.get(name).trigger(location, id));
        else return false;
        return true;
    }

    public static boolean cancelEncounter(String id) {
        if (RUNNING_ENCOUNTERS.containsKey(id)) {
            RUNNING_ENCOUNTERS.get(id).cancel();
            RUNNING_ENCOUNTERS.remove(id);
            return true;
        }

        return false;
    }

    //endregion

    //region GETTERS/SETTERS

    public static Encounter getEncounter(String name) {
        return ENCOUNTERS.get(name);
    }

    public static String[] getRunningEncounterNames() {
        return RUNNING_ENCOUNTERS.keySet().toArray(new String[0]);
    }

    public static String[] getEncounterNames() {
        return ENCOUNTERS.keySet().toArray(new String[0]);
    }

    public static Encounter[] getEncounters() {
        return ENCOUNTERS.values().toArray(new Encounter[0]);
    }

    //endregion
}
