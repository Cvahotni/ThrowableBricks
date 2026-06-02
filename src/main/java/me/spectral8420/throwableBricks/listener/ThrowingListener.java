package me.spectral8420.throwableBricks.listener;

import me.spectral8420.throwableBricks.config.ConfigManager;
import me.spectral8420.throwableBricks.tracker.CooldownTracker;
import me.spectral8420.throwableBricks.helper.ThrowHelper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
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

        ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getType() != ConfigManager.getThrowMaterial()) {
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

        ThrowHelper.throwItemStack(player, destination, world, itemStack, 1 + itemMeta.getEnchantLevel(Enchantment.PUNCH));
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
