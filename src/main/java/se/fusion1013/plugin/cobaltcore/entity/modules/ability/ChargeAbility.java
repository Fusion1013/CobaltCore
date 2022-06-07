package se.fusion1013.plugin.cobaltcore.entity.modules.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;

public class ChargeAbility extends AbilityModule {

    // ----- VARIABLES -----

    // Charge "charge" // TODO: switch to using abstract class for cooldown
    final double chargeCharge; // The time it takes to charge up the charge ability
    double currentCharge = 0;

    // Charge Stats
    final double chargeDistance;
    double currentChargeDistance = 0;

    // Target
    Player target = null;
    Vector targetDirection = null;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>ChargeAbility</code>.
     *
     * @param chargeCooldown the cooldown before the ability can be used again.
     * @param chargeCharge the time it takes to charge up the charge.
     */
    public ChargeAbility(double chargeCooldown, double chargeCharge, double chargeDistance) {
        super(chargeCooldown);
        this.chargeCharge = chargeCharge;
        this.chargeDistance = chargeDistance;
    }

    // ----- EXECUTE METHODS -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {

        Entity entity = customEntity.getSummonedEntity();
        if (entity == null) return;
        Location entityLocation = entity.getLocation();

        if (target == null) {
            target = PlayerUtil.getClosestPlayer(entityLocation);
            targetDirection = VectorUtil.getDirection(entityLocation.toVector(), target.getLocation().toVector()); // Get the direction towards the target.
        }
        if (target == null || targetDirection == null) return;

        // If the current charge is less than the charge time, increase the current charge and display particles. // TODO: Custom particle effect.
        if (currentCharge < chargeCharge) {
            currentCharge += 1.0/20.0;

            // TODO: Display particles in the charge path
            entityLocation.getWorld().spawnParticle(Particle.CRIT, entityLocation, 10, 1, 1, 1, 0);
            if (entity instanceof LivingEntity living) living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 6));

            currentChargeDistance = chargeDistance;

            // Reset cooldown // TODO: Replace with bukkit task
            resetCooldown();

        } else { // Otherwise, charge in the direction

            if (currentChargeDistance > 0) {
                currentChargeDistance--;

                // Move entity
                entity.teleport(entityLocation.clone().add(targetDirection));

                // Damage players
                Player[] players = PlayerUtil.getNearbyPlayers(entityLocation, 2);
                for (Player p : players) p.damage(10); // TODO: Editable value

                // Reset Cooldown // TODO: Replace with bukkit task
                resetCooldown();

            } else {
                entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);

                // Reset the target and target direction to null
                target = null;
                targetDirection = null;

                // Reset charge
                currentCharge = 0;
            }

            // entity.setVelocity(targetDirection.multiply(chargeDistance));

        }

    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getAbilityName() {
        return "Charge";
    }

    @Override
    public String getAbilityDescription() {
        return "When the Charge ability is activated, the entity will lunge in a direction and damage anything in its path.";
    }

    // ----- CLONE -----

    public ChargeAbility(ChargeAbility target) {
        super(target);
        this.chargeCharge = target.chargeCharge;
        this.currentCharge = target.currentCharge;
        this.chargeDistance = target.chargeDistance;
        this.target = target.target;
        this.targetDirection = target.targetDirection;
        this.currentChargeDistance = target.currentChargeDistance;
    }

    @Override
    public ChargeAbility clone() {
        return new ChargeAbility(this);
    }
}
