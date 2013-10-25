/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.broadcast;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

class PluginApi {
    private final Broadcaster broadcaster;
    
    public PluginApi() {
        this.broadcaster = new Broadcaster();
    }

    int reloadMessages(FileConfiguration config) {
        List<String> messages = (List<String>)config.getList("messages");
        this.broadcaster.setMessages(messages);
        
        return messages.size();
    }

    void broadcast() {
        this.broadcaster.broadcast();
    }
}
