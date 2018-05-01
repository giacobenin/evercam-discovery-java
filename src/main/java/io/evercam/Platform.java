package io.evercam;

/**
 * Created by gbeni on 5/1/2018.
 */
public class Platform {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static String getOS() {
        if (isWindows()) return "MS Windows";
        if (isMac()) return "Mac OSX";
        if (isUnix()) return "Unix";
        if(isSolaris()) return "Solaris";
        return "Unknown";
    }

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }

}
