package pl.panryba.mc.broadcast;

import org.mcstats.Metrics;

public class FishMetrics {
    public static void onEnabled(org.bukkit.plugin.Plugin plugin) {
        try {
            Metrics metrics = new Metrics(plugin);
            metrics.start();
        } catch (java.io.IOException e) {
            // Failed to submit the stats :-(
        }
    }
}
