package se.fusion1013.plugin.cobaltcore.action.system;

import java.util.Map;

public interface IActionResult {

    boolean hasActivated();

    Object getData(String key);
    Map<String, Object> getData();

}
