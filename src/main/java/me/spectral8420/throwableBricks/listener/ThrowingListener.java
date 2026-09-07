package me.spectral8420.throwableBricks.listener;

import me.spectral8420.throwableBricks.config.ConfigManager;
import me.spectral8420.throwableBricks.tracker.CooldownTracker;
import me.spectral8420.throwableBricks.helper.ThrowHelper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class ThrowingListener implements Listener {
    @EventHandler
    public void onThrow(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Action action = event.getAction();

        if(action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if(block != null && block.getType().isInteractable()) {
            Material type = block.getType();
            
            //let flame bricks work like arrows
            if (type != Material.CAMPFIRE && type != Material.SOUL_CAMPFIRE && 
                type != Material.TNT && !type.name().contains("CANDLE")) {
                return; 
            }
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if(!ConfigManager.isThrowMaterial(item.getType())) {
            return;
        }

        float cooldown = (float) ConfigManager.getCooldown();

        if(!CooldownTracker.hasCooldown(player.getUniqueId())) {
            CooldownTracker.registerCooldown(player.getUniqueId(), cooldown);
        }

        else {
            if(CooldownTracker.getCooldown(player.getUniqueId()) <= 0.0f) {
                CooldownTracker.removeCooldown(player.getUniqueId());
                CooldownTracker.registerCooldown(player.getUniqueId(), cooldown);
            }

            else {
                return;
            }
        }

        Location from = player.getEyeLocation();
        Vector direction = from.getDirection();

        Location destination = from.add(direction.multiply(5));

        ItemStack itemStack = item.clone();
        itemStack.setAmount(1);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            return;
        }

        boolean hasFlame = itemMeta.hasEnchant(Enchantment.FLAME);
        Location spawnLoc = player.getEyeLocation();

        ThrowHelper.throwItemStack(player, destination, world, itemStack, 1 + itemMeta.getEnchantLevel(Enchantment.PUNCH));
        
        //animate the fire on the brick
        if(hasFlame) {
            org.bukkit.plugin.Plugin mainPlugin = Bukkit.getPluginManager().getPlugin("ThrowableBricks");

            if(mainPlugin != null) {
                Bukkit.getScheduler().runTask(mainPlugin, () -> {
                    for(org.bukkit.entity.Entity nearby : world.getNearbyEntities(spawnLoc, 2.0, 2.0, 2.0)) {
                        if(nearby instanceof Item projectileItem) {
                            if(ConfigManager.isThrowMaterial(projectileItem.getItemStack().getType()) && projectileItem.getPickupDelay() > 0) {
                                projectileItem.setVisualFire(true);
                                projectileItem.setFireTicks(300);

                                break;
                            }
                        }
                    }
                });
            }
        }
        
        int amount = item.getAmount();

        if(player.getGameMode() != GameMode.CREATIVE) {
            if(amount <= 1) {
                player.getInventory().setItemInMainHand(null);
            }

            else {
                item.setAmount(amount - 1);
            }
        }

        player.playSound(player, ConfigManager.getThrowSound(), 1.0f, 1.0f);
        player.updateInventory();
    }
}
