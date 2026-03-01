package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MaterialValueProvider extends AbstractValueProvider<Material> {

    private static final Random random = new Random();

    private final List<Material> materials = new ArrayList<>();

    public MaterialValueProvider(String parameterName) {
        super(parameterName);
    }

    public MaterialValueProvider(String parameterName, Material value) {
        super(parameterName);
        this.materials.add(value);
    }

    public MaterialValueProvider(String parameterName, Material[] values) {
        super(parameterName);
        this.materials.addAll(Arrays.stream(values).toList());
    }

    @Override
    public Material getValue() {
        return materials.get(random.nextInt(materials.size()));
    }

    @Override
    public void setValue(Material value) {
        materials.clear();
        materials.add(value);
    }

    public void setValues(Material[] values) {
        materials.clear();
        materials.addAll(Arrays.stream(values).toList());
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (yaml.get(parameterName) instanceof String value) {
            this.materials.clear();
            this.materials.add(EnumUtils.findEnumInsensitiveCase(Material.class, value));
        } else {
            List<String> valueHolder = yaml.getStringList(parameterName);
            this.materials.clear();
            this.materials.addAll(valueHolder.stream().map(value -> EnumUtils.findEnumInsensitiveCase(Material.class, value)).collect(Collectors.toSet()));
        }
    }

    @Override
    public void load(Map<?, ?> map) {
        if (map.containsKey(parameterName) && map.get(parameterName) instanceof String value) {
            this.materials.clear();
            this.materials.add(EnumUtils.findEnumInsensitiveCase(Material.class, value));
        } else if (map.containsKey(parameterName)) {
            List<String> values = (List<String>) map.get(parameterName);
            this.materials.clear();
            this.materials.addAll(values.stream().map(value -> EnumUtils.findEnumInsensitiveCase(Material.class, value)).collect(Collectors.toSet()));
        }
    }

    @Override
    public List<Material> getValueList() {
        return List.of();
    }

    public Material[] getMaterials() {
        return materials.toArray(new Material[0]);
    }
}
