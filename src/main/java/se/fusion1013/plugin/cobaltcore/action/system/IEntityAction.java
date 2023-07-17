package se.fusion1013.plugin.cobaltcore.action.system;

import org.bukkit.entity.Entity;

public interface IEntityAction extends IAction {

    IActionResult activate(Entity entity);

}
