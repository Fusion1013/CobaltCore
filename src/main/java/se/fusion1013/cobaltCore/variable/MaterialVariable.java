package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.Material;
import se.fusion1013.cobaltCore.variable.provider.MaterialValueProvider;

public class MaterialVariable extends AbstractVariable<Material, MaterialValueProvider, Argument<Material>, MaterialVariable> {

    public MaterialVariable(String name) {
        super(name, new MaterialValueProvider(name, Material.BEDROCK));
    }

    public MaterialVariable(String name, Material defaultValue) {
        super(name, new MaterialValueProvider(name, defaultValue));
    }

    public MaterialVariable(String name, Material[] defaultValues) {
        super(name, new MaterialValueProvider(name, defaultValues));
    }

    @Override
    public MaterialVariable getSelf() {
        return this;
    }

    @Override
    public Argument<Material> getArgument() {
        return null;
    }

    public Material[] getMaterials() {
        return valueGetter.getMaterials();
    }

    public void setValues(Material[] materials) {
        valueGetter.setValues(materials);
    }
}
