package se.fusion1013.plugin.cobaltcore.debug;

import org.bukkit.entity.Player;

public class ParticleDebugEvent implements IDebugEvent {

    @Override
    public String getName() {
        return "particle";
    }

    @Override
    public void throwEvent(Player player) {}
}
