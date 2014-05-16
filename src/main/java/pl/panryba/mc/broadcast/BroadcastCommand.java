package pl.panryba.mc.broadcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

class BroadcastCommand implements CommandExecutor {

    private final Plugin plugin;
    private final PluginApi api;
    private final LanguageStrings strings;

    public BroadcastCommand(Plugin plugin, PluginApi api, LanguageStrings strings) {
        this.api = api;
        this.plugin = plugin;
        this.strings = strings;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (strings.length == 0) {
            return false;
        }

        String subCmnd = strings[0];
        switch (subCmnd) {
            case "alert":                
                return handleAlert(cs, strings);
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
                if (strings.length < 2) {
                    return false;
                }

                handleDelay(cs, strings[1]);
                break;

            case "token":
                return handleToken(cs, strings, 1);

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
        cs.sendMessage(this.strings.getReloaded(count));
    }

    private boolean checkManagePermission(CommandSender cs) {
        if (!cs.hasPermission("fish.broadcast.manage")) {
            cs.sendMessage(this.strings.getNoPermissionToManage());
            return false;
        }

        return true;
    }

    private boolean checkListPermission(CommandSender cs) {
        if (!cs.hasPermission("fish.broadcast.list")) {
            cs.sendMessage(this.strings.getNoPermissionToList());
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

        toSend.add(this.strings.getListTitle());

        int i = 0;
        for (String message : messages) {
            toSend.add((++i) + ". " + message);
        }

        String[] toSendArr = new String[toSend.size()];
        toSend.toArray(toSendArr);

        cs.sendMessage(toSendArr);
    }

    private void handleAdd(CommandSender cs, String[] strings, int startIndex) {
        if (!checkManagePermission(cs)) {
            return;
        }

        String message = joinStrings(strings, startIndex);
        this.plugin.addMessage(message);

        cs.sendMessage(this.strings.getAdded());
    }

    private boolean handleToken(CommandSender cs, String[] strings, int startIndex) {
        if (!checkManagePermission(cs)) {
            return true;
        }

        int parts = strings.length - startIndex;
        if (parts == 0) {
            return false;
        }

        String name = strings[startIndex];

        if (parts == 1) {
            this.plugin.removeToken(name);
            cs.sendMessage(this.strings.getTokenRemoved(name));
        } else {
            this.plugin.setToken(name, joinStrings(strings, startIndex + 1));
            cs.sendMessage(this.strings.getTokenSet(name));
        }

        return true;
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

    private void handleRemove(CommandSender cs, String string) {
        if (!checkManagePermission(cs)) {
            return;
        }

        int index = Integer.parseInt(string) - 1;
        if (!this.plugin.removeMessage(index)) {
            cs.sendMessage(this.strings.getInvalidIndex());
            return;
        }

        cs.sendMessage(this.strings.getRemoved());
    }

    private void handleEdit(CommandSender cs, String string, String[] strings, int startIndex) {
        if (!checkManagePermission(cs)) {
            return;
        }

        int index = Integer.parseInt(string) - 1;
        String message = joinStrings(strings, startIndex);

        if (!this.plugin.editMessage(index, message)) {
            cs.sendMessage(this.strings.getInvalidIndex());
            return;
        }

        cs.sendMessage(this.strings.getModified());
    }

    private void handleDelay(CommandSender cs, String string) {
        int newDelay = Integer.parseInt(string);
        this.plugin.changeDelay(newDelay);

        cs.sendMessage(this.strings.getDelayChanged());
    }

    private boolean handleAlert(CommandSender cs, String[] strings) {
        if(strings.length == 1) {
            this.plugin.disableAlert();
        } else {
            if(strings.length < 3) {
                return false;
            }
            
            StringBuilder alertMsg = new StringBuilder();
            int period = Integer.parseInt(strings[1]);
            
            for(int i = 2; i < strings.length; ++i) {
                if(alertMsg.length() > 0) {
                    alertMsg.append(" ");
                }
                alertMsg.append(strings[i]);
            }
            
            this.plugin.enableAlert(alertMsg.toString(), period);
        }
        
        return true;
    }
}
