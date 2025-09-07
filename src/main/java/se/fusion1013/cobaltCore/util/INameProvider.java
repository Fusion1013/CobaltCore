package se.fusion1013.cobaltCore.util;

public interface INameProvider {
    String getInternalName();
    default void onEnabled() {}
    default void onDisabled() {}
}
