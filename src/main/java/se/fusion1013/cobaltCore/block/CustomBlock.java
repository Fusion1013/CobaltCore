package se.fusion1013.cobaltCore.block;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.variable.LiteralVariable;
import se.fusion1013.cobaltCore.variable.MaterialVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

public class CustomBlock implements ICustomBlock {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final MaterialVariable material = new MaterialVariable("material");
    private final StringVariable dropItem = new StringVariable("drop_item");
    private final LiteralVariable behaviour = new LiteralVariable("behaviour", new String[]{"break", "regrow"});
    // TODO: Conditions

    public CustomBlock(ConfigurationSection yaml) {
        internalName.load(yaml);
        material.load(yaml);
        dropItem.load(yaml);
        behaviour.load(yaml);
    }

    @Override
    public String getInternalName() {
        return internalName.getName();
    }
}
