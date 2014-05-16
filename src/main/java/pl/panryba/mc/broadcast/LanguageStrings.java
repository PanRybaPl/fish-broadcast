package pl.panryba.mc.broadcast;

import org.bukkit.configuration.file.FileConfiguration;

public class LanguageStrings {
    private final FileConfiguration config;
    private final FileConfiguration defaults;
    
    public LanguageStrings(FileConfiguration config, FileConfiguration defaults) {
        this.config = config;
        this.defaults = defaults;
    }
    
    private String getString(String name) {
        String template = this.config.getString(name);
        
        if(template == null) {
            template = this.defaults.getString(name);
        }
        
        return ColorUtils.replaceColors(template);
    }
    
    public String getReloaded(int count) {
        return String.format(getString("reloaded"), count);
    }

    String getNoPermissionToManage() {
        return getString("no_manage_permission");
    }

    String getNoPermissionToList() {
        return getString("no_list_permission");
    }

    String getListTitle() {
        return getString("list_title");
    }

    String getAdded() {
        return getString("added");
    }

    String getInvalidIndex() {
        return getString("invalid_index");
    }

    String getRemoved() {
        return getString("removed");
    }

    String getModified() {
        return getString("modified");
    }

    String getDelayChanged() {
        return getString("delay_changed");
    }

    String getTokenSet(String name) {
        return String.format(getString("token_set"), name);
    }

    String getTokenRemoved(String name) {
        return String.format(getString("token_removed"), name);
    }
}
