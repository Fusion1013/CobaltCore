package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class LocationValueProvider extends AbstractValueProvider<Location> {

    private Location value;

    public LocationValueProvider(String parameterName) {
        super(parameterName);
    }

    public LocationValueProvider(String parameterName, Location value) {
        super(parameterName);
        this.value = value;
    }

    @Override
    public Location getValue() {
        return value;
    }

    @Override
    public void setValue(Location value) {
        this.value = value;
    }

    @Override
    public void load(ConfigurationSection yaml) {

    }

    @Override
    public void load(Map<?, ?> map) {

    }
}
