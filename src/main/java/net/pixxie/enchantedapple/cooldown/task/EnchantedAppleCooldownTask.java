package net.pixxie.enchantedapple.cooldown.task;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.api.utility.Util;
import net.pixxie.enchantedapple.cooldown.EnchantedAppleCooldown;
import net.pixxie.enchantedapple.cooldown.utility.EnchantedAppleCooldownManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EnchantedAppleCooldownTask extends BukkitRunnable {
    private final EnchantedAppleCooldown plugin;
    public EnchantedAppleCooldownTask(EnchantedAppleCooldown plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<Player> playerList = getCooldownPlayers();
        for(Player player : playerList) {
            if(checkEndCooldown(player)) continue;

            sendActionBar(player);
        }
    }

    private List<Player> getCooldownPlayers() {
        List<Player> playerList = Util.newList();

        Collection<? extends Player> onlinePlayerList = Bukkit.getOnlinePlayers();
        for(Player player : onlinePlayerList) {
            if(!EnchantedAppleCooldownManager.isInCooldown(player)) continue;

            playerList.add(player);
        }

        return playerList;
    }

    private String getDecimalTimeLeft(long millis) {
        double seconds = ((double) millis / 1_000.0D);

        DecimalFormat format = new DecimalFormat("0.0");
        return format.format(seconds);
    }

    private boolean checkEndCooldown(Player player) {
        if(player == null) return true;

        long timeLeftMillis = EnchantedAppleCooldownManager.getTimeLeftMillis(player);
        if(timeLeftMillis > 0) return false;

        EnchantedAppleCooldownManager.removeCooldown(player);
        String message = this.plugin.getConfigMessage("messages.action-bar.end-timer");
        if(message == null || message.isEmpty()) return true;

        NMS_Handler nmsHandler = NMS_Handler.getHandler();
        nmsHandler.sendActionBar(player, message);
        return true;
    }

    private void sendActionBar(Player player) {
        String message = this.plugin.getConfigMessage("messages.action-bar.timer");
        if(message == null || message.isEmpty()) return;

        long millisLeft = EnchantedAppleCooldownManager.getTimeLeftMillis(player);
        long secondsLeft = EnchantedAppleCooldownManager.getTimeLeftSeconds(player);

        String timeLeft = Long.toString(secondsLeft);
        String timeLeftDecimal = getDecimalTimeLeft(millisLeft);
        String actionBarMessage = message.replace("{time_left}", timeLeft).replace("{time_left_decimal}", timeLeftDecimal);

        NMS_Handler nmsHandler = NMS_Handler.getHandler();
        nmsHandler.sendActionBar(player, actionBarMessage);
    }
}