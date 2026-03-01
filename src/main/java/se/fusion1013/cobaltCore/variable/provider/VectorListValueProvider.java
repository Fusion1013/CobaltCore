package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.util.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VectorListValueProvider extends AbstractValueProvider<List<Vector>> {

    private final List<Vector> values = new ArrayList<>();

    public VectorListValueProvider(String parameterName, List<Vector> value) {
        super(parameterName);
        values.addAll(value);
    }

    public VectorListValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public List<Vector> getValue() {
        return values;
    }

    @Override
    public void setValue(List<Vector> value) {
        values.clear();
        values.addAll(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (!yaml.contains(parameterName)) return;

        List<Map<?, ?>> mapList = yaml.getMapList(parameterName);
        for (Map<?, ?> map : mapList) {
            values.add(new Vector(
                    MapUtil.setDouble(map, "x", 0),
                    MapUtil.setDouble(map, "y", 0),
                    MapUtil.setDouble(map, "z", 0))
            );
        }
    }

    @Override
    public void load(Map<?, ?> map) {

    }

    @Override
    public List<List<Vector>> getValueList() {
        return List.of();
    }
}
