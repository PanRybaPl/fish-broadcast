package pl.panryba.mc.broadcast;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Plugin extends JavaPlugin {
    
    private PluginApi api;
    private BukkitTask broadcastTask;

    void addMessage(String message) {
        this.api.addMessage(message);
        updateMessagesConfig();
    }

    boolean removeMessage(int index) {
        if(!this.api.removeMessage(index)) {
            return false;
        }
        
        updateMessagesConfig();
        return true;
    }

    private void updateMessagesConfig() {
        List<String> messages = this.api.getMessages();
        FileConfiguration config = getConfig();
        config.set("messages", messages);        
        this.saveConfig();
    }

    boolean editMessage(int index, String message) {
        if(!this.api.editMessage(index, message)) {
            return false;
        }
        
        updateMessagesConfig();
        return true;
    }

    void changeDelay(int newDelay) {
        FileConfiguration config = getConfig();
        config.set("delay", newDelay);
        
        this.saveConfig();
        
        stopBroadcast();
        setupPeriodicBroadcast(newDelay);
    }

    private void stopBroadcast() {
        if(this.broadcastTask != null) {
            this.broadcastTask.cancel();
            this.broadcastTask = null;
        }
    }

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
        
        getCommand("brc").setExecutor(new BroadcastCommand(this, api));
    }

    @Override
    public void onDisable() {
        stopBroadcast();
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
