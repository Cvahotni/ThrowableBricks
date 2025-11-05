package me.spectral8420.throwableBricks.tracker;

import me.spectral8420.throwableBricks.ThrowableBricks;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class CooldownTracker {
    private static final HashMap<UUID, Float> cooldowns = new HashMap<>();

    public static void register(ThrowableBricks plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            HashMap<UUID, Float> cooldownsCopy = new HashMap<>(cooldowns);

            for(UUID uuid : cooldownsCopy.keySet()) {
                modifyCooldown(uuid, 1.0f);
            }
        }, 0, 20);
    }

    public static void registerCooldown(UUID uuid, float cooldown) {
        cooldowns.put(uuid, cooldown);
    }

    public static float getCooldown(UUID uuid) {
        return cooldowns.get(uuid);
    }

    public static void modifyCooldown(UUID uuid, float mod) {
        if(!hasCooldown(uuid)) {
            return;
        }

        float cooldown = getCooldown(uuid);

        cooldowns.remove(uuid);
        cooldowns.put(uuid, Math.max(cooldown - mod, 0.0f));
    }

    public static boolean hasCooldown(UUID uuid) {
        return cooldowns.containsKey(uuid);
    }

    public static void removeCooldown(UUID uuid) {
        cooldowns.remove(uuid);
    }
}
