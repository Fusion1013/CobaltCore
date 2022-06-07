package se.fusion1013.plugin.cobaltcore.entity.modules;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;

public class EntityHealthModule extends EntityModule implements ISpawnExecutable, Cloneable {

    // ----- VARIABLES -----

    int maxHealth;

    // TODO: Scale Health Option

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>EntityHealthModule</code>.
     *
     * @param maxHealth the max health to give the entity.
     */
    public EntityHealthModule(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        Entity entity = customEntity.getSummonedEntity();

        // Only set health if entity is living.
        if (entity instanceof LivingEntity living) {
            AttributeInstance maxHealthAttribute = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);

            // Set the max health attribute and heal the entity to the max value.
            if (maxHealthAttribute == null) return;
            maxHealthAttribute.setBaseValue(maxHealth);
            living.setHealth(maxHealth);
        }
    }

    // ----- CLONE -----

    public EntityHealthModule(EntityHealthModule target) {
        this.maxHealth = target.maxHealth;
    }

    public EntityHealthModule clone() {
        return new EntityHealthModule(this);
    }

}
