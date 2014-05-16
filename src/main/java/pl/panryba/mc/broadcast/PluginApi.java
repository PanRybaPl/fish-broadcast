/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.broadcast;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

class PluginApi {
    private final Broadcaster broadcaster;
    
    public PluginApi() {
        this.broadcaster = new Broadcaster(new BroadcastOutput() {
            @Override
            public void broadcast(String message) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(message);
                }
            }
        });
    }

    int reloadMessages(FileConfiguration config) {
        List<String> messages = (List<String>)config.getList("messages");
        Map<String, Object> tokens = readTokens(config);

        char startDelimiter = config.getString("delimiters.start", "$").charAt(0);
        char stopDelimiter = config.getString("delimiters.stop", "$").charAt(0);

        this.broadcaster.setDelimiters(startDelimiter, stopDelimiter);
        this.broadcaster.setTokens(tokens);
        this.broadcaster.setMessages(messages);
        
        if(messages == null) {
            return 0;
        }
        
        return messages.size();
    }

    private Map<String, Object> readTokens(FileConfiguration config) {
      ConfigurationSection section = (ConfigurationSection)config.get("tokens");

      if(section == null) {
        return null;
      }

      return (Map<String, Object>)section.getValues(false);
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

    Map<String, Object> getTokens() {
        return this.broadcaster.getTokens();
    }

    void addMessage(String message) {
        this.broadcaster.addMessage(message);
    }

    void setToken(String name, Object value) {
        this.broadcaster.setToken(name, value);
    }

    boolean removeToken(String name) {
        return this.broadcaster.removeToken(name);
    }

    boolean removeMessage(int index) {
        return this.broadcaster.removeMessage(index);
    }

    boolean editMessage(int index, String message) {
        return this.broadcaster.editMessage(index, message);
    }

    void alert(String msg) {
        this.broadcaster.broadcast(msg);
    }
}
