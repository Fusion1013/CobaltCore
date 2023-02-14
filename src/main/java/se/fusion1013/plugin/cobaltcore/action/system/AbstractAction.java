package se.fusion1013.plugin.cobaltcore.action.system;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAction implements IAction {

    //region FIELDS

    protected final Map<String, Object> extraData = new HashMap<>();

    private boolean isCancelAction = false;

    //endregion

    //region CONSTRUCTORS

    protected AbstractAction(Map<?, ?> data) {
        if (data.containsKey("is_cancel_action")) isCancelAction = (boolean) data.get("is_cancel_action");
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public void setExtraData(String key, Object data) {
        extraData.put(key, data);
    }

    @Override
    public boolean isCancelAction() {
        return isCancelAction;
    }

    //endregion

}
