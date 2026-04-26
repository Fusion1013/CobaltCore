package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class VectorValueProvider extends AbstractValueProvider<Vector> {

    private static final Random random = new Random();

    private Vector min;
    private Vector max;

    public VectorValueProvider(String parameterName, Vector value) {
        super(parameterName);
        this.min = value;
        this.max = value;
    }

    public VectorValueProvider(String parameterName) {
        super(parameterName);
        min = new Vector(0, 0, 0);
        max = new Vector(0, 0, 0);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Vector getValue() {

        double x = min.getX() == max.getX() ? min.getX() : random.nextDouble(min.getX(), max.getX() + 1);
        double y = min.getY() == max.getY() ? min.getY() : random.nextDouble(min.getY(), max.getY() + 1);
        double z = min.getZ() == max.getZ() ? min.getZ() : random.nextDouble(min.getZ(), max.getZ() + 1);

        return new Vector(x, y, z);
    }

    @Override
    public void setValue(Vector value) {
        this.min = value;
        this.max = value;
    }

    @Override
    public void load(ConfigurationSection yaml) {

    }

    @Override
    public void load(Map<?, ?> map) {
        if (map.containsKey(parameterName)) {
            Map<?, ?> valueMap = (Map<?, ?>) map.get(parameterName);
            if (valueMap.containsKey("x") && valueMap.get("x") instanceof Double value) {
                this.min.setX(value);
                this.max.setX(value);
            } else {
                Map<?, ?> minMax = (Map<?, ?>) valueMap.get("x");
                this.min.setX((double) minMax.get("min"));
                this.max.setX((double) minMax.get("max"));
            }

            if (valueMap.containsKey("y") && valueMap.get("y") instanceof Double value) {
                this.min.setY(value);
                this.max.setY(value);
            } else {
                Map<?, ?> minMax = (Map<?, ?>) valueMap.get("y");
                this.min.setY((double) minMax.get("min"));
                this.max.setY((double) minMax.get("max"));
            }

            if (valueMap.containsKey("z") && valueMap.get("z") instanceof Double value) {
                this.min.setZ(value);
                this.max.setZ(value);
            } else {
                Map<?, ?> minMax = (Map<?, ?>) valueMap.get("z");
                this.min.setZ((double) minMax.get("min"));
                this.max.setZ((double) minMax.get("max"));
            }
        }
    }

    @Override
    public List<Vector> getValueList() {
        return List.of();
    }
}
