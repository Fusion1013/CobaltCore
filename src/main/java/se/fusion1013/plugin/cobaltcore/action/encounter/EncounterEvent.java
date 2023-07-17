package se.fusion1013.plugin.cobaltcore.action.encounter;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.action.system.ActionManager;
import se.fusion1013.plugin.cobaltcore.action.system.IAction;
import se.fusion1013.plugin.cobaltcore.action.system.IActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.ILocationAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EncounterEvent {

    //region FIELDS

    private final String name;

    // -- Timekeeping
    private final double startTime;
    private final double endTime;

    public boolean executed = false;

    // -- Actions
    private final List<IAction> actions = new ArrayList<>();

    //endregion

    //region CONSTRUCTORS

    public EncounterEvent(String name, Map<?, ?> data) {
        this(name, data, false);
    }

    public EncounterEvent(String name, Map<?, ?> data, boolean timeInSeconds) {
        this.name = name;

        int timeMultiplier = 1;
        if (timeInSeconds) timeMultiplier = 1000;

        startTime = (double) data.get("start_time") * timeMultiplier;

        if (data.containsKey("end_time")) endTime = (double) data.get("end_time") * timeMultiplier;
        else endTime = startTime;

        if (data.containsKey("actions")) actions.addAll(ActionManager.getActions((List<Map<?,?>>) data.get("actions")));
    }

    //endregion

    //region EXECUTE

    public List<Map<String, Object>> attemptExecute(Location location, double currentTime) {
        if ((startTime <= currentTime && currentTime <= endTime) || (!executed && currentTime > endTime)) {
            executed = true;

            return executeLocationActions(location);
        }
        return null;
    }

    private List<Map<String, Object>> executeLocationActions(Location location) {
        List<Map<String, Object>> extraData = new ArrayList<>();

        for (IAction action : actions) {
            if (action instanceof ILocationAction locationAction) {
                IActionResult result = locationAction.activate(location);

                extraData.add(result.getData());
            }
        }

        return extraData;
    }

    //endregion

    //region GETTERS/SETTERS

    public IAction[] getActions() {
        return actions.toArray(new IAction[0]);
    }

    public String getName() {
        return name;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    //endregion

}
