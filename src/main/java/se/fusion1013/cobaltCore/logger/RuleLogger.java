package se.fusion1013.cobaltCore.logger;

import se.fusion1013.cobaltCore.CobaltPlugin;

import java.util.function.Supplier;

public class RuleLogger {

    private final RuleContext context;

    private RuleLogger(RuleContext context) {
        this.context = context;
    }

    public static RuleLogger create(String title) {
        RuleContext context = new RuleContext();
        return new RuleLogger(context);
    }

    // ##%%##%%## PRINTING ##%%##%%## //

    public void print(CobaltPlugin plugin) {
        context.print(plugin);
    }

    // ##%%##%%## LOGGING ##%%##%%## //

    public void logMessage(String message) {
        context.logMessage(message);
    }

    public void logValueSet(String name, Object value) {
        context.logValueSet(name, value);
    }

    // ##%%##%%## EVALUATE ##%%##%%## //

    public void evaluate(String title, Runnable methodCall) {
        beginEvaluation(title);
        methodCall.run();
        endEvaluation(title);
    }

    public <T> T evaluate(String title, Supplier<T> method) {
        beginEvaluation(title);
        T result = method.get();
        endEvaluation(title);
        return result;
    }

    private void beginEvaluation(String title) {
        context.beginEvaluation(title);
    }

    private void endEvaluation(String title) {
        context.endEvaluation(title);
    }

}
