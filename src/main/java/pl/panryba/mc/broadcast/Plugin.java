package pl.panryba.mc.broadcast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Plugin extends JavaPlugin {
    
    private PluginApi api;
    private BukkitTask broadcastTask;
    private BukkitTask alertTask;

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

    void disableAlert() {
        if(this.alertTask == null) {
            return;
        }
        
        this.alertTask.cancel();
        this.alertTask = null;
    }

    void enableAlert(String alertMsg, int period) {
        this.disableAlert();
        
        if(period < 1)
            period = 1;
        
        Runnable sendAlert = new AlertRunnable(api, alertMsg);
        this.alertTask = Bukkit.getScheduler().runTaskTimer(this, sendAlert, 0, 20 * period);
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
    
    private class AlertRunnable implements Runnable {
        private final PluginApi api;
        private final String msg;
        
        public AlertRunnable(PluginApi api, String msg) {
            this.api = api;
            this.msg = msg;
        }

        @Override
        public void run() {
            this.api.alert(this.msg);
        }
    }

    @Override
    public void onEnable() {                
        this.api = new PluginApi();

        FileConfiguration config = getConfig();
        api.reloadMessages(config);

        int delay = config.getInt("delay", 20);
        setupPeriodicBroadcast(delay);
        
        String locale = config.getString("locale", "en");
        if(locale == null || locale.isEmpty()) {
            locale = "en";
        }
        
        YamlConfiguration defaultConfig = new YamlConfiguration();
        InputStream defaultStream = getResource("default_messages.yml");
        
        try {
            defaultConfig.load(defaultStream);
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        YamlConfiguration messagesConfig = new YamlConfiguration();
        File messagesFile = new File(getDataFolder(), "messages_" + locale + ".yml");
        
        try {
            messagesConfig.load(messagesFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.WARNING, "Could not find file: " + messagesFile);
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        LanguageStrings strings = new LanguageStrings(messagesConfig, defaultConfig);
        getCommand("brc").setExecutor(new BroadcastCommand(this, api, strings));
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
