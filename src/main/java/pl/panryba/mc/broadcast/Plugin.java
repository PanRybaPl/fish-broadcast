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
import org.mcstats.Metrics;

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
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.api = new PluginApi();

        FileConfiguration config = getConfig();
        api.reloadMessages(config);

        int delay = config.getInt("delay", 20);
        setupPeriodicBroadcast(delay);
        
        String locale = config.getString("locale", "en");
        
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
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
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
