package se.fusion1013.cobaltCore.components;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public abstract class AbstractComponent implements IComponent {

    private String id;

    @Override
    public final void execute(Map<String, Object> context) {
        boolean canRun = validateConditions(context);
        if (!canRun) return;
        run(context);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        id = yaml.getString("id");
    }

    private boolean validateConditions(Map<String, Object> context) {
        return true;
    }

    protected abstract void run(Map<String, Object> context);

    @Override
    public String getInternalName() {
        return id;
    }
}
