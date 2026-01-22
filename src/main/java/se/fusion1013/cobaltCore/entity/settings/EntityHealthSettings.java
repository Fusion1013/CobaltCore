package se.fusion1013.cobaltCore.entity.settings;

import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;

public class EntityHealthSettings implements IEntitySettings {

    protected double maxHealth = 20;
    protected double healthScaling = 1;
    protected double scaleDistance = 0;

    @Override
    public void load(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            load(livingEntity);
        }
    }

    private void load(LivingEntity entity) {
        World world = entity.getWorld();
        Collection<Player> players = world.getNearbyPlayers(entity.getLocation(), scaleDistance);
        int nPlayers = Math.max(1, players.size());
        double scaling = ((healthScaling - 1) * nPlayers) + 1;

        entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth * scaling);
        entity.setHealth(maxHealth * scaling);
    }

    public static class Builder {

        private final EntityHealthSettings obj;

        public Builder() {
            obj = new EntityHealthSettings();
        }

        public EntityHealthSettings build() {
            return obj;
        }

        public Builder addMaxHealth(double maxHealth) {
            obj.maxHealth = maxHealth;
            return this;
        }

        public Builder addHealthScaling(double healthScaling) {
            obj.healthScaling = healthScaling;
            return this;
        }

        public Builder addHealthScaleDistance(double scaleDistance) {
            obj.scaleDistance = scaleDistance;
            return this;
        }
    }
}
