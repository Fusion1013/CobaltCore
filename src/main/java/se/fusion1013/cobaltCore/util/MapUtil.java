package se.fusion1013.cobaltCore.util;

import java.util.Map;

public class MapUtil {

    public static boolean setBoolean(Map<?, ?> map, String key, boolean currentValue) {
        if (map.containsKey(key) && map.get(key) instanceof Boolean value) return value;
        else return currentValue;
    }

    public static double setDouble(Map<?, ?> map, String key, double currentValue) {
        if (map.containsKey(key)) {
            if (map.get(key) instanceof Double value) return value;
            else if (map.get(key) instanceof Integer value) return value;
            else return currentValue;
        } else return currentValue;
    }

    public static String setString(Map<?, ?> map, String key, String currentValue) {
        if (map.containsKey(key) && map.get(key) instanceof String value) return value;
        else return currentValue;
    }

}
