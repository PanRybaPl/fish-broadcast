package pl.panryba.mc.broadcast;

import java.util.ArrayList;
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
}
