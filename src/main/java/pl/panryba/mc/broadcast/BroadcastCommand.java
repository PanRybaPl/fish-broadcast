package pl.panryba.mc.broadcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

class BroadcastCommand implements CommandExecutor {

    private final Plugin plugin;
    private final PluginApi api;

    public BroadcastCommand(Plugin plugin, PluginApi api) {
        this.api = api;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (strings.length == 0) {
            return false;
        }

        String subCmnd = strings[0];
        switch (subCmnd) {
            case "edit":
                if (strings.length < 3) {
                    return false;
                }

                handleEdit(cs, strings[1], strings, 2);
                break;

            case "add":
                if (strings.length < 2) {
                    return false;
                }

                handleAdd(cs, strings, 1);
                break;
            case "remove":
                if (strings.length != 2) {
                    return false;
                }

                handleRemove(cs, strings[1]);
                break;
            case "list":
                handleList(cs);
                break;
            case "reload":
                handleReload(cs);
                break;
            case "delay":
                if(strings.length < 2) {
                    return false;
                }
                
                handleDelay(cs, strings[1]);
                break;
            default:
                return false;
        }

        return true;
    }

    private void handleReload(CommandSender cs) {
        if (!checkManagePermission(cs)) {
            return;
        }

        int count = this.plugin.reloadMessages();
        cs.sendMessage(ChatColor.YELLOW + "Broadcast reloaded - " + count + " messages in total");
    }

    private boolean checkManagePermission(CommandSender cs) {
        if (!cs.hasPermission("fish.broadcast.manage")) {
            cs.sendMessage(ChatColor.RED + "You have no permission to manage broadcasts");
            return false;
        }

        return true;
    }

    private boolean checkListPermission(CommandSender cs) {
        if (!cs.hasPermission("fish.broadcast.list")) {
            cs.sendMessage(ChatColor.RED + "You have no permission to list broadcasts");
            return false;
        }

        return true;
    }

    private void handleList(CommandSender cs) {
        if (!checkListPermission(cs)) {
            return;
        }

        Collection<String> messages = this.api.getFormattedMessages();
        List<String> toSend = new ArrayList<>(messages.size() + 2);

        toSend.add(ChatColor.YELLOW + "-- Fish Broadcast messages:");

        int i = 0;
        for (String message : messages) {
            toSend.add((++i) + ". " + message);
        }

        toSend.add(ChatColor.YELLOW + "Use /brc remove <number> to remove message");

        String[] toSendArr = new String[toSend.size()];
        toSend.toArray(toSendArr);

        cs.sendMessage(toSendArr);
    }

    private void internalAdd(CommandSender cs, String[] strings, int startIndex) {
        if (!checkManagePermission(cs)) {
            return;
        }

        String message = joinStrings(strings, startIndex);
        this.plugin.addMessage(message);
    }

    private void handleAdd(CommandSender cs, String[] strings, int startIndex) {
        internalAdd(cs, strings, startIndex);
        cs.sendMessage("Your Fish Broadcast message has been added");
    }

    private String joinStrings(String[] strings, int startIndex) {
        StringBuilder sb = new StringBuilder();

        for (int i = startIndex; i < strings.length; ++i) {
            String string = strings[i];
            if (sb.length() > 0) {
                sb.append(" ");
            }

            sb.append(string);
        }

        return sb.toString();
    }

    private void internalRemove(CommandSender cs, String string) {
        if (!checkManagePermission(cs)) {
            return;
        }

        int index = Integer.parseInt(string) - 1;
        this.plugin.removeMessage(index);
    }

    private void handleRemove(CommandSender cs, String string) {
        internalRemove(cs, string);
        cs.sendMessage("Selected Fish Broadcast message has been removed");
    }

    private void handleEdit(CommandSender cs, String string, String[] strings, int startIndex) {
        if (!checkManagePermission(cs)) {
            return;
        }

        int index = Integer.parseInt(string) - 1;
        String message = joinStrings(strings, startIndex);
        
        this.plugin.editMessage(index, message);
        cs.sendMessage("Selected Fish Broadcast message has been modified");
    }

    private void handleDelay(CommandSender cs, String string) {
        int newDelay = Integer.parseInt(string);
        this.plugin.changeDelay(newDelay);
        
        cs.sendMessage("Fish Broadcast delay has been changed");
    }
}
