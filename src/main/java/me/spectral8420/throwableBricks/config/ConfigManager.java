package me.spectral8420.throwableBricks.config;

import me.spectral8420.throwableBricks.ThrowableBricks;
import me.spectral8420.throwableBricks.helper.ConsoleHelper;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigManager {
    //TODO: Add functionality for multiple configuration files, instead of using the built-in one.

    private static double damageFromBrick = 8.0;
    private static double powerDamageMultiplier = 0.2;
    private static int defaultPickupDelay = 20;
    private static double minTicksLived = 20.0;
    private static double checkDistance = 4.0;
    private static double cooldown = 1.0;

    private static boolean damageMultipleEntitiesAtOnce = false;
    private static boolean allowEnchantmentsOnThrowableItem = true;

    private static Material throwMaterial = Material.BRICK;

    private static NamespacedKey throwSound = NamespacedKey.fromString("entity.snowball.throw");
    private static NamespacedKey smashSound = NamespacedKey.fromString("block.decorated_pot.break");

    private static final List<Material> breakableMaterials = new ArrayList<>();
    private static final List<PotionEffect> effectsToGiveOnHit = new ArrayList<>();

    public static void load(ThrowableBricks plugin) {
        FileConfiguration config = plugin.getConfig();

        try {
            if(config.contains("damageFromBrick")) {
                damageFromBrick = config.getDouble("damageFromBrick");
            }

            if(config.contains("powerDamageMultiplier")) {
                powerDamageMultiplier = config.getDouble("powerDamageMultiplier");
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

            if(config.contains("damageMultipleEntitiesAtOnce")) {
                damageMultipleEntitiesAtOnce = config.getBoolean("damageMultipleEntitiesAtOnce");
            }

            if(config.contains("allowEnchantmentsOnThrowableItem")) {
                allowEnchantmentsOnThrowableItem = config.getBoolean("allowEnchantmentsOnThrowableItem");
            }

            if(config.contains("throwMaterial")) {
                throwMaterial = Material.valueOf(config.getString("throwMaterial"));
            }

            if(config.contains("throwSound")) {
                throwSound = NamespacedKey.fromString(Objects.requireNonNull(config.getString("throwSound")));
            }

            if(config.contains("smashSound")) {
                smashSound = NamespacedKey.fromString(Objects.requireNonNull(config.getString("smashSound")));
            }

            if(config.contains("breakableMaterials")) {
                List<String> breakableMaterialsList = config.getStringList("breakableMaterials");

                breakableMaterials.clear();
                breakableMaterials.addAll(breakableMaterialsList.stream().map(Material::valueOf).toList());
            }

            else {
                breakableMaterials.addAll(Arrays.asList(
                        Material.GLASS,
                        Material.TINTED_GLASS,
                        Material.GLASS_PANE,
                        Material.WHITE_STAINED_GLASS,
                        Material.ORANGE_STAINED_GLASS,
                        Material.LIGHT_BLUE_STAINED_GLASS,
                        Material.YELLOW_STAINED_GLASS,
                        Material.LIME_STAINED_GLASS,
                        Material.PINK_STAINED_GLASS,
                        Material.GRAY_STAINED_GLASS,
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.CYAN_STAINED_GLASS,
                        Material.PURPLE_STAINED_GLASS,
                        Material.BLUE_STAINED_GLASS,
                        Material.BROWN_STAINED_GLASS,
                        Material.GREEN_STAINED_GLASS,
                        Material.RED_STAINED_GLASS,
                        Material.BLACK_STAINED_GLASS,
                        Material.WHITE_STAINED_GLASS_PANE,
                        Material.ORANGE_STAINED_GLASS_PANE,
                        Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                        Material.YELLOW_STAINED_GLASS_PANE,
                        Material.LIME_STAINED_GLASS_PANE,
                        Material.PINK_STAINED_GLASS_PANE,
                        Material.GRAY_STAINED_GLASS_PANE,
                        Material.LIGHT_GRAY_STAINED_GLASS_PANE,
                        Material.CYAN_STAINED_GLASS_PANE,
                        Material.PURPLE_STAINED_GLASS_PANE,
                        Material.BLUE_STAINED_GLASS_PANE,
                        Material.BROWN_STAINED_GLASS_PANE,
                        Material.GREEN_STAINED_GLASS_PANE,
                        Material.RED_STAINED_GLASS_PANE,
                        Material.BLACK_STAINED_GLASS_PANE
                ));
            }

            loadPotionEffects(config);
        }

        catch(Exception e) {
            ConsoleHelper.log(ChatColor.RED + "There was an exception whilst loading the config file: " + e);
        }

        plugin.saveConfig();
    }

    public static void save(ThrowableBricks plugin) {
        FileConfiguration config = plugin.getConfig();

        try {
            config.set("damageFromBrick", damageFromBrick);
            config.set("powerDamageMultiplier", powerDamageMultiplier);
            config.set("defaultPickupDelay", defaultPickupDelay);
            config.set("minTicksLived", minTicksLived);
            config.set("checkDistance", checkDistance);
            config.set("cooldown", cooldown);
            config.set("damageMultipleEntitiesAtOnce", damageMultipleEntitiesAtOnce);
            config.set("allowEnchantmentsOnThrowableItem", allowEnchantmentsOnThrowableItem);
            config.set("throwMaterial", throwMaterial.name());
            config.set("throwSound", throwSound.toString());
            config.set("smashSound", smashSound.toString());
            config.set("breakableMaterials", breakableMaterials.stream().map(Material::name).collect(Collectors.toList()));

            savePotionEffects(config);
        }

        catch(Exception e) {
            ConsoleHelper.log(ChatColor.RED + "There was an exception whilst saving the config file: " + e);
        }

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

    public static double getPowerDamageMultiplier() {
        return powerDamageMultiplier;
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

    public static boolean isDamageMultipleEntitiesAtOnce() {
        return damageMultipleEntitiesAtOnce;
    }

    public static boolean allowEnchantmentsOnThrowableItem() {
        return allowEnchantmentsOnThrowableItem;
    }

    public static List<PotionEffect> getEffectsToGiveOnHit() {
        return effectsToGiveOnHit;
    }

    public static Material getThrowMaterial() {
        return throwMaterial;
    }

    public static Sound getThrowSound() {
        return Registry.SOUNDS.get(throwSound);
    }

    public static Sound getSmashSound() {
        return Registry.SOUNDS.get(smashSound);
    }

    public static boolean isBreakableMaterial(Material material) {
        return breakableMaterials.contains(material);
    }
}
