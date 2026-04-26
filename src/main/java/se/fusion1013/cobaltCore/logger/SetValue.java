package se.fusion1013.cobaltCore.logger;

public class SetValue<T> {

    private final String parameterName;
    private T value;
    private final RuleLogger ruleLogger;

    public SetValue(String parameterName, RuleLogger ruleLogger) {
        this.parameterName = parameterName;
        this.ruleLogger = ruleLogger;
    }

    public SetValue(String parameterName, T initialValue, RuleLogger ruleLogger) {
        this.parameterName = parameterName;
        value = initialValue;
        this.ruleLogger = ruleLogger;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.ruleLogger.logValueSet(parameterName, value);
        this.value = value;
    }
}
