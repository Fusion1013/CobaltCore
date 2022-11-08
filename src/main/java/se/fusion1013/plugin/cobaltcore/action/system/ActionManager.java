package se.fusion1013.plugin.cobaltcore.action.system;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.action.CoreActionFactory;
import se.fusion1013.plugin.cobaltcore.action.CoreActionType;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionManager extends Manager {

    // ----- VARIABLES -----

    // -- Factories
    private static final List<IActionFactory> REGISTERED_FACTORIES = new ArrayList<>();
    private static final IActionFactory CORE_FACTORY = registerFactory(new CoreActionFactory());

    // ----- REGISTER -----

    public static IActionFactory registerFactory(IActionFactory factory) {
        REGISTERED_FACTORIES.add(factory);
        return factory;
    }

    // ----- GETTERS / SETTERS -----

    public static List<IAction> getActions(List<Map<?, ?>> actionList) {
        List<IAction> foundActions = new ArrayList<>();

        for (Map<?, ?> actionMap : actionList) {
            actionMap.keySet().forEach(k -> {
                String actionTypeString = (String) k;
                Map<?, ?> data = (Map<?, ?>) actionMap.get(k);
                IAction action = getAction(actionTypeString, data);
                foundActions.add(action);
            });
        }

        return foundActions;
    }

    /**
     * Creates a new <code>IAction</code>.
     *
     * @param actionType the type of the <code>IAction</code>.
     * @param data the data to populate the <code>IActon</code> with.
     * @return a new <code>IAction</code>, or null if it could not be created.
     */
    public static IAction getAction(String actionType, Map<?, ?> data) {
        for (IActionFactory factory : REGISTERED_FACTORIES) {
            IAction action = factory.createAction(actionType, data);
            if (action != null) return action;
        }

        return null;
    }

    /**
     * Creates a new <code>IAction</code>.
     *
     * @param actionType the type of the <code>IAction</code>.
     * @param data the data to populate the <code>IActon</code> with.
     * @return a new <code>IAction</code>, or null if it could not be created.
     */
    public static IAction getAction(IActionType actionType, Map<?, ?> data) {
        for (IActionFactory factory : REGISTERED_FACTORIES) {
            IAction action = factory.createAction(actionType, data);
            if (action != null) return action;
        }

        return null;
    }

    // ----- CONSTRUCTORS -----

    public ActionManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }
}
