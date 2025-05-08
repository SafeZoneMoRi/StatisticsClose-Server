package io.morningmc.plugin.statisticsClose;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class StatisticsClose extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        double interval = getConfig().getDouble("interval-minutes", 5.0);
        long ticks = (long) (interval * 60 * 20);

        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            File statsFolder = new File(getServer().getWorldContainer(), "world/stats");
            if (statsFolder.exists() && statsFolder.isDirectory()) {
                File[] files = statsFolder.listFiles((dir, name) -> name.endsWith(".json"));
                if (files != null) {
                    for (File file : files) {
                        if (file.delete()) {
                            getLogger().info("Deleted stat file: " + file.getName());
                        } else {
                            getLogger().warning("Failed to delete stat file: " + file.getName());
                        }
                    }
                }
            } else {
                getLogger().warning("Stats folder not found at: " + statsFolder.getPath());
            }
        }, 20L, ticks);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        resetStats(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        resetStats(event.getPlayer());
    }

    private void resetStats(Player player) {
        for (Statistic stat : Statistic.values()) {
            try {
                if (stat.getType() == Statistic.Type.UNTYPED) {
                    player.setStatistic(stat, 0);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}

