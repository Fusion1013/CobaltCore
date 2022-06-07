package se.fusion1013.plugin.cobaltcore.database.system;

import org.mongodb.morphia.mapping.DefaultCreator;
import se.fusion1013.plugin.cobaltcore.CobaltCore;

public class HackyCreator extends DefaultCreator {
    /**
     * Returns the correct ClassLoader when called by Morphia.
     *
     * @return The ClassLoader of this plugin.
     */
    @Override
    protected ClassLoader getClassLoaderForClass() {
        return CobaltCore.getPlugin().getMongoHack();
    }

}
