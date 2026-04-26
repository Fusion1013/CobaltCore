package se.fusion1013.cobaltCore.logger;

import se.fusion1013.cobaltCore.CobaltPlugin;

import java.util.ArrayList;
import java.util.List;

public class RuleContext {

    private final List<RuleLine> rules = new ArrayList<>();
    private int currentIndentLevel = 0;

    public void logMessage(String message) {
        rules.add(new RuleLine(currentIndentLevel, "[LOG]: " + message));
    }

    public void logValueSet(String name, Object value) {
        rules.add(new RuleLine(currentIndentLevel, "[VALUE]: " + name + " set to " + value));
    }

    public void print(CobaltPlugin plugin) {
        for (RuleLine rule : rules) {
            plugin.getLogger().info(" ".repeat(rule.indent * 4) + rule.message);
        }
    }

    public void beginEvaluation(String title) {
        rules.add(new RuleLine(currentIndentLevel, "[BEGIN EVALUATION]: " + title));
        currentIndentLevel++;
    }

    public void endEvaluation(String title) {
        currentIndentLevel--;
        rules.add(new RuleLine(currentIndentLevel, "[END EVALUATION]: " + title));
    }

    private record RuleLine(int indent, String message) {
    }

}
