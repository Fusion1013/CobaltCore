package se.fusion1013.plugin.cobaltcore.entity.modules;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;

public class EntityPotionEffectModule extends EntityModule implements ITickExecutable, ISpawnExecutable {

    // ----- VARIABLES -----

    PotionEffect effect;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>EntityPotionEffectModule</code>.
     *
     * @param effect the <code>PotionEffect</code> to give the entity.
     */
    public EntityPotionEffectModule(PotionEffect effect) {
        this.effect = effect;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity) {
        Entity entity = customEntity.getSummonedEntity();

        if (entity instanceof LivingEntity living) {
            living.addPotionEffect(effect);
        }
    }

    // ----- CLONE -----

    public EntityPotionEffectModule(EntityPotionEffectModule target) {
        this.effect = target.effect;
    }

    @Override
    public EntityPotionEffectModule clone() {
        return new EntityPotionEffectModule(this);
    }
}
