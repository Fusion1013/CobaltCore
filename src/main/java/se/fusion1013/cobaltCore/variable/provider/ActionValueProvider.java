package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.components.actions.ActionManager;
import se.fusion1013.cobaltCore.components.actions.IAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ActionValueProvider extends AbstractValueProvider<IAction> {

    private static final Random random = new Random();
    private final List<IAction> actions = new ArrayList<>();

    public ActionValueProvider(String parameterName, IAction action) {
        super(parameterName);
        this.actions.add(action);
    }

    public ActionValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public boolean isEmpty() {
        return actions.isEmpty();
    }

    @Override
    public IAction getValue() {
        return actions.get(random.nextInt(actions.size()));
    }

    @Override
    public void setValue(IAction value) {
        actions.clear();
        actions.add(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (!yaml.contains(parameterName)) return;
        loadActionsFromMapList(yaml.getMapList(parameterName));
    }

    @Override
    public void load(Map<?, ?> map) {
        if (!map.containsKey(parameterName)) return;

        Object value = map.get(parameterName);

        List<Map<?, ?>> actionDatas = (List<Map<?, ?>>) value;
        loadActionsFromMapList(actionDatas);
    }

    private void loadActionsFromMapList(List<Map<?, ?>> actionDatas) {
        for (Map<?, ?> actionData : actionDatas) {
            String actionName = (String) actionData.get("action");
            addActionFromMap(actionData, actionName);
        }
    }

    private void addActionFromMap(Map<?, ?> actionData, String actionName) {
        IAction action = ActionManager.getInstance().getNewAction(actionName, actionData);
        if (action == null) return;
        actions.add(action);
    }

    @Override
    public List<IAction> getValueList() {
        return actions;
    }
}
