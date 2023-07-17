package se.fusion1013.plugin.cobaltcore.action.system;

import java.util.HashMap;
import java.util.Map;

public class ActionResult implements IActionResult {

    //region FIELDS

    // -- Required
    public boolean activated;

    // -- Optional Values
    public Map<String, Object> extraData = new HashMap<>();

    //endregion

    //region CONSTRUCTION

    public ActionResult(boolean activated) {
        this.activated = activated;
    }

    public ActionResult extraData(String key, Object data) {
        extraData.put(key, data);
        return this;
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public Object getData(String key) {
        return extraData.get(key);
    }

    @Override
    public boolean hasActivated() {
        return activated;
    }

    @Override
    public Map<String, Object> getData() {
        return extraData;
    }

    //endregion

}
