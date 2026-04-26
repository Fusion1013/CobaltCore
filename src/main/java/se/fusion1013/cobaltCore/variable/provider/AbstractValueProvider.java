package se.fusion1013.cobaltCore.variable.provider;

import com.google.gson.JsonObject;

import java.util.Random;

public abstract class AbstractValueProvider<T> implements IValueProvider<T> {

    protected final Random random = new Random();
    protected final String parameterName;

    public AbstractValueProvider(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public void load(JsonObject json) {

    }

    public abstract boolean isEmpty();
}
