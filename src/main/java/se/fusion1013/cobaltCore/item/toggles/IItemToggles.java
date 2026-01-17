package se.fusion1013.cobaltCore.item.toggles;

public interface IItemToggles {

    boolean getValue(ItemToggleType type);

    void setValue(ItemToggleType type, boolean value);

}
