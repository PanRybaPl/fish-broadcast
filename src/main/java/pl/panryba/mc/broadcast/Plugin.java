package pl.panryba.mc.broadcast;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Plugin extends JavaPlugin {
    
    private PluginApi api;
    private BukkitTask broadcastTask;

    private class BroadcastRunnable implements Runnable {
        private final PluginApi api;
        
        public BroadcastRunnable(PluginApi api) {
            this.api = api;
        }

        @Override
        public void run() {
            this.api.broadcast();
        }    
    }

    @Override
    public void onEnable() {                
        this.api = new PluginApi();

        FileConfiguration config = getConfig();
        api.reloadMessages(config);

        int delay = config.getInt("delay", 20);
        setupPeriodicBroadcast(delay);
        
        getCommand("brc").setExecutor(new BroadcastCommand(this));
    }

    @Override
    public void onDisable() {
        if(this.broadcastTask != null) {
            this.broadcastTask.cancel();
            this.broadcastTask = null;
        }
    }
    
    public int reloadMessages() {
        reloadConfig();
        
        FileConfiguration config = getConfig();
        return api.reloadMessages(config);
    }
    
    private void setupPeriodicBroadcast(int delay) {
        BroadcastRunnable runnable = new BroadcastRunnable(api);
        this.broadcastTask = Bukkit.getScheduler().runTaskTimer(this, runnable, 0, 20 * delay);
    }
}
