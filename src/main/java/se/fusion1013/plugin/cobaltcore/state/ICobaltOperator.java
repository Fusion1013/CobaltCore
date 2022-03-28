package se.fusion1013.plugin.cobaltcore.state;

/**
 * Defines an operator performed on an item every tick
 */
public interface ICobaltOperator<T> {
    /**
     * Defines an operator performed on an item every tick
     */
    void performOperation(T item, CobaltState<T> state);
}
