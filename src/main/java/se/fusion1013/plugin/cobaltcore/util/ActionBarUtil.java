package se.fusion1013.plugin.cobaltcore.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.ArrayList;
import java.util.List;

public class ActionBarUtil {

    public static class ActionBarBuilder {

        // VARIABLES

        private List<ActionBarComponent> components = new ArrayList<>();
        private int globalWidth = 0;

        // CONSTRUCTORS

        public ActionBarBuilder addComponent(ActionBarComponent component) {
            this.components.add(component);
            globalWidth += component.iconWidth;
            return this;
        }

        // GETTERS / SETTERS

        public Component getComponent() {

            if (components.isEmpty()) return Component.empty();

            int currentWidth = 0;

            StringBuilder comp = new StringBuilder("[");

            for (ActionBarComponent component : components) {

                int middlePoint = (globalWidth - 1) / 2;
                int currentPos = currentWidth - middlePoint;
                int tr = middlePoint / (-2);
                int centerOffset = tr - currentPos;

                int realOffset = centerOffset + component.xOff;

                comp.append("{\"translate\":\"offset.").append(realOffset).append("\",\"with\":[{\"text\":\"").append(component.unicodeChar).append("\",\"font\":\"").append(component.font).append("\"}]},");

                currentWidth += component.iconWidth;
            }

            comp.deleteCharAt(comp.length()-1);

            comp.append("]");

            return GsonComponentSerializer.gson().deserialize(comp.toString());
        }

        public List<ActionBarComponent> getComponents() {
            return components;
        }
    }

    public static class ActionBarComponent {

        // VARIABLES

        String unicodeChar;
        int iconWidth;
        int xOff;
        String font = "minecraft:default";

        // CONSTRUCTOR

        public ActionBarComponent(String unicodeChar, int iconWidth, int xOff) {
            this.unicodeChar = unicodeChar;
            this.iconWidth = iconWidth+1; // Add 1 pixel because mojang
            this.xOff = xOff;
        }

        public ActionBarComponent(String unicodeChar, int iconWidth, int xOff, String font) {
            this.unicodeChar = unicodeChar;
            this.iconWidth = iconWidth+1; // Add 1 pixel because mojang
            this.xOff = xOff;
            this.font = font;
        }

    }
}
