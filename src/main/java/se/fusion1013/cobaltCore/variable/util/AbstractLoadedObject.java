package se.fusion1013.cobaltCore.variable.util;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.variable.AbstractVariable;

import java.util.List;
import java.util.Map;

public abstract class AbstractLoadedObject {

    protected abstract List<AbstractVariable> variables();

    protected void load(ConfigurationSection yaml) {
        variables().forEach(v -> v.load(yaml));
    }

    protected void load(Map<?, ?> map) {
        variables().forEach(v -> v.load(map));
    }

    protected void load(JsonObject json) {
//        variables().forEach(v -> v.load(json));
        throw new NotImplementedException("Json loading not implemented");
    }
}
