package utils;

import exception.InvalidClassNameException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        return shortName.substring(firstIndex);
    }

    public static String extractPrefix(String script) {
        if (script.startsWith("V")) {
            return script.substring(0, 1);
        } else {
            throw new InvalidClassNameException();
        }
    }

    public static String extractSQL(File file) {
        try {
            return Files.readString(Paths.get(file.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}