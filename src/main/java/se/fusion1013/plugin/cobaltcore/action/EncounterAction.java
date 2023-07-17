package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.action.encounter.EncounterManager;
import se.fusion1013.plugin.cobaltcore.action.system.AbstractAction;
import se.fusion1013.plugin.cobaltcore.action.system.ActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.IActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.ILocationAction;

import java.util.Map;

public class EncounterAction extends AbstractAction implements ILocationAction {

    //region FIELDS

    private String encounter;

    //endregion

    protected EncounterAction(Map<?, ?> data) {
        super(data);

        if (data.containsKey("encounter")) encounter = (String) data.get("encounter");
    }

    @Override
    public String getInternalName() {
        return "encounter_action";
    }

    @Override
    public IActionResult activate() {
        if (extraData.containsKey("location")) {
            Location location = (Location) extraData.get("location");
            return activate(location);
        }

        return new ActionResult(false);
    }

    @Override
    public IActionResult activate(Location location) {
        boolean executed = EncounterManager.playEncounter(encounter, location);

        if (executed) return new ActionResult(true);
        else return new ActionResult(false);
    }
}
