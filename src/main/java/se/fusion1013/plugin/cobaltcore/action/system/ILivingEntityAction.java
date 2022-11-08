package se.fusion1013.plugin.cobaltcore.action.system;

import org.bukkit.entity.LivingEntity;

public interface ILivingEntityAction extends IEntityAction {

    boolean activate(LivingEntity entity);
}
