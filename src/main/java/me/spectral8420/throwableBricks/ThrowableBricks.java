package me.spectral8420.throwableBricks;

import me.spectral8420.throwableBricks.compatibility.Compatibility;
import me.spectral8420.throwableBricks.compatibility.CompatibilityChecks;
import me.spectral8420.throwableBricks.compatibility.LandsCompatibility;
import me.spectral8420.throwableBricks.config.ConfigManager;
import me.spectral8420.throwableBricks.listener.CustomListenerManager;
import me.spectral8420.throwableBricks.helper.ConsoleHelper;
import me.spectral8420.throwableBricks.tracker.CooldownTracker;
import me.spectral8420.throwableBricks.tracker.ProjectileTracker;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class ThrowableBricks extends JavaPlugin {
    @Override
    public void onLoad() {
        if(CompatibilityChecks.isWorldGuardPluginInstalled()) {
            Compatibility.registerCustomWorldGuardFlag();
        }
    }

    @Override
    public void onEnable() {
        CustomListenerManager.register(this);

        CooldownTracker.register(this);
        ProjectileTracker.register(this);

        if(CompatibilityChecks.isLandsPluginInstalled()) {
            LandsCompatibility.setup(this);
        }

        ConfigManager.load(this);
        ConsoleHelper.log(ChatColor.GREEN + "ThrowableBricks has been enabled!");
    }

    @Override
    public void onDisable() {
        ConfigManager.save(this);
        ConsoleHelper.log(ChatColor.RED + "ThrowableBricks has been disabled!");
    }
}
