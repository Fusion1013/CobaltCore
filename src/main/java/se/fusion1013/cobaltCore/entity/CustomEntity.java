package se.fusion1013.cobaltCore.entity;

import org.bukkit.entity.EntityType;
import se.fusion1013.cobaltCore.CobaltPlugin;

public class CustomEntity extends AbstractCustomEntity implements ICustomEntity {

    public CustomEntity(CobaltPlugin plugin, String internalName, EntityType entityType) {
        super(plugin, internalName, entityType);
    }

    public CustomEntity(String internalName, EntityType entityType) {
        super(internalName, entityType);
    }

    public static class Builder extends AbstractCustomEntity.Builder<CustomEntity, Builder> {

        public Builder(String internalName, EntityType entityType) {
            super(internalName, entityType);
        }

        @Override
        protected CustomEntity createObj() {
            return new CustomEntity(internalName, entityType);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }

}
