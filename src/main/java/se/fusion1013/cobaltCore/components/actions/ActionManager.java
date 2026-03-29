package se.fusion1013.cobaltCore.components.actions;

import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.manager.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ActionManager extends Manager<CobaltCore> {

    private static final Map<String, Supplier<IAction>> ACTION_FACTORIES = new HashMap<>();

    private static final Supplier<IAction> PARTICLE_ACTION_FACTORY = register("particle", ParticleAction::new);
    private static final Supplier<IAction> SOUND_ACTION_FACTORY = register("sound", SoundAction::new);
    private static final Supplier<IAction> POTION_EFFECT_ACTION_FACTORY = register("effect", PotionEffectAction::new);
    private static final Supplier<IAction> VELOCITY_ACTION_FACTORY = register("velocity", VelocityAction::new);
    private static final Supplier<IAction> DAMAGE_ACTION_FACTORY = register("damage", DamageAction::new);
    private static final Supplier<IAction> LOCATION_ACTION_FACTORY = register("location", LocationAction::new);
    private static final Supplier<IAction> SUMMON_ACTION_FACTORY = register("summon", SummonAction::new);
    private static final Supplier<IAction> DELAY_ACTION_FACTORY = register("delay", DelayAction::new);
    private static final Supplier<IAction> SET_BLOCK_ACTION_FACTORY = register("set_block", SetBlockAction::new);
    private static final Supplier<IAction> NEARBY_ENTITIES_ACTION_FACTORY = register("nearby_entities", NearbyEntitiesAction::new);

    public ActionManager(CobaltCore plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    public static Supplier<IAction> register(String id, Supplier<IAction> action) {
        ACTION_FACTORIES.put(id, action);
        return action;
    }

    public IAction getNewAction(String id, Map<?, ?> data) {
        IAction action = getNewAction(id);
        if (action == null) return null;
        action.loadFromMap(data);
        return action;
    }

    public IAction getNewAction(String id) {
        Supplier<IAction> actionFactory = ACTION_FACTORIES.get(id);
        if (actionFactory == null) return null;
        return actionFactory.get();
    }

    private static ActionManager INSTANCE;

    public static ActionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
