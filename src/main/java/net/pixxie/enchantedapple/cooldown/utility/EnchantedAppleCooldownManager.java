package net.pixxie.enchantedapple.cooldown.utility;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.SirBlobman.api.utility.Util;
import net.pixxie.enchantedapple.cooldown.EnchantedAppleCooldown;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnchantedAppleCooldownManager {
    private static final Map<UUID, Long> cooldownExpireMap = Util.newMap();

    public static boolean isInCooldown(Player player) {
        if(player == null) return false;

        UUID uuid = player.getUniqueId();
        return cooldownExpireMap.containsKey(uuid);
    }

    public static void addCooldown(Player player) {
        if(player == null) return;

        long secondsToAdd = getSecondsToAdd(player);
        long millisToAdd = TimeUnit.SECONDS.toMillis(secondsToAdd);

        long systemMillis = System.currentTimeMillis();
        long expireMillis = (systemMillis + millisToAdd);

        UUID uuid = player.getUniqueId();
        cooldownExpireMap.put(uuid, expireMillis);
    }

    public static void removeCooldown(Player player) {
        if(player == null) return;

        UUID uuid = player.getUniqueId();
        cooldownExpireMap.remove(uuid);
    }

    public static long getTimeLeftMillis(Player player) {
        if(!isInCooldown(player)) return 0L;

        UUID uuid = player.getUniqueId();
        long expireTime = cooldownExpireMap.getOrDefault(uuid, 0L);

        long systemTime = System.currentTimeMillis();
        return (expireTime - systemTime);
    }

    public static long getTimeLeftSeconds(Player player) {
        long timeLeftMillis = getTimeLeftMillis(player);
        return TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis);
    }

    public static boolean canBypass(Player player) {
        if(player == null) return false;

        EnchantedAppleCooldown plugin = getPlugin();
        FileConfiguration config = plugin.getConfig();
        if(config == null) return false;

        String bypassPermission = config.getString("bypass-permission");
        if(bypassPermission == null || bypassPermission.isEmpty()) return false;

        Permission permission = new Permission(bypassPermission, "Bypass the enchantedapple cooldown timer.", PermissionDefault.FALSE);
        return player.hasPermission(permission);
    }


    private static EnchantedAppleCooldown getPlugin() {
        return JavaPlugin.getPlugin(EnchantedAppleCooldown.class);
    }

    private static List<String> getMatchingPermissions(Player player) {
        if(player == null) return Collections.emptyList();

        List<String> permissionList = Util.newList();
        Set<PermissionAttachmentInfo> effectivePermissionSet = player.getEffectivePermissions();
        for(PermissionAttachmentInfo effectivePermission : effectivePermissionSet) {
            if(!effectivePermission.getValue()) continue;

            String permission = effectivePermission.getPermission();
            if(!permission.startsWith("enchantedapple.cooldown.timer.")) continue;

            permissionList.add(permission);
        }

        return permissionList;
    }

    private static int getSecondsToAdd(Player player) {
        EnchantedAppleCooldown plugin = getPlugin();
        FileConfiguration config = plugin.getConfig();
        if(config == null) return getNormalSecondsToAdd();

        String cooldownMode = config.getString("cooldown-mode");
        if(cooldownMode == null || !cooldownMode.equals("PERMISSION_BASED")) return getNormalSecondsToAdd();

        return getPermissionBasedSecondsToAdd(player);
    }

    private static int getPermissionBasedSecondsToAdd(Player player) {
        List<String> permissionList = getMatchingPermissions(player);
        if(permissionList.isEmpty()) return getNormalSecondsToAdd();

        int cooldownSeconds = Integer.MAX_VALUE;
        for(String permission : permissionList) {
            String secondsString = permission.substring("enchantedapple.cooldown.timer.".length());
            try {
                int seconds = Integer.parseInt(secondsString);
                if(seconds < cooldownSeconds) cooldownSeconds = seconds;
            } catch(NumberFormatException ignored) {}
        }

        return (cooldownSeconds == Integer.MAX_VALUE ? getNormalSecondsToAdd() : cooldownSeconds);
    }

    private static int getNormalSecondsToAdd() {
        EnchantedAppleCooldown plugin = getPlugin();
        FileConfiguration config = plugin.getConfig();
        if(config == null) return 0;

        return config.getInt("default-timer", 30);
    }
}