package me.spectral8420.throwableBricks.compatibility;

import org.bukkit.Bukkit;

public class CompatibilityChecks {
    public static boolean isLandsPluginInstalled() {
        return Bukkit.getPluginManager().getPlugin("Lands") != null;
    }

    public static boolean isWorldGuardPluginInstalled() {
        return Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }
}
