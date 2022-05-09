package se.fusion1013.plugin.cobaltcore.commands.system;

import java.util.ArrayList;
import java.util.List;

public enum CommandResult {

    // ----- VALUES -----

    SUCCESS,
    CREATED,
    DELETED,
    LIST,
    INFO,
    FAILED;

    // ----- VARIABLES -----

    String description = "";

    List<String> descriptionList = new ArrayList<>();

    // ----- GETTERS / SETTERS -----

    public String getDescription() {
        return description;
    }

    public CommandResult setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getDescriptionList() {
        return descriptionList;
    }
    public CommandResult setDescriptionList(List<String> descriptionList) {
        this.descriptionList = descriptionList;
        return this;
    }

    public CommandResult addDescriptionListString(String line) {
        this.descriptionList.add(line);
        return this;
    }
}
