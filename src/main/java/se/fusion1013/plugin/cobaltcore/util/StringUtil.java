package se.fusion1013.plugin.cobaltcore.util;

import se.fusion1013.plugin.cobaltcore.util.animation.EasingUtil;

/**
 * Contains various utility methods for working with strings.
 */
public class StringUtil {

    // ----- ANIMATION -----

    public static String shiftingGradient(String string, String gradientCode, int period, int tick) {
        int gradientCenter = Math.round(EasingUtil.easeInOutSine(tick, 0, string.length(), period));
        String shiftedString = HexUtils.colorify(gradientCode + string.substring(gradientCenter) + string.substring(0, gradientCenter));
        int newGradientCenter = Math.round(EasingUtil.easeInOutSine(tick, 0, shiftedString.length(), period));
        return shiftedString.substring(shiftedString.length() - newGradientCenter) + shiftedString.substring(0, shiftedString.length() - newGradientCenter);
    }

    public static String revealStringMiddleOut(String string, int period, int tick) {
        int middlePoint = string.length() / 2;
        int i = (int) EasingUtil.easeInOutSine(tick, 0, middlePoint, period);
        return string.substring(middlePoint-i, middlePoint+i);
    }

    public static String revealStringRightLeft(String string, int period, int tick) {
        int i = (int) EasingUtil.easeInOutSine(tick, 0, string.length(), period);
        return string.substring(string.length() - i);
    }

    public static String revealStringLeftRight(String string, int period, int tick) {
        int i = (int) EasingUtil.easeInOutSine(tick, 0, string.length(), period);
        return string.substring(0, i);
    }

    // ----- COMPARISON -----

    /**
     * Checks whether the provided string is a word or not.
     *
     * @param toCheck the string to check.
     * @return true if the string is a word.
     */
    public static boolean isWord(String toCheck){
        return toCheck.matches("\\w+$");
    }

    /**
     * Checks if the value is equal to any of the compares.
     * @param toParse value to check.
     * @param compare values to compare with.
     * @return boolean value.
     */
    public static boolean equalsIgnoreCase(String toParse, String... compare) {
        for (String s : compare) {
            if (s.equalsIgnoreCase(toParse)) return true;
        }
        return false;
    }
}
