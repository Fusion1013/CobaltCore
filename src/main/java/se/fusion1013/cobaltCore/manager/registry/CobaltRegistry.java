package se.fusion1013.cobaltCore.manager.registry;

import se.fusion1013.cobaltCore.util.INameProvider;

public class CobaltRegistry<T extends IRegistryItem> extends SimpleCobaltRegistry<T> {

    public String[] getNames() {
        return content.values().stream().map(INameProvider::getInternalName).toArray(String[]::new);
    }

}
