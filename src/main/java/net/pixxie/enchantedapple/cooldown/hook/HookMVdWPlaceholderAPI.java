package net.pixxie.enchantedapple.cooldown.hook;

import net.pixxie.enchantedapple.cooldown.EnchantedAppleCooldown;

import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class HookMVdWPlaceholderAPI implements PlaceholderHook, PlaceholderReplacer {
    private final EnchantedAppleCooldown plugin;
    public HookMVdWPlaceholderAPI(EnchantedAppleCooldown plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        PlaceholderAPI.registerPlaceholder(this.plugin, "enchantedapple_cooldown_time_left", this);
        PlaceholderAPI.registerPlaceholder(this.plugin, "enchantedapple_cooldown_time_left_decimal", this);
        return true;
    }

    @Override
    public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
        Player player = e.getPlayer();
        if(player == null) return null;

        String placeholder = e.getPlaceholder();
        if(!placeholder.startsWith("enchantedapple_cooldown_")) return null;

        String id = placeholder.substring("enchantedapple_cooldown_".length());
        return getPlaceholder(this.plugin, player, id);
    }
}