package se.fusion1013.plugin.cobaltcore.debug;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ParticleDebugEvent implements IDebugEvent {

    List<String> description = new ArrayList<>();

    public ParticleDebugEvent() {}

    public ParticleDebugEvent addDescriptionLine(String line) {
        this.description.add(line);
        return this;
    }

    @Override
    public String getName() {
        return "particle";
    }

    @Override
    public void throwEvent(Player player) {
        for (String s : description) {
            player.sendMessage(s);
        }
    }

    @Override
    public String[] getDescription() {
        return description.toArray(new String[0]);
    }
}
