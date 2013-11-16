/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.broadcast;

import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

class PluginApi {
    private final Broadcaster broadcaster;
    
    public PluginApi() {
        this.broadcaster = new Broadcaster(new BroadcastOutput() {
            @Override
            public void broadcast(String message) {
                Bukkit.broadcastMessage(message);
            }
        });
    }

    int reloadMessages(FileConfiguration config) {
        List<String> messages = (List<String>)config.getList("messages");
        this.broadcaster.setMessages(messages);
        
        if(messages == null) {
            return 0;
        }
        
        return messages.size();
    }

    void broadcast() {
        this.broadcaster.broadcast();
    }

    Collection<String> getFormattedMessages() {
        return this.broadcaster.getFormattedMessages();
    }

    List<String> getMessages() {
        return this.broadcaster.getMessages();
    }

    void addMessage(String message) {
        this.broadcaster.addMessage(message);
    }

    boolean removeMessage(int index) {
        return this.broadcaster.removeMessage(index);
    }

    boolean editMessage(int index, String message) {
        return this.broadcaster.editMessage(index, message);
    }
}
