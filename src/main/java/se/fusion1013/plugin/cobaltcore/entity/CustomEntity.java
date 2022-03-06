package se.fusion1013.plugin.cobaltcore.entity;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.IDeathExecutable;
import se.fusion1013.plugin.cobaltcore.entity.modules.ISpawnExecutable;
import se.fusion1013.plugin.cobaltcore.entity.modules.ITickExecutable;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.AbilityModule;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomEntity implements ICustomEntity, Cloneable, Runnable {

    // ----- VARIABLES -----

    // Internal // TODO: Default values.
    String internalName;
    UUID entityUuid; // Assigned after spawning the entity.
    BukkitTask task;

    // Abilities
    double generalAbilityCooldown; // The cooldown between different abilities.
    List<AbilityModule> abilityModules = new ArrayList<>();

    // Generic Modules // TODO: Add shorthand methods for certain modules so that the user does not have to figure out which execute to add it to
    List<ISpawnExecutable> executeModuleOnSpawn = new ArrayList<>();
    List<ITickExecutable> executeModuleOnTick = new ArrayList<>();
    List<IDeathExecutable> executeModuleOnDeath = new ArrayList<>();

    // Spawn Location
    private Location spawnLocation; // The location that the entity spawned at

    // Entity Information
    EntityType baseEntityType;
    Entity summonedEntity;

    // ----- CONSTRUCTORS -----

    public CustomEntity(String internalName, EntityType baseEntityType) {
        this.internalName = internalName;
        this.baseEntityType = baseEntityType;
        this.entityUuid = UUID.randomUUID();
    }

    // ----- ENTITY SPAWNING -----


    @Override
    public CustomEntity attemptNaturalSpawn(Location location) {
        return forceSpawn(location); // TODO: Add spawn requirements
    }

    /**
     * Spawns the entity at the given location.
     *
     * @param location the location to spawn the entity at.
     * @return the spawned <code>CustomEntity</code> object.
     */
    @Override
    public CustomEntity forceSpawn(Location location) {
        return this.clone().spawnWithoutClone(location);
    }

    public CustomEntity spawnWithoutClone(Location location) {

        // Store Location and Summon Entity
        this.spawnLocation = location;
        World spawnWorld = location.getWorld();
        if (spawnWorld == null) return null;
        summonedEntity = spawnWorld.spawnEntity(location, baseEntityType);

        // Execute all OnSpawn Modules
        for (ISpawnExecutable module : executeModuleOnSpawn) {
            module.execute(this);
        }

        // Start the runnable
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltCore.getInstance(), this, 1, 1);

        return this;
    }

    // ----- ENTITY TICK -----

    @Override
    public void run() {

        // If entity is not dead, execute all tick modules
        for (ITickExecutable module : executeModuleOnTick) {
            module.execute(this);
        }

        // Check if entity is dead, if so, cancel the task.
        if (!isAlive()) {
            onDeath();
            task.cancel();
        }
    }

    @Override
    public boolean isAlive() {
        return summonedEntity.isValid();
    }

    // ----- ENTITY DEATH -----

    public void onDeath() {
        // Execute all OnDeath Modules.
        for (IDeathExecutable module : executeModuleOnDeath) {
            module.execute(this);
        }

        // Remove entity from summoned entities.
        CustomEntityManager.removeEntity(entityUuid);
    }

    // ----- CLONE -----

    public CustomEntity(CustomEntity target) {

        this.internalName = target.internalName;
        this.entityUuid = target.entityUuid;
        this.task = target.task;

        this.generalAbilityCooldown = target.generalAbilityCooldown;
        this.abilityModules = target.abilityModules; // TODO: Clone the Modules

        // Clone all execute modules
        for (ISpawnExecutable ex : target.executeModuleOnSpawn) executeModuleOnSpawn.add(ex.clone());
        for (IDeathExecutable ex : target.executeModuleOnDeath) executeModuleOnDeath.add(ex.clone());
        for (ITickExecutable ex : target.executeModuleOnTick) executeModuleOnTick.add(ex.clone());

        this.spawnLocation = target.spawnLocation;
        this.baseEntityType = target.baseEntityType;
        this.summonedEntity = target.summonedEntity;
    }

    @Override
    public CustomEntity clone() {
        return new CustomEntity(this);
    }


    // ----- BUILDER -----

    protected static class CustomEntityBuilder {

        CustomEntity obj;

        // ----- VARIABLES -----

        // Abilities
        double generalAbilityCooldown;
        List<AbilityModule> abilityModules = new ArrayList<>();

        // General Modules
        List<ISpawnExecutable> executeModuleOnSpawn = new ArrayList<>();
        List<ITickExecutable> executeModuleOnTick = new ArrayList<>();
        List<IDeathExecutable> executeModuleOnDeath = new ArrayList<>();

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
            obj.setGeneralAbilityCooldown(generalAbilityCooldown);
            obj.setAbilityModules(abilityModules);

            obj.setExecuteModuleOnSpawn(executeModuleOnSpawn);
            obj.setExecuteModuleOnTick(executeModuleOnTick);
            obj.setExecuteModuleOnDeath(executeModuleOnDeath);

            return obj;
        }

        // ----- SETTERS / ADDERS -----

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

    @Override
    public String getInternalName() {
        return internalName;
    }

    public Entity getSummonedEntity() {
        return summonedEntity;
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }
}
