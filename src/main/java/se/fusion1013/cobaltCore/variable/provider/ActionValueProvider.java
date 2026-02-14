package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.components.actions.ActionManager;
import se.fusion1013.cobaltCore.components.actions.IAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionValueProvider extends AbstractValueProvider<List<IAction>> {

    private final List<IAction> actions = new ArrayList<>();

    public ActionValueProvider(String parameterName, List<IAction> actions) {
        super(parameterName);
        this.actions.addAll(actions);
    }

    public ActionValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public List<IAction> getValue() {
        return actions;
    }

    @Override
    public void setValue(List<IAction> value) {
        actions.clear();
        actions.addAll(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {

    }

    @Override
    public void load(Map<?, ?> map) {
        if (!map.containsKey(parameterName)) return;

        List<Map<?, ?>> actionDatas = (List<Map<?, ?>>) map.get(parameterName);
        for (Map<?, ?> actionData : actionDatas) {
            String actionName = (String) actionData.get("action");
            IAction action = ActionManager.getInstance().getNewAction(actionName);
            action.loadFromMap(actionData);
            actions.add(action);
        }
    }
}
