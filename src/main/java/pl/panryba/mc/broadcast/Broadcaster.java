package pl.panryba.mc.broadcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.ChatColor;

class Broadcaster {

    private List<String> messages;
    private int current;
    private final BroadcastOutput output;

    public Broadcaster(BroadcastOutput output) {
        this.output = output;
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
        this.output.broadcast(msg);
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

    boolean addMessage(String message) {
        if(message == null) {
            return false;
        }
        
        if(message.equals("")) {
            return false;
        }
        
        return this.messages.add(message);
    }

    boolean removeMessage(int index) {
        if(index < 0 || index >= this.messages.size()) {
            return false;
        }
        
        this.messages.remove(index);
        return true;
    }

    boolean editMessage(int index, String message) {
        if(index < 0 || index >= this.messages.size()) {
            return false;
        }
        
        this.messages.remove(index);
        this.messages.add(index, message);
        
        return true;
    }
}
