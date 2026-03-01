package se.fusion1013.cobaltCore.variable.provider;

import com.google.gson.JsonObject;

public abstract class AbstractValueProvider<T> implements IValueProvider<T> {

    protected final String parameterName;

    public AbstractValueProvider(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public void load(JsonObject json) {
        
    }
}
