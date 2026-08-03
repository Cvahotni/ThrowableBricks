package me.spectral8420.throwableBricks.listener;

import me.spectral8420.throwableBricks.ThrowableBricks;
import me.spectral8420.throwableBricks.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilEnchantListener implements Listener {
    private final ThrowableBricks plugin;

    public AnvilEnchantListener(ThrowableBricks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        if(!ConfigManager.allowEnchantmentsOnThrowableItem()) {
            return;
        }

        ItemStack target = event.getInventory().getFirstItem();
        ItemStack sacrifice = event.getInventory().getSecondItem();

        if(target == null || target.getType() != ConfigManager.getThrowMaterial() || sacrifice == null) {
            return;
        }

        if(sacrifice.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) sacrifice.getItemMeta();

            int powerLevel = 0;
            int punchLevel = 0;

            if(bookMeta.hasStoredEnchant(Enchantment.POWER)) {
                powerLevel = bookMeta.getStoredEnchantLevel(Enchantment.POWER);
            }

            if(bookMeta.hasStoredEnchant(Enchantment.PUNCH)) {
                punchLevel = bookMeta.getStoredEnchantLevel(Enchantment.PUNCH);
            }

            if(powerLevel > 0 || punchLevel > 0) {
                ItemStack result = target.clone();
                ItemMeta resultMeta = result.getItemMeta();

                if(powerLevel > 0) {
                    resultMeta.addEnchant(Enchantment.POWER, powerLevel, false);
                }

                if(punchLevel > 0) {
                    resultMeta.addEnchant(Enchantment.PUNCH, punchLevel, false);
                }

                result.setItemMeta(resultMeta);
                event.setResult(result);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!ConfigManager.allowEnchantmentsOnThrowableItem()) {
            return;
        }

        if(!(event.getInventory() instanceof AnvilInventory anvil)) {
            return;
        }

        if(event.getRawSlot() != 2) {
            return;
        }

        ItemStack resultItem = anvil.getItem(2);

        if(resultItem == null || resultItem.getType() == Material.AIR) {
            return;
        }

        Player player = Bukkit.getPlayer(event.getWhoClicked().getName());

        if(player == null) {
            return;
        }

        int previousXP = player.getTotalExperience();

        if(resultItem.getType() == ConfigManager.getThrowMaterial()) {
            event.setCancelled(false);

            Bukkit.getScheduler().runTask(plugin, () -> {
                ItemStack result = anvil.getItem(2);

                if(result != null) {
                    event.setCursor(result.clone());

                    anvil.setItem(0, new ItemStack(Material.AIR));
                    anvil.setItem(1, new ItemStack(Material.AIR));

                    player.getWorld().playSound(
                            player.getLocation(),
                            Sound.BLOCK_ANVIL_USE,
                            1.0f,
                            1.0f
                    );

                    player.setTotalExperience(previousXP);
                }
            });
        }
    }
}