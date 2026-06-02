package me.spectral8420.throwableBricks.listener;

import me.spectral8420.throwableBricks.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilEnchantListener implements Listener {
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
}