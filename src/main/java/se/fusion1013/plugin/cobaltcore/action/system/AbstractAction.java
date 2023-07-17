package se.fusion1013.plugin.cobaltcore.action.system;

import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    //region ACTIVATION

    @Override
    public IActionResult activate(Map<String, Object> data) {
        extraData.putAll(data);
        return activate();
    }

    //endregion

    //region GETTERS/SETTERS

    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("is_cancel_action", isCancelAction)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("action.abstract.info", placeholders));
        return info;
    }

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
