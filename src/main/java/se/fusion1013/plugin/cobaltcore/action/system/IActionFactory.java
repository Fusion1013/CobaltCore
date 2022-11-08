package se.fusion1013.plugin.cobaltcore.action.system;

import java.util.Map;

public interface IActionFactory {

    IAction createAction(IActionType actionType, Map<?, ?> data);

    IAction createAction(String actionType, Map<?, ?> data);

}
