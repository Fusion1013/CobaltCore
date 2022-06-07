package se.fusion1013.plugin.cobaltcore.world.structure.modules;

public abstract class StructureModule implements IStructureModule {

    // ----- BUILDER -----

    protected static abstract class StructureModuleBuilder<T extends StructureModule, B extends StructureModuleBuilder> {

        // ----- VARIABLES -----

        protected T obj;

        // ----- CONSTRUCTOR -----

        public StructureModuleBuilder() {}

        // ----- BUILDING -----

        public T build() {
            obj = createObj();
            return obj;
        }

        // ----- HELPER METHODS -----

        protected abstract T createObj();
        protected abstract B getThis();

    }

}
