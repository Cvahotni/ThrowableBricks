package me.spectral8420.throwableBricks.tracker;

import me.spectral8420.throwableBricks.ThrowableBricks;
import me.spectral8420.throwableBricks.compatibility.Compatibility;
import me.spectral8420.throwableBricks.compatibility.CompatibilityChecks;
import me.spectral8420.throwableBricks.config.ConfigManager;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class ProjectileTracker {
    private static final List<UUID> projectiles = new ArrayList<>();
    private static final HashMap<UUID, UUID> projectileOwners = new HashMap<>();

    public static void register(ThrowableBricks plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(World world : Bukkit.getWorlds()) {
                damageTargets(world);
            }
        }, 0, 1);
    }

    public static void addProjectile(UUID uuid, UUID owner) {
        projectiles.add(uuid);
        projectileOwners.put(uuid, owner);
    }

    public static void damageTargets(World world) {
        List<UUID> projectilesToRemove = new ArrayList<>();

        for(UUID uuid : projectiles) {
            Item entity = (Item) world.getEntity(uuid);

            if(entity == null) {
                continue;
            }

            if(!projectileOwners.containsKey(uuid)) {
                continue;
            }

            if(entity.isOnGround()) {
                entity.setPickupDelay(ConfigManager.getDefaultPickupDelay());
                projectilesToRemove.add(entity.getUniqueId());
            }

            Location location = entity.getLocation();

            boolean hasHitTarget = false;

            double minTicksLived = ConfigManager.getMinTicksLived();
            double checkDistance = ConfigManager.getCheckDistance();

            for(Player player : world.getPlayers()) {
                if(entity.getTicksLived() > minTicksLived || projectileOwners.get(uuid) != player.getUniqueId()) {
                    Location playerLocation = player.getLocation();

                    if(location.distanceSquared(playerLocation) <= checkDistance) {
                        if(player.getGameMode() != GameMode.CREATIVE) {
                            hitEntity(player, world);
                            hasHitTarget = true;
                        }
                    }
                }
            }

            for(Entity currentEntity : world.getEntities()) {
                Location entityLocation = currentEntity.getLocation();

                if(currentEntity instanceof Player) {
                    continue;
                }

                if(!(currentEntity instanceof LivingEntity livingEntity)) {
                    continue;
                }

                if(location.distanceSquared(entityLocation) <= checkDistance) {
                    hitEntity(livingEntity, world);
                    hasHitTarget = true;
                }
            }

            List<Vector> offsets = Arrays.asList(
                    new Vector(-1.0, 0.0, 0.0),
                    new Vector(1.0, 0.0, 0.0),
                    new Vector(0.0, -1.0, 0.0),
                    new Vector(0.0, 1.0, 0.0),
                    new Vector(0.0, 0.0, -1.0),
                    new Vector(0.0, 0.0, 1.0)
            );

            double distance = 1000.0;
            Vector closest = offsets.getFirst();

            for(Vector offset : offsets) {
                Location modifiedLocation = new Location(
                        world,
                        location.x() + offset.getX(),
                        location.y() + offset.getY(),
                        location.z() + offset.getZ()
                );

                double currentDistance = modifiedLocation.distanceSquared(location);
                BlockState state = world.getBlockState(modifiedLocation);

                if(!state.getBlock().getType().name().contains("GLASS")) {
                    continue;
                }

                if(currentDistance < distance) {
                    distance = currentDistance;
                    closest = offset;
                }
            }

            Location modifiedLocation = location.clone().add(closest);
            BlockState state = world.getBlockState(modifiedLocation);

            if(CompatibilityChecks.isLandsPluginInstalled()) {
                if(!Compatibility.checkLands(modifiedLocation)) {
                    continue;
                }
            }

            if(CompatibilityChecks.isWorldGuardPluginInstalled()) {
                if(!Compatibility.checkWorldGuard(modifiedLocation)) {
                    continue;
                }
            }

            if(state.getBlock().getType().name().contains("GLASS")) {
                world.spawnParticle(
                        Particle.BLOCK,
                        modifiedLocation,
                        30,
                        0.3, 0.3, 0.3,
                        state.getBlock().getBlockData()
                );

                world.playSound(
                        modifiedLocation,
                        Sound.BLOCK_GLASS_BREAK,
                        1.0f, 1.0f
                );

                world.setBlockData(modifiedLocation, Material.AIR.createBlockData());
                hasHitTarget = true;
            }

            if(hasHitTarget) {
                projectilesToRemove.add(entity.getUniqueId());
                entity.remove();
            }
        }

        for(UUID uuid : projectilesToRemove) {
            projectiles.remove(uuid);
            projectileOwners.remove(uuid);
        }
    }

    private static void hitEntity(LivingEntity livingEntity, World world) {
        world.playSound(livingEntity.getLocation(), Sound.BLOCK_DECORATED_POT_SHATTER, 1.0f, 1.0f);
        livingEntity.damage(ConfigManager.getDamageFromBrick());

        for(PotionEffect effect : ConfigManager.getEffectsToGiveOnHit()) {
            livingEntity.addPotionEffect(effect);
        }
    }
}
