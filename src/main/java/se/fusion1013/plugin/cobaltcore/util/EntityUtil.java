package se.fusion1013.plugin.cobaltcore.util;

import dev.jorel.commandapi.SuggestionInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil {

    // ----- COMMAND ARGUMENTS -----

    public static String[] getEntityArguments(SuggestionInfo info) { // TODO
        List<String> names = new ArrayList<>();
        if (info.sender() instanceof Player player) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("cobalt.core.vanish") || !PlayerUtil.isVanished(p)) names.add(p.getName());
            }
        }
        return names.toArray(new String[0]);
    }

}
