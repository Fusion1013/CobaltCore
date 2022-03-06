package se.fusion1013.plugin.cobaltcore.util;

import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringPlaceholders {

    private Map<String, String> placeholders;

    private static final Map<String, String> variableDefaultStyles = new LinkedHashMap<String, String>(){
        {
            put("location", "&b%x%&7, &b%y%&7, &b%z%&7");
            put("time", "&b%days%&7 days, &b%hours%&7 hours, &b%minutes%&7 minutes, &b%seconds%&7 seconds, &b%milliseconds%&7 milliseconds");
            put("timestamp", "&b%value%&7 %format%");
        }};

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
        if (diff.get(TimeUnit.DAYS) != 0) string += StringPlaceholders.builder()
                .addPlaceholder("value", diff.get(TimeUnit.DAYS))
                .addPlaceholder("format", "days").build()
                .apply(variableDefaultStyles.get("timestamp")) + ", ";
        if (diff.get(TimeUnit.HOURS) != 0) string += StringPlaceholders.builder()
                .addPlaceholder("value", diff.get(TimeUnit.HOURS))
                .addPlaceholder("format", "hours").build()
                .apply(variableDefaultStyles.get("timestamp")) + ", ";
        if (diff.get(TimeUnit.MINUTES) != 0) string += StringPlaceholders.builder()
                .addPlaceholder("value", diff.get(TimeUnit.MINUTES))
                .addPlaceholder("format", "minutes").build()
                .apply(variableDefaultStyles.get("timestamp")) + ", ";
        if (diff.get(TimeUnit.SECONDS) != 0) string += StringPlaceholders.builder()
                .addPlaceholder("value", diff.get(TimeUnit.SECONDS))
                .addPlaceholder("format", "seconds").build()
                .apply(variableDefaultStyles.get("timestamp")) + ", ";
        if (diff.get(TimeUnit.MILLISECONDS) != 0) string += StringPlaceholders.builder()
                .addPlaceholder("value", diff.get(TimeUnit.MILLISECONDS))
                .addPlaceholder("format", "milliseconds").build()
                .apply(variableDefaultStyles.get("timestamp")) + ", ";

        return string.substring(0, Math.max(0, string.length() - 2));
    }

    private static String objectToString(Object object){
        if (object == null) return "null";
        if (object instanceof Location location) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("x", Math.round(location.getX() * 10) / 10.0)
                    .addPlaceholder("y", Math.round(location.getY() * 10) / 10.0)
                    .addPlaceholder("z", Math.round(location.getZ() * 10) / 10.0)
                    .build();
            return placeholders.apply(variableDefaultStyles.get("location"));
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
