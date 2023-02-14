package se.fusion1013.plugin.cobaltcore.action.system;

public interface IAction {

    /**
     * Gets the internal name of the <code>IAction</code>.
     *
     * @return the internal name string.
     */
    String getInternalName();

    void setExtraData(String key, Object data);

    boolean isCancelAction();
}
