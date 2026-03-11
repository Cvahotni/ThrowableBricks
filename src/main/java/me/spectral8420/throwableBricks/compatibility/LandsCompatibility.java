package me.spectral8420.throwableBricks.compatibility;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.land.LandWorld;
import me.angeschossen.lands.api.role.Role;
import me.spectral8420.throwableBricks.ThrowableBricks;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;

public class LandsCompatibility {
    private static LandsIntegration integration;

    public static void setup(ThrowableBricks plugin) {
        if(!CompatibilityChecks.isLandsPluginInstalled()) {
            return;
        }

        integration = LandsIntegration.of(plugin);
    }

    public static boolean checkLands(Location location) {
        World world = location.getWorld();

        if(world == null) {
            return true;
        }

        LandWorld landWorld = LandsCompatibility.getIntegration().getWorld(world);

        if(landWorld == null) {
            return true;
        }

        int bitShift = 4;

        int chunkX = location.getBlockX() >> bitShift;
        int chunkZ = location.getBlockZ() >> bitShift;

        Land land = landWorld.getLandByChunk(chunkX, chunkZ);

        if(land == null) {
            return true;
        }

        Area area = land.getArea(location);

        if(area == null) {
            return true;
        }

        Role role = area.getEntryRole();
        Set<RoleFlag> flagList = role.getActionFlags();

        for(RoleFlag flag : flagList) {
            String flagName = flag.getName();

            if(!flagName.equals("block_break")) {
                continue;
            }

            return flag.getDefaultState();
        }

        return true;
    }

    public static LandsIntegration getIntegration() {
        return integration;
    }
}
