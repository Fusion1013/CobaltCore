package se.fusion1013.cobaltCore.variable.provider;

public abstract class AbstractValueProvider<T> implements IValueProvider<T> {

    protected final String parameterName;

    public AbstractValueProvider(String parameterName) {
        this.parameterName = parameterName;
    }
}
