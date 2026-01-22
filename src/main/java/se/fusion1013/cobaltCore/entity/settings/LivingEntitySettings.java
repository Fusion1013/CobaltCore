package se.fusion1013.cobaltCore.entity.settings;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class LivingEntitySettings implements IEntitySettings {

    @Override
    public void load(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            load(livingEntity);
        }
    }

    private void load(LivingEntity entity) {
    }

}
