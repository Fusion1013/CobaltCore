package se.fusion1013.cobaltCore.manager.registry;

import se.fusion1013.cobaltCore.util.INameProvider;

public interface IRegistryItem extends INameProvider {

    default String[] getInfo() {
        return new String[0];
    }

}
