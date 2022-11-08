package se.fusion1013.plugin.cobaltcore.entity;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandHandler;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;
import se.fusion1013.plugin.cobaltcore.entity.modules.*;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.ChargeAbility;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.constants.EntityConstants;

import java.util.*;

public class CustomEntityManager extends Manager implements Listener {

    // ----- VARIABLES -----

    private static final List<ICustomEntity> inbuiltCustomEntities = new ArrayList<>();
    private static final Map<UUID, ICustomEntity> summonedCustomEntities = new HashMap<>(); // TODO: When it is queried, check if any of the entities need to be removed if they have died.

    // ----- REGISTERED ENTITIES -----

    public static final ICustomEntity TEST_ENTITY = register(new CustomEntity.CustomEntityBuilder("test_entity", EntityType.ZOMBIE)
            .addExecuteOnSpawnModule(new EntityHealthModule(10))
            .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE)))
            .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD)))
            .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.GLOWING, 1000000, 1, false, false)))
            .addExecuteOnSpawnModule(new EntitySpawnMethodModule(((customEntity, spawnParameters) -> {
                Entity entity = customEntity.getSummonedEntity();
                if (entity instanceof Zombie zombie) zombie.setAdult();
            })))
            .addExecuteOnTickModule(new EntityBossBarModule("Test Entity", 10, BarColor.BLUE, BarStyle.SEGMENTED_6))
            .addExecuteOnDeathModule(new EntityDropModule(100, 2, new LootPool(1, new LootEntry(new ItemStack(Material.DIAMOND), 0, 1))))
            .addAbilityModule(new ChargeAbility(8, 1, 5))
            .setCustomName("Test Entity")
            .build());

    // ----- REGISTER -----

    public static ICustomEntity register(ICustomEntity entity) {
        inbuiltCustomEntities.add(entity);
        return entity;
    }

    // ----- ENTITY MANAGING -----

    /**
     * Removes an entity from the summoned entities map.
     *
     * @param uuid the uuid of the entity to remove.
     * @return true if the entity was removed.
     */
    public static boolean removeEntity(UUID uuid) {
        ICustomEntity entity = summonedCustomEntities.remove(uuid);
        return entity != null;
    }

    /**
     * Force summons a <code>CustomEntity</code>, without checking spawning requirements.
     *
     * @param entityName the name of the entity to summon.
     * @param location the location to summon the entity.
     * @return true if the entity was summoned.
     */
    public static CustomEntity forceSummonEntity(String entityName, Location location) {
        ICustomEntity entityToSummon = getCustomEntity(entityName);
        if (entityToSummon == null) return null;

        CustomEntity summoned = entityToSummon.forceSpawn(location, null);
        if (summoned == null) return null;

        summonedCustomEntities.put(summoned.getEntityUuid(), summoned);
        return summoned;
    }

    /**
     * Attempts to summon a <code>CustomEntity</code>, checking spawning requirements.
     *
     * @param entityName the name of the entity to summon.
     * @param location the location to summon the entity.
     * @param spawnParameters the spawn parameters.
     * @return true if the entity was summoned.
     */
    public static boolean attemptSummonEntity(String entityName, Location location, ISpawnParameters spawnParameters) {
        ICustomEntity entityToSummon = getCustomEntity(entityName);
        if (entityToSummon == null) return false;

        CustomEntity summoned = entityToSummon.attemptNaturalSpawn(location, spawnParameters);
        if (summoned == null) return false;

        summonedCustomEntities.put(summoned.getEntityUuid(), summoned);
        return true;
    }

    /**
     * Gets the registered <code>CustomEntity</code> with the registered name.
     *
     * @param entityName the internal name of the entity.
     * @return the <code>CustomEntity</code>. Returns null if not found.
     */
    public static ICustomEntity getCustomEntity(String entityName) {
        for (ICustomEntity entity : inbuiltCustomEntities) if (entity.getInternalName().equalsIgnoreCase(entityName)) return entity;
        return null;
    }

    /**
     * Gets an array of <code>CustomEntity</code> names.
     *
     * @return an array of registered <code>CustomEntity</code> names.
     */
    public static String[] getInternalEntityNames() {
        String[] names = new String[inbuiltCustomEntities.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = inbuiltCustomEntities.get(i).getInternalName();
        }
        return names;
    }

    // ----- ENTITY DEATH -----

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (container.has(EntityConstants.IS_CUSTOM_ENTITY)) {
            String uuidString = container.getOrDefault(EntityConstants.IS_CUSTOM_ENTITY, PersistentDataType.STRING, "");
            UUID uuid = UUID.fromString(uuidString);

            ICustomEntity customEntity = summonedCustomEntities.get(uuid);

            if (customEntity != null) {
                se.fusion1013.plugin.cobaltcore.entity.EntityDeathEvent calledEvent = new se.fusion1013.plugin.cobaltcore.entity.EntityDeathEvent(customEntity, event.getEntity().getLocation());
                Bukkit.getPluginManager().callEvent(calledEvent);
                if (calledEvent.isCancelled()) return;
                customEntity.onDeath(event.getEntity().getLocation(), event.getEntity());
                CobaltCore.getInstance().getLogger().info("On death for entity " + customEntity.getInternalName());
            }
        }
    }

    // ----- CONSTRUCTORS -----

    public CustomEntityManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getServer().getPluginManager().registerEvents(this, CobaltCore.getInstance());
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static CustomEntityManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CustomEntityManager</code>.
     *
     * @return The object of this class
     */
    public static CustomEntityManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CustomEntityManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
