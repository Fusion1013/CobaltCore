package se.fusion1013.plugin.cobaltcore.action.system;

import java.util.Map;

public interface IAction {

    //region GETTERS/SETTERS

    /**
     * Gets the internal name of the <code>IAction</code>.
     *
     * @return the internal name string.
     */
    String getInternalName();

    /**
     * Used to manually set a data point to be used during the <code>IAction</code> activation.
     *
     * @param key the key.
     * @param data the data.
     */
    void setExtraData(String key, Object data);

    boolean isCancelAction();

    //endregion

    //region ACTIVATION

    /**
     * Activates the <code>IAction</code> without inserting any additional data.
     *
     * @return the <code>IActionResult</code>.
     */
    IActionResult activate();

    /**
     * Activates the <code>IAction</code> with additional data.
     * The data given to this method will get override previously injected data.
     *
     * @param data the data to use during activation.
     * @return the <code>IActionResult</code>.
     */
    IActionResult activate(Map<String, Object> data);

    //endregion
}
