package se.fusion1013.plugin.cobaltcore.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringPlaceholders {

    private Map<String, String> placeholders;

    public StringPlaceholders(){
        this.placeholders = new HashMap<>();
    }

    public void addPlaceholder(String placeholder, Map<TimeUnit, Long> time) {
        this.placeholders.put(placeholder, objectToString(time));
    }

    public void addPlaceholder(String placeholder, Object value){
        this.placeholders.put(placeholder, objectToString(value));
    }

    public String apply(String string){
        for (String key : this.placeholders.keySet()){
            string = string.replaceAll(Pattern.quote('%' + key + '%'), Matcher.quoteReplacement(this.placeholders.get(key)));
        }
        return string;
    }

    public Map<String, String> getPlaceholders(){
        return Collections.unmodifiableMap(this.placeholders);
    }

    public static Builder builder(){
        return new Builder();
    }

    public static Builder builder(String placeholder, Object value){
        return new Builder(placeholder, objectToString(value));
    }

    public static StringPlaceholders empty(){
        return builder().build();
    }

    public static StringPlaceholders single(String placeholder, Object value){
        return builder(placeholder, value).build();
    }

    private static String objectToString(Map<TimeUnit, Long> diff) { // TODO: Redo this method
        if (diff == null) return "null";
        String string = "";
        if (diff.get(TimeUnit.DAYS) != 0) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("value", diff.get(TimeUnit.DAYS))
                    .addPlaceholder("format", "milliseconds").build();
            string += LocaleManager.getInstance().getLocaleMessage("timestamp", placeholders) + ", ";
        }
        if (diff.get(TimeUnit.HOURS) != 0) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("value", diff.get(TimeUnit.HOURS))
                    .addPlaceholder("format", "milliseconds").build();
            string += LocaleManager.getInstance().getLocaleMessage("timestamp", placeholders) + ", ";
        }
        if (diff.get(TimeUnit.MINUTES) != 0) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("value", diff.get(TimeUnit.MINUTES))
                    .addPlaceholder("format", "milliseconds").build();
            string += LocaleManager.getInstance().getLocaleMessage("timestamp", placeholders) + ", ";
        }
        if (diff.get(TimeUnit.SECONDS) != 0) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("value", diff.get(TimeUnit.SECONDS))
                    .addPlaceholder("format", "milliseconds").build();
            string += LocaleManager.getInstance().getLocaleMessage("timestamp", placeholders) + ", ";
        }
        if (diff.get(TimeUnit.MILLISECONDS) != 0) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("value", diff.get(TimeUnit.MILLISECONDS))
                    .addPlaceholder("format", "milliseconds").build();
            string += LocaleManager.getInstance().getLocaleMessage("timestamp", placeholders) + ", ";
        }

        return string.substring(0, Math.max(0, string.length() - 2));
    }

    private static String objectToString(Object object){
        if (object == null) return "null";
        if (object instanceof Location location) { // --- LOCATION
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("x", Math.round(location.getX() * 10) / 10.0)
                    .addPlaceholder("y", Math.round(location.getY() * 10) / 10.0)
                    .addPlaceholder("z", Math.round(location.getZ() * 10) / 10.0)
                    .build();
            return LocaleManager.getInstance().getLocaleMessage("location", placeholders);
        } else if (object instanceof Vector vector) { // --- VECTOR
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("x", Math.round(vector.getX() * 10) / 10.0)
                    .addPlaceholder("y", Math.round(vector.getY() * 10) / 10.0)
                    .addPlaceholder("z", Math.round(vector.getZ() * 10) / 10.0)
                    .build();
            return LocaleManager.getInstance().getLocaleMessage("vector", placeholders);
        }

        return object.toString();
    }

    public static class Builder{

        private StringPlaceholders stringPlaceholders;

        private Builder(){
            this.stringPlaceholders = new StringPlaceholders();
        }

        private Builder(String placeholder, Object value){
            this();
            this.stringPlaceholders.addPlaceholder(placeholder, objectToString(value));
        }

        public Builder addPlaceholder(String placeholder, Object value){
            this.stringPlaceholders.addPlaceholder(placeholder, objectToString(value));
            return this;
        }

        public Builder addPlaceholder(String placeholder, Map<TimeUnit, Long> time) {
            this.stringPlaceholders.addPlaceholder(placeholder, time);
            return this;
        }

        public String apply(String string){
            return this.stringPlaceholders.apply(string);
        }

        public StringPlaceholders build(){
            return this.stringPlaceholders;
        }
    }
}
