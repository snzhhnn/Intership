package utils;

public class ExtractUtils {
    public static String extractVersion(String shortName) {
        return shortName.substring(1, 2);
    }

    public static String extractDescription(String shortName) {
        int lastIndex = shortName.indexOf(".");
        String description = shortName.substring(4, lastIndex);
        return description.replaceAll("_", " ");
    }

    public static String extractType(String shortName) {
        int firstIndex = shortName.indexOf(".");
        return shortName.substring(firstIndex, shortName.length());
    }
}