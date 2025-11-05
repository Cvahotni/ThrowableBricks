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
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.land.LandWorld;
import me.angeschossen.lands.api.role.Role;
import me.spectral8420.throwableBricks.helper.ConsoleHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;

public class Compatibility {
    public static StateFlag BRICKS_CAN_BREAK_GLASS;

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

            StateFlag.State state = set.getFlag(BRICKS_CAN_BREAK_GLASS);

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
            BRICKS_CAN_BREAK_GLASS = flag;
        }

        catch(FlagConflictException e) {
            Flag<?> existing = registry.get("bricks-can-break-glass");

            if(existing instanceof StateFlag) {
                BRICKS_CAN_BREAK_GLASS = (StateFlag) existing;
            }

            else {
                ConsoleHelper.log(ChatColor.RED + "Could not register world guard flag: " + e.getMessage());
            }
        }
    }
}
