package se.fusion1013.plugin.cobaltcore.action.encounter;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.FileUtil;
import se.fusion1013.plugin.cobaltcore.util.IFileConstructor;
import se.fusion1013.plugin.cobaltcore.util.INameProvider;
import se.fusion1013.plugin.cobaltcore.util.IProviderStorage;

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

        FileUtil.loadFilesInto(plugin, "encounters/", new IProviderStorage() {
            @Override
            public void put(String key, INameProvider provider) {
                ENCOUNTERS.put(key, (Encounter) provider);
            }

            @Override
            public boolean has(String key) {
                return ENCOUNTERS.containsKey(key);
            }

            @Override
            public INameProvider get(String key) {
                return ENCOUNTERS.get(key);
            }
        }, new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return new Encounter(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return new Encounter(json);
            }
        }, overwrite);
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
