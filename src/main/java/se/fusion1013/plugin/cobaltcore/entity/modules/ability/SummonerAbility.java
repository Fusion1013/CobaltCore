package se.fusion1013.plugin.cobaltcore.entity.modules.ability;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.util.GeometryUtil;

import java.util.Random;

public class SummonerAbility extends AbilityModule implements IAbilityModule {

    // ----- VARIABLES -----

    // Can summon both vanilla entities and CustomEntities // TODO: Add support for different kinds of entities (Or create an ability bundle that executes multiple abilities at the same time?)
    private EntityType entityTypeSummon;
    private ICustomEntity customEntitySummon;

    final int summonCountMin;
    final int summonCountMax;

    double spawnChance = 1;
    ISpawnParameters customSpawnParameters = null;

    // TODO: Particles

    // ----- CONSTRUCTORS -----

    public SummonerAbility(EntityType entity, int summonCountMin, int summonCountMax, double cooldown) { // TODO: Modify entity
        super(cooldown);
        this.entityTypeSummon = entity;
        this.summonCountMin = summonCountMin;
        this.summonCountMax = summonCountMax;
    }

    public SummonerAbility(ICustomEntity entity, int summonCountMin, int summonCountMax, double cooldown) {
        super(cooldown);
        this.customEntitySummon = entity;
        this.summonCountMin = summonCountMin;
        this.summonCountMax = summonCountMax;
    }

    // ----- EXECUTE METHODS -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        Random r = new Random();
        if (r.nextDouble() > spawnChance) return;

        int spawnCount = r.nextInt(summonCountMin, summonCountMax+1);

        for (int i = 0; i < spawnCount; i++) {
            // Calculate spawn location
            World world = customEntity.getSummonedEntity().getWorld();
            Vector position = GeometryUtil.getPointInUnit(GeometryUtil.Shape.SPHERE);
            Location location = new Location(world,
                    position.getX() + customEntity.getSummonedEntity().getLocation().getX(),
                    Math.max(position.getY() + customEntity.getSummonedEntity().getLocation().getY(), customEntity.getSummonedEntity().getLocation().getY()),
                    position.getZ() + customEntity.getSummonedEntity().getLocation().getZ()
            );

            // Summon Entity
            if (customEntitySummon != null) customEntitySummon.forceSpawn(location, customSpawnParameters);
            if (entityTypeSummon != null) world.spawnEntity(location, entityTypeSummon);
        }
    }

    // ----- GETTERS / SETTERS -----


    public SummonerAbility setSpawnChance(double spawnChance) {
        this.spawnChance = spawnChance;
        return this;
    }

    public SummonerAbility setCustomSpawnParameters(ISpawnParameters customSpawnParameters) {
        this.customSpawnParameters = customSpawnParameters;
        return this;
    }

    @Override
    public String getAbilityName() {
        return "Summoner";
    }

    @Override
    public String getAbilityDescription() {
        return "When activated, summons a number of the specified mobs around the entity.";
    }

    // ----- CLONE -----

    /**
     * Creates a new <code>SummonerAbility</code> with the target as a base.
     *
     * @param target the target to copy the values of.
     */
    public SummonerAbility(SummonerAbility target) {
        super(target);
        if (target.entityTypeSummon != null) this.entityTypeSummon = target.entityTypeSummon;
        if (target.customEntitySummon != null) this.customEntitySummon = target.customEntitySummon;

        this.summonCountMax = target.summonCountMax;
        this.summonCountMin = target.summonCountMin;
        this.spawnChance = target.spawnChance;
        this.customSpawnParameters = target.customSpawnParameters;
    }

    @Override
    public SummonerAbility clone() {
        return new SummonerAbility(this);
    }
}
