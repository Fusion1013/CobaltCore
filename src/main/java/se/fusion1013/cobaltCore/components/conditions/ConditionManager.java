package se.fusion1013.cobaltCore.components.conditions;

import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FactoryRegistry;

import java.util.Map;
import java.util.function.Supplier;

public class ConditionManager extends Manager<CobaltCore> {

    public static final FactoryRegistry<ICondition> CONDITIONS = FactoryRegistry.forRegistry(
            BiomeCondition::new,
            WorldCondition::new
    );

    public ConditionManager(CobaltCore plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    public ICondition getNewCondition(String id, Map<?, ?> data) {
        ICondition condition = getNewCondition(id);
        if (condition == null) return null;
        condition.loadFromMap(data);
        return condition;
    }

    public ICondition getNewCondition(String id) {
        Supplier<ICondition> conditionFactory = CONDITIONS.get(id);
        if (conditionFactory == null) return null;
        return conditionFactory.get();
    }

    private static ConditionManager INSTANCE;

    public static ConditionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConditionManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
