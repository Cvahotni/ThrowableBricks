package me.spectral8420.throwableBricks.compatibility;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionType;
import me.spectral8420.throwableBricks.helper.ConsoleHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldGuardCompatibility {
    public static StateFlag BRICKS_CAN_BREAK_GLASS;

    public static boolean checkWorldGuard(Location location) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = location.getWorld();

        if(world == null) {
            return true;
        }

        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

        if(regionManager == null) {
            return true;
        }

        if(regionManager.getRegions() == null) {
            return true;
        }

        if(regionManager.getRegions().isEmpty()) {
            return true;
        }

        for(ProtectedRegion set : regionManager.getRegions().values()) {
            if(!set.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                continue;
            }

            if(set.getType() == RegionType.GLOBAL) {
                continue;
            }

            StateFlag.State state = set.getFlag(WorldGuardCompatibility.getBricksCanBreakGlass());

            if(state == StateFlag.State.DENY) {
                return false;
            }
        }

        return true;
    }

    public static void registerCustomWorldGuardFlag() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            StateFlag flag = new StateFlag("bricks-can-break-glass", true);
            registry.register(flag);
            WorldGuardCompatibility.setBricksCanBreakGlass(flag);
        }

        catch(FlagConflictException e) {
            Flag<?> existing = registry.get("bricks-can-break-glass");

            if(existing instanceof StateFlag) {
                WorldGuardCompatibility.setBricksCanBreakGlass((StateFlag) existing);
            }

            else {
                ConsoleHelper.log(ChatColor.RED + "Could not register world guard flag: " + e.getMessage());
            }
        }
    }

    public static StateFlag getBricksCanBreakGlass() {
        return BRICKS_CAN_BREAK_GLASS;
    }

    public static void setBricksCanBreakGlass(StateFlag bricksCanBreakGlass) {
        BRICKS_CAN_BREAK_GLASS = bricksCanBreakGlass;
    }
}
