package se.fusion1013.plugin.cobaltcore.debug;

import org.bukkit.entity.Player;

public interface IDebugEvent {
    String getName();
    void throwEvent(Player player);
}
