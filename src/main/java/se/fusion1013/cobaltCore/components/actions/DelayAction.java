package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Bukkit;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.ActionVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;

import java.util.List;
import java.util.Map;

public class DelayAction extends AbstractAction {

    private final IntVariable delay = new IntVariable("delay");
    private final ActionVariable actions = new ActionVariable("actions");

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(delay, actions);
    }

    @Override
    public void execute(Map<String, Object> context) {
        Bukkit.getScheduler().runTaskLater(CobaltCore.getInstance(), () -> {
            actions.getValueList().forEach(a -> a.execute(context));
        }, delay.getValue());
    }

    @Override
    public String getId() {
        return "delay";
    }
}
