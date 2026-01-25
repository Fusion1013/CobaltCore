package se.fusion1013.cobaltCore.entity.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.entity.CustomEntity;

import java.util.List;

public class EntityTargetLoader implements IEntityLoaderComponent {
    @Override
    public void load(YamlConfiguration yaml, CustomEntity.Builder builder) {
        if (yaml.contains("no_attack")) addNoAttackList(yaml.getStringList("no_attack"), builder);
    }

    private void addNoAttackList(@NotNull List<String> players, CustomEntity.Builder builder) {
        players.forEach(builder::addNoAttackPlayer);
    }

    @Override
    public void load(JsonObject json, CustomEntity.Builder builder) {

    }
}
