package se.fusion1013.cobaltCore.util;

public class StringUtil {

    public static String reformatString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Split the string by underscores
        String[] words = input.split("_");

        // Build the formatted string
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                // Convert to lowercase, capitalize the first letter
                result.append(word.substring(0, 1).toUpperCase());
                result.append(word.substring(1).toLowerCase());
                result.append(" ");
            }
        }

        // Trim any trailing space
        return result.toString().trim();
    }

}
