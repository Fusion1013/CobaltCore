package se.fusion1013.cobaltCore.components;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import se.fusion1013.cobaltCore.components.actions.ActionManager;
import se.fusion1013.cobaltCore.components.actions.IAction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Component extends AbstractComponent {

    private static final ActionManager ACTION_MANAGER = ActionManager.getInstance();
    private final List<IAction> actions = new ArrayList<>();

    @Override
    protected void run(Map<String, Object> context) {
        for (IAction action : actions) {
            action.execute(context);
        }
    }

    @Override
    public void load(ConfigurationSection yaml) {
        super.load(yaml);
        List<?> actionsData = yaml.getList("actions");
        for (Object actionData : actionsData) {
            if (actionData instanceof Map<?, ?> map) {
                String actionType = (String) map.get("action");
                IAction action = ActionManager.getInstance().getNewAction(actionType);
                action.loadFromMap(map);
                this.actions.add(action);
            }
        }
    }

    private List<ConfigurationSection> getConfigList(ConfigurationSection config, String path) {
        if (!config.isList(path)) return null;

        List<ConfigurationSection> list = new LinkedList();

        for (Object object : config.getList(path)) {
            if (object instanceof Map) {
                MemoryConfiguration mc = new MemoryConfiguration();
                mc.addDefaults((Map) object);

                list.add(mc);
            }
        }

        return list;
    }
}
