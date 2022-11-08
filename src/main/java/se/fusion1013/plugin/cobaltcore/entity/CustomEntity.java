package se.fusion1013.plugin.cobaltcore.entity;


import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.IDeathExecutable;
import se.fusion1013.plugin.cobaltcore.entity.modules.ISpawnExecutable;
import se.fusion1013.plugin.cobaltcore.entity.modules.ITickExecutable;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.AbilityModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.IAbilityModule;
import se.fusion1013.plugin.cobaltcore.util.constants.EntityConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CustomEntity implements ICustomEntity, Cloneable, Runnable {

    // ----- VARIABLES -----

    // Internal
    String internalName;
    UUID entityUuid; // Assigned after spawning the entity.
    BukkitTask task;
    NamespacedKey key;

    // Abilities
    double generalAbilityCooldown; // The cooldown between different abilities.
    double currentAbilityCooldown = 0;
    List<AbilityModule> abilityModules = new ArrayList<>();

    // Generic Modules // TODO: Add shorthand methods for certain modules so that the user does not have to figure out which execute to add it to
    List<ISpawnExecutable> executeModuleOnSpawn = new ArrayList<>();
    List<ITickExecutable> executeModuleOnTick = new ArrayList<>();
    List<IDeathExecutable> executeModuleOnDeath = new ArrayList<>();

    // Spawn Info
    private Location spawnLocation; // The location that the entity spawned at
    private ISpawnParameters spawnParameters;
    List<IEntityModification> entityModifications = new ArrayList<>();

    // Entity Information
    EntityType baseEntityType;
    Entity summonedEntity;

    // Name
    String customName = null;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>CustomEntity</code>.
     *
     * @param internalName the internal name of the entity.
     * @param baseEntityType the <code>EntityType</code> of the <code>CustomEntity</code>.
     */
    public CustomEntity(String internalName, EntityType baseEntityType) {
        this.internalName = internalName;
        this.baseEntityType = baseEntityType;

        this.key = new NamespacedKey(CobaltCore.getInstance(), internalName);
    }

    // ----- ENTITY SPAWNING -----

    @Override
    public CustomEntity attemptNaturalSpawn(Location location, ISpawnParameters spawnParameters) {
        return forceSpawn(location, spawnParameters); // TODO: Spawn Requirements
    }

    @Override
    public CustomEntity forceSpawn(Location location, ISpawnParameters spawnParameters) {
        return this.clone().spawnWithoutClone(location, spawnParameters);
    }

    public CustomEntity spawnWithoutClone(Location location, ISpawnParameters spawnParameters) {

        this.spawnParameters = spawnParameters;

        // Store Location and Summon Entity
        this.spawnLocation = location;
        World spawnWorld = location.getWorld();
        if (spawnWorld == null) return null;
        summonedEntity = spawnWorld.spawnEntity(location, baseEntityType);

        // Modify the entity
        for (IEntityModification modification : entityModifications) modification.modifyEntity(summonedEntity);

        // Set entity tag
        this.entityUuid = UUID.randomUUID();
        summonedEntity.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);
        summonedEntity.getPersistentDataContainer().set(EntityConstants.IS_CUSTOM_ENTITY, PersistentDataType.STRING, entityUuid.toString());

        // Set entity name
        if (customName != null) {
            summonedEntity.customName(Component.text(customName));
            summonedEntity.setCustomNameVisible(true);
        }

        // Execute all OnSpawn Modules
        for (ISpawnExecutable module : executeModuleOnSpawn) {
            module.execute(this, spawnParameters);
        }

        // Start the runnable
        task = Bukkit.getScheduler().runTaskTimer(CobaltCore.getInstance(), this, 1, 1);

        return this;
    }

    // ----- ENTITY TICK -----

    @Override
    public void run() {

        Random r = new Random();

        // Execute all tick modules
        for (ITickExecutable module : executeModuleOnTick) {
            module.execute(this, spawnParameters);
        }

        // Check if entity is dead, if so, cancel the task.
        if (!isAlive()) {
            task.cancel();
        }

        // Attempt to execute abilities
        if (currentAbilityCooldown <= 0) {
            for (AbilityModule ability : abilityModules) {
                if (ability.attemptAbility(this, spawnParameters)) {
                    if (generalAbilityCooldown > 0) {
                        currentAbilityCooldown = generalAbilityCooldown + (r.nextDouble() / 2.0);
                        break;
                    }
                }
            }
        } else {
            currentAbilityCooldown -= 1.0/20.0;
        }
    }

    @Override
    public boolean isAlive() {
        return summonedEntity.isValid();
    }

    // ----- ENTITY DEATH / DESPAWN -----

    @Override
    public void despawn() {
        if (summonedEntity.isValid()) summonedEntity.remove();

        // Remove entity from summoned entities.
        CustomEntityManager.removeEntity(entityUuid);
    }

    @Override
    public void onDeath(Location location, Entity dyingEntity) {
        // Execute all OnDeath Modules.
        for (IDeathExecutable module : executeModuleOnDeath) {
            module.execute(this, spawnParameters);
        }

        // Execute all onDeath ability modules
        for (IAbilityModule module : abilityModules) {
            module.onEntityDeath(this, spawnParameters, location, dyingEntity);
        }

        // Execute everything that would happen if it were to despawn
        despawn();
    }

    // ----- CLONE -----

    public CustomEntity(CustomEntity target) {

        this.internalName = target.internalName;
        this.entityUuid = target.entityUuid;
        this.task = target.task;
        this.key = target.key;

        this.generalAbilityCooldown = target.generalAbilityCooldown;
        this.currentAbilityCooldown = target.currentAbilityCooldown;

        // Clone ability modules
        for (AbilityModule module : target.abilityModules) abilityModules.add(module.clone());

        // Clone all execute modules
        for (ISpawnExecutable ex : target.executeModuleOnSpawn) executeModuleOnSpawn.add(ex.clone());
        for (IDeathExecutable ex : target.executeModuleOnDeath) executeModuleOnDeath.add(ex.clone());
        for (ITickExecutable ex : target.executeModuleOnTick) executeModuleOnTick.add(ex.clone());

        this.spawnLocation = target.spawnLocation;
        this.baseEntityType = target.baseEntityType;
        this.summonedEntity = target.summonedEntity;

        this.customName = target.customName;

        this.spawnParameters = target.spawnParameters;

        this.entityModifications = target.entityModifications;
    }

    @Override
    public CustomEntity clone() {
        return new CustomEntity(this);
    }


    // ----- BUILDER -----

    public static class CustomEntityBuilder {

        CustomEntity obj;

        // ----- VARIABLES -----

        // Abilities
        double generalAbilityCooldown;
        List<AbilityModule> abilityModules = new ArrayList<>();

        // General Modules
        List<ISpawnExecutable> executeModuleOnSpawn = new ArrayList<>();
        List<ITickExecutable> executeModuleOnTick = new ArrayList<>();
        List<IDeathExecutable> executeModuleOnDeath = new ArrayList<>();

        // Name
        String customName = null;

        List<IEntityModification> entityModifications = new ArrayList<>();

        // ----- CONSTRUCTORS -----

        /**
         * Creates a new <code>CustomEntityBuilder</code>.
         *
         * @param internalName the internal name of the <code>CustomEntity</code>.
         * @param baseEntityType the base entity type of the <code>CustomEntity</code>.
         */
        public CustomEntityBuilder(String internalName, EntityType baseEntityType) {
            obj = new CustomEntity(internalName, baseEntityType);
        }

        // ----- INTERNAL -----

        /**
         * Builds the <code>CustomEntity</code>.
         *
         * @return the created <code>CustomEntity</code>.
         */
        public CustomEntity build() {
            obj.setCustomName(customName);
            obj.setGeneralAbilityCooldown(generalAbilityCooldown);
            obj.setAbilityModules(abilityModules);

            obj.setExecuteModuleOnSpawn(executeModuleOnSpawn);
            obj.setExecuteModuleOnTick(executeModuleOnTick);
            obj.setExecuteModuleOnDeath(executeModuleOnDeath);

            obj.setEntityModifications(entityModifications);

            return obj;
        }

        // ----- SETTERS / ADDERS -----

        /**
         * Adds a modification to an entity.
         *
         * @param modification the modification to add.
         * @return the builder.
         */
        public CustomEntityBuilder addEntityModification(IEntityModification modification) {
            this.entityModifications.add(modification);
            return this;
        }

        /**
         * Sets the custom name to display over the entity.
         *
         * @param customName the custom name.
         * @return the builder.
         */
        public CustomEntityBuilder setCustomName(String customName) {
            this.customName = customName;
            return this;
        }

        /**
         * Sets the cooldown between abilities.
         *
         * @param cooldown the cooldown between abilities.
         * @return the builder.
         */
        public CustomEntityBuilder setGeneralAbilityCooldown(double cooldown) {
            this.generalAbilityCooldown = cooldown;
            return this;
        }

        /**
         * Adds an ability module to the <code>CustomEntity</code>.
         *
         * @param module the module to add.
         * @return the builder.
         */
        public CustomEntityBuilder addAbilityModule(AbilityModule module) {
            abilityModules.add(module);
            return this;
        }

        /**
         * Adds a module that will be executed when the entity spawns.
         *
         * @param module the module to add.
         * @return the builder.
         */
        public CustomEntityBuilder addExecuteOnSpawnModule(ISpawnExecutable module) {
            executeModuleOnSpawn.add(module);
            return this;
        }

        /**
         * Adds a module that will be executed every tick.
         *
         * @param module the module to add.
         * @return the builder.
         */
        public CustomEntityBuilder addExecuteOnTickModule(ITickExecutable module) {
            executeModuleOnTick.add(module);
            return this;
        }

        /**
         * Adds a module that will be executed when the entity dies.
         *
         * @param module the module to add.
         * @return the builder.
         */
        public CustomEntityBuilder addExecuteOnDeathModule(IDeathExecutable module) {
            executeModuleOnDeath.add(module);
            return this;
        }

    }

    // ----- GETTERS / SETTERS -----

    @Override
    public Location getLocation() {
        if (isAlive()) return summonedEntity.getLocation();
        else return null;
    }

    public <T extends AbilityModule> T getAbilityModule(Class<T> abilityModuleClass) {
        for (AbilityModule module : abilityModules) {
            if (module.getClass() == abilityModuleClass) return (T)module;
        }
        return null;
    }

    public <T extends ITickExecutable> T getTickExecutable(Class<T> entityModuleClass) {
        for (ITickExecutable module : executeModuleOnTick) {
            if (module.getClass() == entityModuleClass) return (T) module;
        }
        return null;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public void setGeneralAbilityCooldown(double generalAbilityCooldown) {
        this.generalAbilityCooldown = generalAbilityCooldown;
    }

    public void setAbilityModules(List<AbilityModule> abilityModules) {
        this.abilityModules = abilityModules;
    }

    public void setExecuteModuleOnSpawn(List<ISpawnExecutable> executeModuleOnSpawn) {
        this.executeModuleOnSpawn = executeModuleOnSpawn;
    }

    public void setExecuteModuleOnTick(List<ITickExecutable> executeModuleOnTick) {
        this.executeModuleOnTick = executeModuleOnTick;
    }

    public void setExecuteModuleOnDeath(List<IDeathExecutable> executeModuleOnDeath) {
        this.executeModuleOnDeath = executeModuleOnDeath;
    }

    public void addEntityModification(IEntityModification modification) {
        this.entityModifications.add(modification);
    }

    public void setEntityModifications(List<IEntityModification> entityModifications) {
        this.entityModifications = entityModifications;
    }

    @Override
    public EntityType getBaseEntityType() {
        return baseEntityType;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    public Entity getSummonedEntity() {
        return summonedEntity;
    }

    @Override
    public UUID getEntityUuid() {
        return entityUuid;
    }

    // ----- ENTITY MODIFICATION INTERFACE -----

    public interface IEntityModification {
        void modifyEntity(Entity entity);
    }

}
