package pl.panryba.mc.broadcast;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;
import static org.junit.Assert.*;

public class BukkitTest {
    @Test
    public void testGetDefaultConfig() {
        FileConfiguration config = new YamlConfiguration();
        String strValue = config.getString("non_existing", "default");
        
        assertEquals("default", strValue);
        
        int intValue = config.getInt("non_existing", 123);
        
        assertEquals(123, intValue);
    }
}
