package se.fusion1013.plugin.cobaltcore.entity.modules.ability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;

public class RageAbility extends AbilityModule {

    // ----- VARIABLES -----

    int effectDuration;

    int speedAmplifier;
    int strengthAmplifier;

    // ----- CONSTRUCTORS -----

    public RageAbility(double cooldown, int effectDuration, int speedAmplifier, int strengthAmplifier) {
        super(cooldown);
        this.effectDuration = effectDuration;
        this.speedAmplifier = speedAmplifier;
        this.strengthAmplifier = strengthAmplifier;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity) {

        Entity entity = customEntity.getSummonedEntity();
        if (entity instanceof LivingEntity living) {

            // TODO: Particles

            // Effects
            living.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, effectDuration, strengthAmplifier, true, false));
            living.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, effectDuration, speedAmplifier, true, false));

        }

    }

    /*
    @Override
    public boolean attemptAbility(CustomEntity entity) {
        execute(entity);
        return true;
    }
     */

    // ----- GETTERS / SETTERS -----

    @Override
    public String getAbilityName() {
        return "Rage";
    }

    @Override
    public String getAbilityDescription() {
        return "Increases the entity's speed and damage for the specified time.";
    }

    // ----- CLONE -----

    public RageAbility(RageAbility target) {
        super(target);
        this.strengthAmplifier = target.strengthAmplifier;
        this.speedAmplifier = target.speedAmplifier;
        this.effectDuration = target.effectDuration;
    }

    @Override
    public RageAbility clone() {
        return new RageAbility(this);
    }
}
