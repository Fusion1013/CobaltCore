package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.components.conditions.ConditionManager;
import se.fusion1013.cobaltCore.components.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConditionValueProvider extends AbstractValueProvider<ICondition> {

    private static final Random random = new Random();
    private final List<ICondition> conditions = new ArrayList<>();

    public ConditionValueProvider(String parameterName, ICondition condition) {
        super(parameterName);
        this.conditions.add(condition);
    }

    public ConditionValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public ICondition getValue() {
        return conditions.get(random.nextInt(conditions.size()));
    }

    @Override
    public void setValue(ICondition value) {
        conditions.clear();
        conditions.add(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (!yaml.contains(parameterName)) return;
        loadConditionsFromMapList(yaml.getMapList(parameterName));
    }

    @Override
    public void load(Map<?, ?> map) {
        if (!map.containsKey(parameterName)) return;
        loadConditionsFromMapList((List<Map<?, ?>>) map.get(parameterName));
    }

    private void loadConditionsFromMapList(@NotNull List<Map<?, ?>> conditionDatas) {
        for (Map<?, ?> conditionData : conditionDatas) {
            String conditionName = (String) conditionData.get("condition");
            addConditionFromMap(conditionData, conditionName);
        }
    }

    private void addConditionFromMap(Map<?, ?> conditionData, String conditionName) {
        ICondition condition = ConditionManager.getInstance().getNewCondition(conditionName, conditionData);
        if (condition == null) return;
        conditions.add(condition);
    }

    @Override
    public List<ICondition> getValueList() {
        return conditions;
    }
}
