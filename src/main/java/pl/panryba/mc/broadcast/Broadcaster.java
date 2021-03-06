package pl.panryba.mc.broadcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

class Broadcaster {

    private List<String> messages;
    private List<String> preparedMessages; // cached messages with processed tokens

    private Map<String, Object> tokens;
    private int current;
    private final BroadcastOutput output;

    private char startDelimiter;
    private char stopDelimiter;

    public Broadcaster(BroadcastOutput output) {
        this.output = output;
        this.current = -1;
        this.messages = new ArrayList<>();
        this.tokens = new HashMap<>();
        this.startDelimiter = '$';
        this.stopDelimiter = '$';

        resetPreparedMessages();
    }

    public void setDelimiters(char start, char stop) {
        this.startDelimiter = start;
        this.stopDelimiter = stop;

        resetPreparedMessages();
    }

    public void setMessages(List<String> messages) {
        if(messages == null) {
            this.messages = new ArrayList<>();
        } else {
            this.messages = new ArrayList<>(messages);
        }

        resetPreparedMessages();
    }

    public void setTokens(Map<String, Object> tokens) {
        if(tokens == null) {
            this.tokens = new HashMap<>();
        } else {
            this.tokens = tokens;
        }

        resetPreparedMessages();
    }

    public boolean removeToken(String name) {
        return this.tokens.remove(name) != null;
    }

    public void broadcast() {
        prepareMessages();

        if (++this.current >= this.preparedMessages.size()) {
            if (this.preparedMessages.isEmpty()) {
                return;
            }

            this.current = 0;
        }

        this.broadcast(this.preparedMessages.get(this.current));
    }
    
    void broadcast(String msg) {
        this.output.broadcast(formatMessage(processMessage(msg)));
    }

    private void resetPreparedMessages() {
        this.preparedMessages = null;
    }

    private void prepareMessages() {
        if (this.preparedMessages != null) {
            return;
        }

        this.preparedMessages = new ArrayList<>();

        for(String message : this.messages) {
            this.preparedMessages.add(processMessage(message));
        }
    }

    private String formatMessage(String message) {
        return ColorUtils.replaceColors(message);
    }

    private String processMessage(String message) {
        for(Map.Entry<String, Object> e : tokens.entrySet()) {
            String delimitedToken = this.startDelimiter + e.getKey() + this.stopDelimiter;
            message = message.replace(delimitedToken, e.getValue().toString());
        }

        return message;
    }

    Collection<String> getFormattedMessages() {
        List<String> result = new ArrayList<>(this.messages.size());
        
        for(String message : this.messages) {
            result.add(formatMessage(message));
        }
        
        return result;
    }

    List<String> getMessages() {
        return this.messages;
    }

    Map<String, Object> getTokens() {
        return this.tokens;
    }

    boolean addMessage(String message) {
        if(message == null) {
            return false;
        }
        
        if(message.equals("")) {
            return false;
        }
        
        boolean result = this.messages.add(message);
        resetPreparedMessages();

        return result;
    }

    void setToken(String name, Object value) {
        this.tokens.put(name, value);
        resetPreparedMessages();
    }

    boolean removeMessage(int index) {
        if(index < 0 || index >= this.messages.size()) {
            return false;
        }
        
        this.messages.remove(index);
        resetPreparedMessages();
        return true;
    }

    boolean editMessage(int index, String message) {
        if(index < 0 || index >= this.messages.size()) {
            return false;
        }
        
        this.messages.remove(index);
        this.messages.add(index, message);

        resetPreparedMessages();
        return true;
    }
}
