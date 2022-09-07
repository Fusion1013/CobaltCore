package se.fusion1013.plugin.cobaltcore.world.spawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
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

    int delaySummon = 0;
    String playSound = "";
    String playSoundDelayed = "";

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

    // ----- BUILDER METHODS -----

    public CustomSpawner addDelayedSound(String sound) {
        this.playSoundDelayed = sound;
        return this;
    }

    public CustomSpawner addSound(String sound) {
        this.playSound = sound;
        return this;
    }

    public CustomSpawner addSpawnDelay(int delay) {
        this.delaySummon = delay;
        return this;
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

            if (p.getWorld() != location.getWorld()) continue;

            if (p.getLocation().distanceSquared(location) < activationRange*activationRange) {
                location.getBlock().setType(Material.AIR);
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

            if (p.getWorld() != location.getWorld()) continue;

            if (p.getLocation().distanceSquared(location) < activationRange*activationRange) {
                spawnMobs();
                return;
            }
        }
    }

    private void spawnMobs() {
        Random r = new Random();

        CobaltCore.getInstance().getLogger().info("Executing spawner");

        // Play sound
        location.getWorld().playSound(location, playSound, 10, 1);

        Bukkit.getScheduler().scheduleSyncDelayedTask(CobaltCore.getInstance(), () -> {

            // Spawn entities
            for (int i = 0; i < spawnCount; i++) {
                Vector offset;
                if (spawnRadius > 0) offset = new Vector(r.nextInt(-spawnRadius, spawnRadius) + .5, .5, r.nextInt(-spawnRadius, spawnRadius) + .5);
                else offset = new Vector();
                boolean hasSpawned = CustomEntityManager.attemptSummonEntity(entityName, location.clone().add(offset), null);

                if (hasSpawned) {
                    location.getWorld().spawnParticle(Particle.FLAME, location.clone().add(offset).add(new Vector(0, 1, 0)), 10, .3, .5, .3, 0);
                }
            }

            // Play delayed sound
            location.getWorld().playSound(location, playSoundDelayed, 10, 1);

        }, delaySummon);
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

    public int getDelaySummon() {
        return delaySummon;
    }

    public String getPlaySound() {
        return playSound;
    }

    public String getPlaySoundDelayed() {
        return playSoundDelayed;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // ----- CLONE -----

    public CustomSpawner(CustomSpawner target) {
        // General Spawner Info
        this.uuid = UUID.randomUUID();

        if (target.location != null) this.location = target.location.clone();
        this.entityName = target.entityName;
        this.entity = target.entity;
        this.type = target.type;
        this.removeNextTick = target.removeNextTick;

        this.spawnCount = target.spawnCount;
        this.activationRange = target.activationRange;
        this.spawnRadius = target.getSpawnRadius();

        this.delaySummon = target.getDelaySummon();
        this.playSound = target.getPlaySound();
        this.playSoundDelayed = target.getPlaySoundDelayed();

        // Instant Spawner

        // Continuous Spawner
        this.cooldown = target.getCooldown();
        this.currentCooldown = 0;
    }

    public CustomSpawner clone() {
        return new CustomSpawner(this);
    }

}
