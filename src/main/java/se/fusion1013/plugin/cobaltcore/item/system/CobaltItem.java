package se.fusion1013.plugin.cobaltcore.item.system;

public class CobaltItem extends AbstractCobaltItem {

    /**
     * Creates a new <code>CobaltItem</code>.
     *
     * @param internalName the internal name of the item.
     */
    public CobaltItem(String internalName) {
        super(internalName);
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
