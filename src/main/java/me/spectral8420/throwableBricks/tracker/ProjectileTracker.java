package me.spectral8420.throwableBricks.tracker;

import me.spectral8420.throwableBricks.ThrowableBricks;
import me.spectral8420.throwableBricks.compatibility.CompatibilityChecks;
import me.spectral8420.throwableBricks.compatibility.LandsCompatibility;
import me.spectral8420.throwableBricks.compatibility.WorldGuardCompatibility;
import me.spectral8420.throwableBricks.config.ConfigManager;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
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
        }, 0, 0);
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
                            hitEntity(player, world, entity.getItemStack());
                            hasHitTarget = true;

                            if(!ConfigManager.isDamageMultipleEntitiesAtOnce()) {
                                break;
                            }
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
                    hitEntity(livingEntity, world, entity.getItemStack());
                    hasHitTarget = true;

                    if(!ConfigManager.isDamageMultipleEntitiesAtOnce()) {
                        break;
                    }
                }
            }

            List<Vector> offsets = Arrays.asList(
                    new Vector(-1.0, -1.0, -1.0),
                    new Vector(-1.0, -1.0, 0.0),
                    new Vector(-1.0, -1.0, 1.0),
                    new Vector(-1.0, 0.0, -1.0),
                    new Vector(-1.0, 0.0, 0.0),
                    new Vector(-1.0, 0.0, 1.0),
                    new Vector(-1.0, 1.0, -1.0),
                    new Vector(-1.0, 1.0, 0.0),
                    new Vector(-1.0, 1.0, 1.0),
                    new Vector(0.0, -1.0, -1.0),
                    new Vector(0.0, -1.0, 0.0),
                    new Vector(0.0, -1.0, 1.0),
                    new Vector(0.0, 0.0, -1.0),
                    new Vector(0.0, 0.0, 0.0),
                    new Vector(0.0, 0.0, 1.0),
                    new Vector(0.0, 1.0, -1.0),
                    new Vector(0.0, 1.0, 0.0),
                    new Vector(0.0, 1.0, 1.0),
                    new Vector(1.0, -1.0, -1.0),
                    new Vector(1.0, -1.0, 0.0),
                    new Vector(1.0, -1.0, 1.0),
                    new Vector(1.0, 0.0, -1.0),
                    new Vector(1.0, 0.0, 0.0),
                    new Vector(1.0, 0.0, 1.0),
                    new Vector(1.0, 1.0, -1.0),
                    new Vector(1.0, 1.0, 0.0),
                    new Vector(1.0, 1.0, 1.0)
            );

            double distance = 1000.0;
            Vector closest = offsets.getFirst();

            boolean isFlaming = entity.getItemStack().getItemMeta() != null && entity.getItemStack().getItemMeta().hasEnchant(Enchantment.FLAME);

            for(Vector offset : offsets) {
                Location modifiedLocation = new Location(
                        world,
                        location.x() + offset.getX(),
                        location.y() + offset.getY(),
                        location.z() + offset.getZ()
                );

                double currentDistance = modifiedLocation.distanceSquared(location);
                BlockState state = world.getBlockState(modifiedLocation);

                Material type = state.getBlock().getType();

                boolean isFlameTarget = isFlaming && (type == Material.TNT || type == Material.CAMPFIRE || type == Material.SOUL_CAMPFIRE || type.name().contains("CANDLE"));

                if(!ConfigManager.isBreakableMaterial(state.getBlock().getType()) && !isFlameTarget) {
                    continue;
                }

                if(currentDistance < distance) {
                    distance = currentDistance;
                    closest = offset;
                }
            }

            Location modifiedLocation = location.clone().add(closest);

            //get block reference before offset is added to help with thin targets
            org.bukkit.block.Block trueBlock = world.getBlockAt(modifiedLocation);
            Material trueType = trueBlock.getType();

            Location blockLocation = new Location(
                    world,
                    Math.floor(modifiedLocation.getX()),
                    Math.floor(modifiedLocation.getY()),
                    Math.floor(modifiedLocation.getZ())
            );

            blockLocation.add(0.5, 0.5, 0.5);
            BlockState state = world.getBlockState(blockLocation);

            if(CompatibilityChecks.isLandsPluginInstalled()) {
                if(!LandsCompatibility.checkLands(blockLocation)) {
                    continue;
                }
            }

            if(CompatibilityChecks.isWorldGuardPluginInstalled()) {
                if(!WorldGuardCompatibility.checkWorldGuard(blockLocation)) {
                    continue;
                }
            }

            if(ConfigManager.isBreakableMaterial(state.getBlock().getType())) {
                world.spawnParticle(
                        Particle.BLOCK,
                        blockLocation,
                        30,
                        0.3, 0.3, 0.3,
                        state.getBlock().getBlockData()
                );

                BlockData data = state.getBlock().getBlockData();
                SoundGroup soundGroup = data.getSoundGroup();

                world.playSound(
                        blockLocation,
                        soundGroup.getBreakSound(),
                        1.0f, 1.0f
                );

                world.setBlockData(blockLocation, Material.AIR.createBlockData());
                hasHitTarget = true;
            }

            //lets flame bricks work like flame bows
            if(isFlaming) {
                //TNT
                if(trueType == Material.TNT) {
                    trueBlock.setType(Material.AIR);
                    org.bukkit.entity.TNTPrimed tnt = world.spawn(blockLocation, org.bukkit.entity.TNTPrimed.class);
                    org.bukkit.entity.Entity owner = world.getEntity(projectileOwners.get(uuid));
                    if(owner instanceof Player p) {
                        tnt.setSource(p);
                    }
                    hasHitTarget = true;
                }
                //campfires
                else if(trueType == Material.CAMPFIRE || trueType == Material.SOUL_CAMPFIRE) {
                    if(trueBlock.getBlockData() instanceof org.bukkit.block.data.type.Campfire campfireData) {
                        if(!campfireData.isLit()) {
                            campfireData.setLit(true);
                            trueBlock.setBlockData(campfireData);
                            world.playSound(blockLocation, org.bukkit.Sound.ITEM_FLINTANDSTEEL_USE, 1.0f, 1.0f);
                            hasHitTarget = true;
                        }
                    }
                }
                //candles
                else if(trueBlock.getBlockData() instanceof org.bukkit.block.data.type.Candle candleData) {
                    if(!candleData.isLit()) {
                        candleData.setLit(true);
                        trueBlock.setBlockData(candleData);
                        world.playSound(blockLocation, org.bukkit.Sound.ITEM_FLINTANDSTEEL_USE, 1.0f, 1.0f);
                        hasHitTarget = true;
                    }
                }
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

    private static void hitEntity(LivingEntity livingEntity, World world, ItemStack itemStack) {
        if(itemStack == null) {
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            return;
        }

        double damageMultiplier = 1.0;

        if(itemMeta.hasEnchant(Enchantment.POWER)) {
            int level = itemMeta.getEnchantLevel(Enchantment.POWER);
            damageMultiplier = 1.0 + (((double) level) * ConfigManager.getPowerDamageMultiplier());
        }

        //burn the living
        if(itemMeta.hasEnchant(Enchantment.FLAME)) {
            livingEntity.setFireTicks(100);
        }

        world.playSound(livingEntity.getLocation(), ConfigManager.getSmashSound(), 1.0f, 1.0f);
        livingEntity.damage(ConfigManager.getDamageFromBrick() * damageMultiplier);

        for(PotionEffect effect : ConfigManager.getEffectsToGiveOnHit()) {
            livingEntity.addPotionEffect(effect);
        }
    }
}
