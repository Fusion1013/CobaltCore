package se.fusion1013.cobaltCore.entity.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import se.fusion1013.cobaltCore.entity.CustomEntity;
import se.fusion1013.cobaltCore.entity.ICustomEntity;
import se.fusion1013.cobaltCore.loader.AbstractFileLoader;
import se.fusion1013.cobaltCore.loader.IFileLoaderComponent;

public class EntityLoader extends AbstractFileLoader<ICustomEntity, CustomEntity.Builder> {

    private static final IEntityLoaderComponent[] LOADERS = new IEntityLoaderComponent[]{

    };

    @Override
    public ICustomEntity load(YamlConfiguration yaml) {
        String internalName = yaml.getString("internal_name");
        String entityType = yaml.getString("entity_type");
        if (internalName == null || entityType == null) return null;

        var builder = new CustomEntity.Builder(internalName, EntityType.valueOf(entityType));
        for (IEntityLoaderComponent loader : LOADERS) loader.load(yaml, builder);
        return builder.build();
    }

    @Override
    public ICustomEntity load(JsonObject json) {
        return null;
    }

    @Override
    protected IFileLoaderComponent<CustomEntity.Builder>[] getLoaders() {
        return LOADERS;
    }

}
