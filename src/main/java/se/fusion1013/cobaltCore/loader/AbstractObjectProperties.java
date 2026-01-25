package se.fusion1013.cobaltCore.loader;

import org.json.simple.JSONObject;

public abstract class AbstractObjectProperties<T, E> {
    public abstract void create(T obj);

    public abstract E fromJson(JSONObject json);

    public abstract JSONObject toJson(E obj);
}
