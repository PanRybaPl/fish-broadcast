/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.broadcast;

import org.bukkit.ChatColor;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Marcin
 */
public class ColorUtilsTest {
    
    @Test
    public void testReplaceBukkitColors() {
        assertEquals(ChatColor.YELLOW + "test", ColorUtils.replaceColors(ChatColor.YELLOW + "test"));
    }
    
    @Test
    public void testReplaceFormattingCodes() {
        assertEquals("§1Test", ColorUtils.replaceColors("&1Test"));
    }
    
    @Test
    public void testReplaceWithoutFormatting() {
        assertEquals("test", ColorUtils.replaceColors("test"));
    }
}