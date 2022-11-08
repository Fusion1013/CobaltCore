package se.fusion1013.plugin.cobaltcore.item.components;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.event.PlayerHeldItemTickEvent;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.item.IItemActivatorExecutor;
import se.fusion1013.plugin.cobaltcore.item.ItemActivator;

import java.util.HashMap;
import java.util.Map;

public class ActionbarComponent extends AbstractItemComponent {

    // ----- VARIABLES -----

    private Component actionBarComponent = Component.text("Component Text");
    private BukkitTask task;

    // ----- CONSTRUCTORS -----

    public ActionbarComponent(String owningItem) {
        super(owningItem);
    }

    public ActionbarComponent(String owningItem, Map<?, ?> data) {
        super(owningItem, data);

        String componentString = (String) data.get("component");
        this.actionBarComponent = Component.text(componentString);
    }

    // ----- ACTIONBAR DISPLAY -----

    private void execute(ICustomItem item, Event event, EquipmentSlot slot) {
        PlayerHeldItemTickEvent heldTickEvent = (PlayerHeldItemTickEvent) event;
        Player player = heldTickEvent.getPlayer();
        player.sendActionBar(actionBarComponent);
    }

    // ----- EVENT REGISTER -----

    @Override
    public Map<ItemActivator, IItemActivatorExecutor> registerEvents() {
        Map<ItemActivator, IItemActivatorExecutor> events = new HashMap<>();
        events.put(ItemActivator.HELD_TICK, this::execute);
        return events;
    }

    // ----- BUILDER -----

    public static class Builder extends AbstractItemComponent.Builder<ActionbarComponent, Builder> {

        // ----- VARIABLES -----

        private Component actionBarComponent = Component.text("");

        // ----- CONSTRUCTORS -----

        public Builder() {}

        // ----- CREATION METHODS -----

        @Override
        protected ActionbarComponent createObj() {
            return new ActionbarComponent(owningItem);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public ActionbarComponent build() {
            ActionbarComponent obj = super.build();
            obj.actionBarComponent = actionBarComponent;
            return obj;
        }

        // ----- BUILDER METHODS -----

        public Builder setActionbarComponent(Component component) {
            this.actionBarComponent = component;
            return getThis();
        }
    }

    // ----- VALUE LOADING -----

    @Override
    public void loadValues(Map<?, ?> values) {
        String componentString = (String) values.get("component");
        this.actionBarComponent = Component.text(componentString);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getInternalName() {
        return "actionbar_component";
    }
}
