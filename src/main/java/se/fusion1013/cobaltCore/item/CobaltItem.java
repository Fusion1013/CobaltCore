package se.fusion1013.cobaltCore.item;

import org.bukkit.Material;

public class CobaltItem extends AbstractCobaltItem {

    /**
     * Creates a new <code>CobaltItem</code>.
     *
     * @param internalName the internal name of the item.
     */
    public CobaltItem(String internalName) {
        super(internalName);
    }

    public CobaltItem(String internalName, Material material) {
        super(internalName, material);
    }

    // ----- BUILDER -----

    public static class Builder extends AbstractCobaltItem.Builder<CobaltItem, Builder> {

        public Builder(String internalName) {
            super(internalName);
        }

        @Override
        protected CobaltItem createObj() {
            return new CobaltItem(internalName);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }

}