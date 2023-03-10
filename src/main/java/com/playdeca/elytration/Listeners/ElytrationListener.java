package com.playdeca.elytration.Listeners;

import com.playdeca.elytration.Records.Checkpoint;
import com.playdeca.elytration.Records.PlayerStats;
import com.playdeca.elytration.Records.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class ElytrationListener implements Listener {

    private final Map<UUID, PlayerStats> playerStatsMap;
    private final Map<UUID, Long> lastFlightEndTimeMap;

    public ElytrationListener(Map<UUID, PlayerStats> playerStatsMap, Map<UUID, Long> lastFlightEndTimeMap) {
        this.playerStatsMap = playerStatsMap;
        this.lastFlightEndTimeMap = lastFlightEndTimeMap;
    }

    @EventHandler
    public void onPlayerToggleGlide(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        // Check if the player is gliding
        if (!player.isGliding()) {
            return;
        }

        // Check if the player is in a world with elytration enabled
        if (!ElytrationUtils.isElytrationWorld(player.getWorld())) {
            return;
        }

        // Check if the player is in a zone
        Zone zone = ElytrationUtils.getZoneAtLocation(player.getLocation());
        if (zone == null) {
            return;
        }

        // Get the player's current stats and last flight end time
        PlayerStats playerStats = playerStatsMap.get(player.getUniqueId());
        long lastFlightEndTime = lastFlightEndTimeMap.getOrDefault(player.getUniqueId(), 0L);

        // Check if the player is starting a new flight
        if (!event.isFlying()) {
            // Set the player's last checkpoint to the start checkpoint of the zone
            Checkpoint startCheckpoint = zone.getCheckpoint(0);
            playerStats = playerStats.withZoneId(zone.id()).withLastCheckpointIndex(0);
            player.sendMessage(ChatColor.GREEN + "Elytra flight started!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
            lastFlightEndTimeMap.put(player.getUniqueId(), currentTime);
            return;
        }

        // Get the player's current checkpoint index
        int lastCheckpointIndex = playerStats.lastCheckpointIndex();

        // Check if the player has passed through a new checkpoint
        for (int i = lastCheckpointIndex + 1; i < zone.checkpoints().size(); i++) {
            Checkpoint checkpoint = zone.checkpoints().get(i);
            if (checkpoint.containsLocation(player.getLocation())) {
                playerStats = playerStats.withLastCheckpointIndex(i);
                player.sendMessage(ChatColor.GREEN + "Checkpoint " + i + " reached!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                break;
            }
        }

        // Check if the player has finished the zone
        Checkpoint finishCheckpoint = zone.getCheckpoint(zone.checkpoints().size() - 1);
        if (finishCheckpoint.containsLocation(player.getLocation())) {
            // Calculate the player's time and best time
            long currentTimeMs = currentTime - lastFlightEndTime;
            long bestTimeMs = playerStats.bestTime() == 0 ? currentTimeMs : Math.min(currentTimeMs, playerStats.bestTime());

            // Update the player's stats and display the results
            playerStats = playerStats.withUpdatedBestTime(bestTimeMs);
            player.sendMessage(ChatColor.GREEN + "Finished zone \"" + zone.name() + "\" in " + ElytrationUtils.formatTime(currentTimeMs) + "!");
            // Update the player's stats in the map
            playerStatsMap.put(player.getUniqueId(), playerStats);

            // Send a message to the player
            player.sendMessage(ChatColor.GREEN + "Checkpoint set!");

            // Play a sound and particle effect at the checkpoint location
            player.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
            location.getWorld().spawnParticle(Particle.END_ROD, location, 100, 0.5, 0.5, 0.5);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Check if the player is currently playing Elytration
        if (isPlayingElytration(player)) {
            // Get the player's current stats
            PlayerStats playerStats = playerStatsMap.get(player.getUniqueId());

            // Check if the player has set a checkpoint in this zone
            if (playerStats.lastCheckpointIndex() > 0) {
                // Get the last checkpoint the player reached
                Checkpoint lastCheckpoint = zone.getCheckpoint(playerStats.lastCheckpointIndex());

                // Teleport the player back to the last checkpoint
                player.teleport(lastCheckpoint.toLocation());

                // Send a message to the player
                player.sendMessage(ChatColor.RED + "You died! Respawning at your last checkpoint.");

                // Play a sound and particle effect at the checkpoint location
                player.playSound(lastCheckpoint.toLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);
                lastCheckpoint.world().spawnParticle(Particle.END_ROD, lastCheckpoint.toLocation(), 100, 0.5, 0.5, 0.5);
            } else {
                // Teleport the player back to the start of the zone
                Vector startDirection = zone.startDirection();
                Location startLocation = new Location(zone.world(), zone.startX(), zone.startY(), zone.startZ());
                player.teleport(startLocation);

                // Send a message to the player
                player.sendMessage(ChatColor.RED + "You died! Respawning at the start of the zone.");

                // Play a sound and particle effect at the start location
                player.playSound(startLocation, Sound.ENTITY_PLAYER_HURT, 1f, 1f);
                startLocation.getWorld().spawnParticle(Particle.END_ROD, startLocation, 100, 0.5, 0.5, 0.5);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Remove the player's stats from the map when they quit
        playerStatsMap.remove(player.getUniqueId());
    }

    private boolean isPlayingElytration(Player player) {
        // Check if the player is inside a zone and playing Elytration
        for (Zone zone : zones) {
            if (zone.containsLocation(player.getLocation())) {
                return true;
            }
        }
        return false;
    }
}
