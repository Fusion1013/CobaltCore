package se.fusion1013.cobaltCore.commands.system;

import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

import java.util.function.Supplier;

public record ReloadInfo(String name, Runnable reload, Supplier<String[]> verboseResult) implements IRegistryItem {
    @Override
    public String getInternalName() {
        return name;
    }
}
