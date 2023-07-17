package se.fusion1013.plugin.cobaltcore.item.components;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import se.fusion1013.plugin.cobaltcore.action.system.ActionManager;
import se.fusion1013.plugin.cobaltcore.action.system.IAction;
import se.fusion1013.plugin.cobaltcore.action.system.ILivingEntityAction;
import se.fusion1013.plugin.cobaltcore.action.system.IPlayerAction;
import se.fusion1013.plugin.cobaltcore.event.PlayerHeldItemTickEvent;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.item.IItemActivatorExecutor;
import se.fusion1013.plugin.cobaltcore.item.ItemActivator;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;

import java.util.*;

public class ChargeComponent extends AbstractItemComponent {

    // ----- VARIABLES -----

    // -- Display Settings
    private String text = "-- < [ Charge: [VALUE]% ] > --";
    private boolean displayOnZero = true;

    // -- Charge Settings
    private int chargeTime = 20; // Measured in ticks
    private final List<IPlayerAction> chargeRequirements = new ArrayList<>();
    private boolean resetOnStopCharge = true;

    // -- Activation
    private final List<ILivingEntityAction> actions = new ArrayList<>();

    // -- Internal
    private final Map<UUID, Integer> currentPlayerCharges = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public ChargeComponent(String owningItem) {
        super(owningItem);
    }

    public ChargeComponent(String owningItem, Map<?, ?> data) {
        super(owningItem, data);

        // Display
        if (data.containsKey("display_on_zero")) displayOnZero = (boolean) data.get("display_on_zero");
        if (data.containsKey("action_bar_text")) text = (String) data.get("action_bar_text");

        // Charge Settings
        if (data.containsKey("charge_time")) this.chargeTime = (int) data.get("charge_time");
        if (data.containsKey("charge_requirements")) {
            List<IAction> actions = ActionManager.getActions((List<Map<?,?>>) data.get("charge_requirements"));

            for (IAction action : actions) {
                if (action instanceof IPlayerAction playerAction) this.chargeRequirements.add(playerAction);
            }
        }
        if (data.containsKey("reset_on_stop_charge")) this.resetOnStopCharge = (boolean) data.get("reset_on_stop_charge");

        // Activation
        if (data.containsKey("activation_actions")) {
            List<IAction> actions = ActionManager.getActions((List<Map<?,?>>) data.get("activation_actions"));

            for (IAction action : actions) {
                if (action instanceof ILivingEntityAction livingEntityAction) this.actions.add(livingEntityAction);
            }
        }
    }

    // ----- EXECUTE -----

    private boolean canCharge(Player player) {
        boolean canCharge = true;
        for (ILivingEntityAction required : chargeRequirements) {
            if (!required.activate(player).hasActivated()) canCharge = false;
        }
        return canCharge;
    }

    public void execute(ICustomItem item, Event event, EquipmentSlot slot) {
        PlayerHeldItemTickEvent heldEvent = (PlayerHeldItemTickEvent) event;
        Player p = heldEvent.getPlayer();

        int currentCharge = currentPlayerCharges.computeIfAbsent(p.getUniqueId(), k -> 0);

        // Attempt to charge
        boolean canCharge = canCharge(p);
        if (canCharge && currentCharge < chargeTime) {
            currentCharge++;
        } else if (!canCharge && currentCharge >= chargeTime) {
            for (ILivingEntityAction action : actions) {
                if (!action.activate(p).hasActivated()) break;
            }

            currentCharge = 0;
        } else if (!canCharge && resetOnStopCharge) {
            currentCharge = 0;
        }

        // Display charge // TODO: Send using action bar manager
        double percentage = (double) currentCharge / chargeTime;

        if (displayOnZero || percentage > 0) {
            String valuePrefix = "";
            if (percentage < .25) valuePrefix = "&c";
            else if (percentage < .50) valuePrefix = "&6";
            else if (percentage < .75) valuePrefix = "&e";
            else valuePrefix = "&a";

            p.sendActionBar(
                    HexUtils.colorify(
                            text.replace("[VALUE]", valuePrefix + Math.round(percentage * 1000) / 10.0)
                    )
            );
        }

        currentPlayerCharges.put(p.getUniqueId(), currentCharge);
    }

    // ----- EVENTS -----

    @Override
    public Map<ItemActivator, IItemActivatorExecutor> registerEvents() {
        Map<ItemActivator, IItemActivatorExecutor> events = new HashMap<>();

        events.put(ItemActivator.HELD_TICK, this::execute);

        return events;
    }

    // ----- VALUE LOADING -----

    @Override
    public void loadValues(Map<?, ?> values) {
        if (values.containsKey("charge_time")) this.chargeTime = (int) values.get("charge_time");
        if (values.containsKey("actions")) {
            List<IAction> actions = ActionManager.getActions((List<Map<?,?>>) values.get("actions"));

            for (IAction action : actions) {
                if (action instanceof ILivingEntityAction livingEntityAction) this.actions.add(livingEntityAction);
            }
        }

        if (values.containsKey("display_on_zero")) displayOnZero = (boolean) values.get("display_on_zero");
        if (values.containsKey("action_bar_text")) text = (String) values.get("action_bar_text");
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getInternalName() {
        return "charge_component";
    }
}
