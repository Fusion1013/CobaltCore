package se.fusion1013.plugin.cobaltcore.locale;

public class Message {
    public String prefix = "prefix.core";
    public String key;

    public Message(String key){
        this.key = key;
    }

    public Message setPrefix(String prefix){
        this.prefix = prefix;
        return this;
    }
}
