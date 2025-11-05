package me.spectral8420.throwableBricks.listener;

import me.spectral8420.throwableBricks.ThrowableBricks;
import org.bukkit.Bukkit;

public class CustomListenerManager {
    public static void register(ThrowableBricks plugin) {
        Bukkit.getPluginManager().registerEvents(new ThrowingListener(), plugin);
    }
}
