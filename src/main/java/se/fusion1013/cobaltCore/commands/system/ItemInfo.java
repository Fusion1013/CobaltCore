package se.fusion1013.cobaltCore.commands.system;

import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

import java.util.function.Function;
import java.util.function.Supplier;

public record ItemInfo(String name, Supplier<String[]> listItems,
                       Function<String, String[]> itemInfo) implements IRegistryItem {
    @Override
    public String getInternalName() {
        return name;
    }
}
