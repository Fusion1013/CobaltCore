package se.fusion1013.plugin.cobaltcore.util;

public interface INameProvider {
    String getInternalName();
    default void onEnabled() {}
    default void onDisabled() {}
}
