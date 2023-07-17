package se.fusion1013.plugin.cobaltcore.action.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionContext {

    //region FIELDS

    private final Map<String, Object> DATA = new HashMap<>();
    private final Map<String, List<IAction>> ACTION_MAP = new HashMap<>();

    //endregion

    //region EXECUTE

    public void executeActions(String key) {
        if (ACTION_MAP.containsKey(key)) {
            for (IAction action : ACTION_MAP.get(key)) {
                IActionResult result = action.activate(DATA);
                DATA.putAll(result.getData());
            }
        }
    }

    //endregion

    //region GETTERS/SETTERS

    public void addAction(String key, IAction action) {
        ACTION_MAP.computeIfAbsent(key, k -> new ArrayList<>()).add(action);
    }

    public void addActions(String key, List<IAction> actions) {
        ACTION_MAP.computeIfAbsent(key, k -> new ArrayList<>()).addAll(actions);
    }

    public void addData(String key, Object data) {
        DATA.put(key, data);
    }

    public void addData(Map<String, Object> data) {
        DATA.putAll(data);
    }

    //endregion

}
