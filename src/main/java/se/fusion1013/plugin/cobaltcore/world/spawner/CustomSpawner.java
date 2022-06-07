package se.fusion1013.plugin.cobaltcore.world.spawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;

import java.util.Random;
import java.util.UUID;

public class CustomSpawner {

    // ----- VARIABLES -----

    // General Spawner Info
    UUID uuid;

    Location location;
    String entityName;
    ICustomEntity entity;
    SpawnerType type;
    boolean removeNextTick = false;

    int spawnCount;
    double activationRange;
    int spawnRadius;

    // Instant Spawner

    // Continuous Spawner
    int cooldown = 0;
    int currentCooldown = 0;

    // ----- CONSTRUCTORS -----

    // Instant Spawner
    public CustomSpawner(Location location, String entity, int spawnCount, double activationRange, int spawnRadius) {
        this.uuid = UUID.randomUUID();

        this.location = location;
        this.entityName = entity;
        this.spawnCount = spawnCount;
        this.activationRange = activationRange;
        this.spawnRadius = spawnRadius;

        this.type = SpawnerType.INSTANT;
    }

    public CustomSpawner(UUID uuid, Location location, String entity, int spawnCount, double activationRange, int spawnRadius) {
        this.uuid = uuid;

        this.location = location;
        this.entityName = entity;
        this.spawnCount = spawnCount;
        this.activationRange = activationRange;
        this.spawnRadius = spawnRadius;

        this.type = SpawnerType.INSTANT;
    }

    // Continuous Spawner
    public CustomSpawner(Location location, String entity, int spawnCount, double activationRange, int spawnRadius, int cooldown) {
        this.uuid = UUID.randomUUID();

        this.location = location;
        this.entityName = entity;
        this.spawnCount = spawnCount;
        this.activationRange = activationRange;
        this.spawnRadius = spawnRadius;
        this.cooldown = cooldown;
        this.currentCooldown = cooldown;

        this.type = SpawnerType.CONTINUOUS;
    }

    public CustomSpawner(UUID uuid, Location location, String entity, int spawnCount, double activationRange, int spawnRadius, int cooldown) {
        this.uuid = uuid;

        this.location = location;
        this.entityName = entity;
        this.spawnCount = spawnCount;
        this.activationRange = activationRange;
        this.spawnRadius = spawnRadius;
        this.cooldown = cooldown;
        this.currentCooldown = cooldown;

        this.type = SpawnerType.CONTINUOUS;
    }

    // ----- SPAWNER TICK -----

    public void tick() {
        if (removeNextTick) return;

        if (entity == null) entity = CustomEntityManager.getCustomEntity(entityName);
        if (entity == null) return;

        switch (type) {
            case INSTANT -> tickInstant();
            case CONTINUOUS -> tickContinuous();
        }
    }

    private void tickInstant() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distanceSquared(location) < activationRange*activationRange) {
                spawnMobs();
                removeNextTick = true;
                return;
            }
        }
    }

    private void tickContinuous() {
        // Check Cooldown
        if (currentCooldown >= 0) {
            currentCooldown--;
            return;
        } else {
            currentCooldown = cooldown;
        }

        // Spawn Mobs
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distanceSquared(location) < activationRange*activationRange) {
                spawnMobs();
                return;
            }
        }
    }

    private void spawnMobs() {
        Random r = new Random();
        for (int i = 0; i < spawnCount; i++) {
            Vector offset;
            if (spawnRadius > 0) offset = new Vector(r.nextInt(-spawnRadius, spawnRadius), 0, r.nextInt(-spawnRadius, spawnRadius));
            else offset = new Vector();
            CustomEntity spawned = entity.attemptNaturalSpawn(location.clone().add(offset), null);

            if (spawned != null) {
                location.getWorld().spawnParticle(Particle.FLAME, location.clone().add(offset).add(new Vector(0, 1, 0)), 10, .3, .5, .3, 0);
            }
        }
    }

    // ----- GETTERS / SETTERS -----

    public UUID getUuid() {
        return uuid;
    }

    public Location getLocation() {
        return location;
    }

    public ICustomEntity getEntity() { return entity; }

    public String getEntityName() {
        return entityName;
    }

    public SpawnerType getType() {
        return type;
    }

    public int getSpawnCount() {
        return spawnCount;
    }

    public double getActivationRange() {
        return activationRange;
    }

    public int getSpawnRadius() {
        return spawnRadius;
    }

    public int getCooldown() {
        return cooldown;
    }
}
