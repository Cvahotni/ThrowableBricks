package me.spectral8420.throwableBricks.compatibility;

import me.angeschossen.lands.api.LandsIntegration;
import me.spectral8420.throwableBricks.ThrowableBricks;

public class LandsCompatibility {
    private static LandsIntegration integration;

    public static void setup(ThrowableBricks plugin) {
        if(!CompatibilityChecks.isLandsPluginInstalled()) {
            return;
        }

        integration = LandsIntegration.of(plugin);
    }

    public static LandsIntegration getIntegration() {
        return integration;
    }
}
