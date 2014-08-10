package pl.panryba.mc.broadcast;

import org.bukkit.ChatColor;

public class ColorUtils {
    public static String replaceColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
}
