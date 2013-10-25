package pl.panryba.mc.broadcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

class Broadcaster {

    private List<String> messages;
    private int current;

    public Broadcaster() {
        this.current = -1;
        this.messages = new ArrayList<>();
    }

    public void setMessages(List<String> messages) {
        this.messages = new ArrayList<>(messages);
    }

    public void broadcast() {
        if (++this.current >= this.messages.size()) {
            if (this.messages.isEmpty()) {
                return;
            }

            this.current = 0;
        }

        String msg = replaceColors(this.messages.get(this.current));
        Bukkit.broadcastMessage(msg);
    }

    private String replaceColors(String message) {
        message = message.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
        message = message.replaceAll("(?i)&r", ChatColor.RESET.toString());

        return message;
    }

    Collection<String> getFormattedMessages() {
        List<String> result = new ArrayList<>(this.messages.size());
        
        for(String message : this.messages) {
            result.add(replaceColors(message));
        }
        
        return result;
    }

    List<String> getMessages() {
        return this.messages;
    }

    void addMessage(String message) {
        this.messages.add(message);
    }

    void removeMessage(int index) {
        this.messages.remove(index);
    }

    void editMessage(int index, String message) {
        this.messages.remove(index);
        this.messages.add(index, message);
    }
}
