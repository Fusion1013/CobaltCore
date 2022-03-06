package se.fusion1013.plugin.cobaltcore.item;

import org.bukkit.Material;

public class CustomItem extends AbstractCustomItem implements ICustomItem {

    // ----- CONSTRUCTORS -----

    public CustomItem(String internalName) {
        super(internalName);
    }

    // ----- BUILDER -----

    public static class CustomItemBuilder extends AbstractCustomItemBuilder<CustomItem, CustomItemBuilder>{

        public CustomItemBuilder(String internalName, Material material, int count) {
            super(internalName, material, count);
        }

        @Override
        protected CustomItem createObj() {
            return new CustomItem(internalName);
        }

        @Override
        protected CustomItemBuilder getThis() {
            return this;
        }
    }
}
