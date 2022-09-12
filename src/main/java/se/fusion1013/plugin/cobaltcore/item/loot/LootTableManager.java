package se.fusion1013.plugin.cobaltcore.item.loot;

import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.FileUtil;

import java.io.FileReader;
import java.io.IOException;

public class LootTableManager extends Manager {

    // ----- VARIABLES -----

    // ----- LOOT TABLE FILE LOADING -----

    public void loadLootTableFile(Plugin plugin, String filePath) { // TODO: Load LootTables from json files
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(FileUtil.getOrCreateFileFromResource(plugin, filePath)));
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray pools = (JSONArray) jsonObject.get("pools");

        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }
    }

    // ----- CONSTRUCTORS -----

    public LootTableManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static LootTableManager INSTANCE = null;
    /**
     * Returns the object representing this <code>LootTableManager</code>.
     *
     * @return The object of this class.
     */
    public static LootTableManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new LootTableManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
