package me.spectral8420.throwableBricks.config;

import me.spectral8420.throwableBricks.ThrowableBricks;
import me.spectral8420.throwableBricks.helper.ConsoleHelper;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    //TODO: Add functionality for multiple configuration files, instead of using the built-in one.

    private static double damageFromBrick = 8.0;
    private static int defaultPickupDelay = 20;
    private static double minTicksLived = 20.0;
    private static double checkDistance = 4.0;
    private static double cooldown = 1.0;

    private static final List<PotionEffect> effectsToGiveOnHit = new ArrayList<>();

    public static void load(ThrowableBricks plugin) {
        FileConfiguration config = plugin.getConfig();

        if(config.contains("damageFromBrick")) {
            damageFromBrick = config.getDouble("damageFromBrick");
        }

        if(config.contains("defaultPickupDelay")) {
            defaultPickupDelay = config.getInt("defaultPickupDelay");
        }

        if(config.contains("minTicksLived")) {
            minTicksLived = config.getDouble("minTicksLived");
        }

        if(config.contains("checkDistance")) {
            checkDistance = config.getDouble("checkDistance");
        }

        if(config.contains("cooldown")) {
            cooldown = config.getDouble("cooldown");
        }

        loadPotionEffects(config);
        plugin.saveConfig();
    }

    public static void save(ThrowableBricks plugin) {
        FileConfiguration config = plugin.getConfig();

        config.set("damageFromBrick", damageFromBrick);
        config.set("defaultPickupDelay", defaultPickupDelay);
        config.set("minTicksLived", minTicksLived);
        config.set("checkDistance", checkDistance);
        config.set("cooldown", cooldown);

        savePotionEffects(config);
        plugin.saveConfig();
    }

    private static void loadPotionEffects(FileConfiguration config) {
        if(!config.contains("effectsToGiveOnHit")) {
            config.set("effectsToGiveOnHit", Arrays.asList(
                "BLINDNESS : 50 : 2",
                "CONFUSION : 50 : 2"
            ));
        }

        List<String> potionEffectStrings = config.getStringList("effectsToGiveOnHit");

        for(String line : potionEffectStrings) {
            String[] split = line.split(" : ");
            int tokenSpiltAmount = 3;

            if(split.length != tokenSpiltAmount) {
                ConsoleHelper.log(ChatColor.RED + "Invalid potion effect: " + line);
                continue;
            }

            try {
                PotionEffectType type = PotionEffectType.getByName(split[0]);

                if(type == null) {
                    ConsoleHelper.log(ChatColor.RED + "Invalid potion effect, type not found: " + split[0]);
                    continue;
                }

                int duration = Integer.parseInt(split[1]);
                int amplifier = Integer.parseInt(split[2]);

                effectsToGiveOnHit.add(new PotionEffect(type, duration, amplifier));
            }

            catch (Exception e) {
                ConsoleHelper.log(ChatColor.RED + "Exception whilst processing potion effect: " + line);
            }
        }
    }

    private static void savePotionEffects(FileConfiguration config) {
        List<String> potionEffectStrings = new ArrayList<>();

        for(PotionEffect effect : effectsToGiveOnHit) {
            String name = effect.getType().getName();
            String duration = "" + effect.getDuration();
            String amplifier = "" + effect.getAmplifier();

            String line = name + " : " + duration + " : " + amplifier;
            potionEffectStrings.add(line);
        }

        config.set("effectsToGiveOnHit", potionEffectStrings);
    }

    public static double getDamageFromBrick() {
        return damageFromBrick;
    }

    public static int getDefaultPickupDelay() {
        return defaultPickupDelay;
    }

    public static double getMinTicksLived() {
        return minTicksLived;
    }

    public static double getCheckDistance() {
        return checkDistance;
    }

    public static double getCooldown() {
        return cooldown;
    }

    public static List<PotionEffect> getEffectsToGiveOnHit() {
        return effectsToGiveOnHit;
    }
}
