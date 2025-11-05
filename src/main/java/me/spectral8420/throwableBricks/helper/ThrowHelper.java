package me.spectral8420.throwableBricks.helper;

import me.spectral8420.throwableBricks.tracker.ProjectileTracker;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ThrowHelper {
    public static void throwItemStack(Player player, Location target, World world, ItemStack item, double speed) {
        Item entity = world.dropItemNaturally(player.getLocation(), item);

        entity.setPickupDelay(Integer.MAX_VALUE);
        entity.setGravity(true);
        entity.setWillAge(false);
        entity.setCustomNameVisible(false);
        entity.setGlowing(false);

        Vector direction = target.toVector().subtract(player.getLocation().toVector()).normalize();
        entity.setVelocity(direction.multiply(speed));

        ProjectileTracker.addProjectile(entity.getUniqueId(), player.getUniqueId());
    }
}
