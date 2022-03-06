package se.fusion1013.plugin.cobaltcore.util;

/**
 * Contains various utility methods for working with strings.
 */
public class StringUtil {

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
