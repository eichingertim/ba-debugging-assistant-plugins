package de.ur.mi.assistant.utils;

/**
 * Helper class to log errors while plugin is running
 */
public class InternLogger {

    public static void warn(String tag, String msg) {
        System.out.printf("%s (WARNING): %s", tag, msg);
    }

    public static void error(String tag, String msg) {
        System.out.printf("%s (ERROR): %s", tag, msg);
    }

    public static void info(String tag, String msg) {
        System.out.printf("%s (INFO): %s", tag, msg);
    }

}
