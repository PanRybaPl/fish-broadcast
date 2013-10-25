package pl.panryba.mc.broadcast;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

class BroadcastCommand implements CommandExecutor {
    private final Plugin plugin;

    public BroadcastCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(strings.length == 0) {
            return false;
        }
        
        String subCmnd = strings[0];
        switch(subCmnd) {
            case "reload":
                handleReload(cs);
                break;
            default:
                return false;
        }
        
        return true;
    }

    private void handleReload(CommandSender cs) {
        int count = this.plugin.reloadMessages();
        cs.sendMessage(ChatColor.YELLOW + "Broadcast reloaded - " + count + " messages in total");
    }
}
