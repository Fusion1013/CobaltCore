package se.fusion1013.plugin.cobaltcore.entity.modules;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.advancement.CobaltAdvancementManager;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;

import java.util.ArrayList;
import java.util.List;

public class EntityHealthModule extends EntityModule implements ISpawnExecutable, Cloneable {

    // ----- VARIABLES -----

    int maxHealth;

    float boostFactor = 1;
    // TODO: Scale Health Option

    boolean scaleHealth = false;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>EntityHealthModule</code>.
     *
     * @param maxHealth the max health to give the entity.
     */
    public EntityHealthModule(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    // ----- BUILDER -----

    public EntityHealthModule scaleHealth() {
        this.scaleHealth = true;
        return this;
    }

    public EntityHealthModule setBoostFactor(float boostFactor) {
        this.boostFactor = boostFactor;
        return this;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        // Calculate the actual health value
        float actualHealth = maxHealth;

        if (scaleHealth) actualHealth = calculateScaledHealth();


        Entity entity = customEntity.getSummonedEntity();

        // Only set health if entity is living.
        if (entity instanceof LivingEntity living) {
            AttributeInstance maxHealthAttribute = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);

            // Set the max health attribute and heal the entity to the max value.
            if (maxHealthAttribute == null) return;
            maxHealthAttribute.setBaseValue(actualHealth);
            living.setHealth(actualHealth);

            CobaltCore.getInstance().getLogger().info("Set health for entity " + customEntity.getInternalName() + " to " + actualHealth);
        }
    }

    // ----- HEALTH SCALING -----

    private float calculateScaledHealth() {
        List<Player> players = getParticipatingPlayers();

        float multiplayerFactor = 1;
        float[] healthAdded = new float[players.size()+1];
        healthAdded[0] = .35f;

        for (int i = 0; i < players.size()-1; i++) {
            if (i > 9) {
                multiplayerFactor = (multiplayerFactor * 2 + 8) / 3;
            } else {
                multiplayerFactor = multiplayerFactor + healthAdded[i];
                healthAdded[i+1] = calculateNextHealthAdded(healthAdded[i]);
            }
        }

        return maxHealth * boostFactor * multiplayerFactor;
    }

    private float calculateNextHealthAdded(float previous) {
        return previous + (1 - previous) / 3;
    }

    private List<Player> getParticipatingPlayers() {
        List<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) players.add(player);
        }
        return players;
    }

    // ----- CLONE -----

    public EntityHealthModule(EntityHealthModule target) {
        this.maxHealth = target.maxHealth;
        this.scaleHealth = target.scaleHealth;
        this.boostFactor = target.boostFactor;
    }

    public EntityHealthModule clone() {
        return new EntityHealthModule(this);
    }

}
