package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.List;
import java.util.Map;

public abstract class AbstractAction implements IAction {

    private final StringVariable targetLocation = new StringVariable("target_location", "default_location");
    private final StringVariable targetEntity = new StringVariable("target_entity", "default_entity");

    protected Location getTargetLocation(Map<?, ?> context) {
        return (Location) context.get(targetLocation.getValue());
    }

    protected LivingEntity getTargetLivingEntity(Map<?, ?> context) {
        return (LivingEntity) context.get(targetEntity.getValue());
    }

    @Override
    public void loadFromMap(Map<?, ?> map) {
        getVariables().forEach(k -> k.load(map));
        targetLocation.load(map);
        targetEntity.load(map);
        if (map.containsKey("target")) {
            String target = (String) map.get("target");
            targetLocation.setValue(target);
            targetEntity.setValue(target);
        }
    }

    @Override
    public void loadFromYaml(ConfigurationSection yaml) {
        getVariables().forEach(k -> k.load(yaml));
        targetLocation.load(yaml);
        targetEntity.load(yaml);
        if (yaml.contains("target")) {
            String target = yaml.getString("target");
            targetLocation.setValue(target);
            targetEntity.setValue(target);
        }
    }

    protected abstract List<AbstractVariable<?, ?, ?, ?>> getVariables();
}
