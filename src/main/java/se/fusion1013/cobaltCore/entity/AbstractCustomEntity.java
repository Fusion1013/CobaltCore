package se.fusion1013.cobaltCore.entity;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;

public abstract class AbstractCustomEntity implements ICustomEntity {

    private static final NamespacedKey CUSTOM_ENTITY_KEY = new NamespacedKey(CobaltCore.getInstance(), "is_custom_entity");

    private final String internalName;
    private final NamespacedKey key;
    private final EntityType entityType;

    public AbstractCustomEntity(String internalName, EntityType entityType) {
        this.internalName = internalName;
        this.entityType = entityType;
        this.key = new NamespacedKey(CobaltCore.getInstance(), this.internalName);
    }

    public AbstractCustomEntity(CobaltPlugin plugin, String internalName, EntityType entityType) {
        this.internalName = internalName;
        this.entityType = entityType;
        this.key = new NamespacedKey(plugin, this.internalName);
    }

    public void onLoad() {

    }

    @Override
    public ICustomEntityInstance spawn(World world, Location location) {
        Entity customEntity = world.spawnEntity(location, entityType, CreatureSpawnEvent.SpawnReason.COMMAND, entity -> {
            PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
            persistentDataContainer.set(CUSTOM_ENTITY_KEY, PersistentDataType.INTEGER, 1);
            persistentDataContainer.set(key, PersistentDataType.INTEGER, 1);
        });
        return new CustomEntityInstance(customEntity);
    }

    protected static abstract class Builder<T extends AbstractCustomEntity, B extends Builder> {

        private final T obj;
        public final String internalName;
        public final EntityType entityType;

        public Builder(String internalName, EntityType entityType) {
            this.internalName = internalName;
            this.entityType = entityType;
            obj = createObj();
        }

        public T build() {
            obj.onLoad();
            return obj;
        }

        protected abstract T createObj();

        protected abstract B getThis();

    }

    // ----- GETTER / SETTER -----


    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public String getId() {
        return internalName;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }
}
