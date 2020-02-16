package net.pixxie.enchantedapple.cooldown.hook;

import java.text.DecimalFormat;

import net.pixxie.enchantedapple.cooldown.EnchantedAppleCooldown;
import net.pixxie.enchantedapple.cooldown.utility.EnchantedAppleCooldownManager;

import org.bukkit.entity.Player;

public interface PlaceholderHook {
    default String getPlaceholder(EnchantedAppleCooldown plugin, Player player, String id) {
        if(plugin == null || player == null || id == null || id.isEmpty()) return null;

        switch(id) {
            case "time_left": return getTimeLeft(plugin, player);
            case "time_left_decimal": return getTimeLeftDecimal(plugin, player);

            default: return null;
        }
    }

    default String getTimeLeft(EnchantedAppleCooldown plugin, Player player) {
        long secondsLeft = EnchantedAppleCooldownManager.getTimeLeftSeconds(player);
        if(secondsLeft <= 0) return getZeroTimeLeft(plugin, player);

        return Long.toString(secondsLeft);
    }

    default String getTimeLeftDecimal(EnchantedAppleCooldown plugin, Player player) {
        double millisLeft = EnchantedAppleCooldownManager.getTimeLeftMillis(player);
        if(millisLeft <= 0) return getZeroTimeLeft(plugin, player);

        double secondsLeft = (millisLeft / 1_000.0D);
        DecimalFormat format = new DecimalFormat("0.0");

        return format.format(secondsLeft);
    }

    default String getZeroTimeLeft(EnchantedAppleCooldown plugin, Player player) {
        String message = plugin.getConfigMessage("messages.zero-time-left");
        return (message == null || message.isEmpty() ? "0" : message);
    }

    boolean register();
}