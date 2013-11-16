package pl.panryba.mc.broadcast;

import org.bukkit.ChatColor;

public class ColorUtils {
    public static String replaceColors(String message) {
        message = message.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
        message = message.replaceAll("(?i)&r", ChatColor.RESET.toString());

        return message;
    }
    
}
