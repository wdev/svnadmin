package util;

public class FormatterUtil {

    public static String formatName(String name) {
        return name.replace("-", "_").toLowerCase().trim();
    }
}
