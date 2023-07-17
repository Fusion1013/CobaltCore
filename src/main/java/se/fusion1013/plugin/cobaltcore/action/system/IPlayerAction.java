package se.fusion1013.plugin.cobaltcore.action.system;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IPlayerAction extends ILivingEntityAction {

    IActionResult activate(Player player);

}
