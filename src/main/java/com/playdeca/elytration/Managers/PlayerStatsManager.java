package com.playdeca.elytration.Managers;

import com.playdeca.elytration.Records.PlayerStats;
import com.playdeca.elytration.Records.Zone;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatsManager {
    private final Map<UUID, PlayerStats> playerStatsMap;
    private final ZoneManager zoneManager;

    public PlayerStatsManager(ZoneManager zoneManager) {
        playerStatsMap = new HashMap<>();
        this.zoneManager = zoneManager;
    }

    public void addPlayer(UUID playerId) {
        if (!playerStatsMap.containsKey(playerId)) {
            playerStatsMap.put(playerId, new PlayerStats(playerId, -1, -1, Long.MAX_VALUE));
        }
    }

    public void removePlayer(UUID playerId) {
        playerStatsMap.remove(playerId);
    }

    public void updatePlayerZone(UUID playerId, int zoneId) {
        PlayerStats playerStats = playerStatsMap.get(playerId);
        if (playerStats != null) {
            playerStatsMap.put(playerId, playerStats.withZoneId(zoneId));
        }
    }

    public int getPlayerZone(UUID playerId) {
        PlayerStats playerStats = playerStatsMap.get(playerId);
        return playerStats != null ? playerStats.zoneId() : -1;
    }

    public void updatePlayerCheckpoint(UUID playerId, int checkpointIndex) {
        PlayerStats playerStats = playerStatsMap.get(playerId);
        if (playerStats != null) {
            playerStatsMap.put(playerId, playerStats.updateLastCheckpointIndex(checkpointIndex));
        }
    }

    public int getPlayerLastCheckpoint(UUID playerId) {
        PlayerStats playerStats = playerStatsMap.get(playerId);
        return playerStats != null ? playerStats.lastCheckpointIndex() : -1;
    }

    public long getPlayerBestTime(UUID playerId, int zoneId) {
        PlayerStats playerStats = playerStatsMap.get(playerId);
        if (playerStats == null || playerStats.zoneId() != zoneId) {
            return Long.MAX_VALUE;
        }
        return playerStats.bestTime();
    }

    public void updatePlayerBestTime(UUID playerId, int zoneId, long time) {
        PlayerStats playerStats = playerStatsMap.get(playerId);
        if (playerStats == null || playerStats.zoneId() != zoneId || time >= playerStats.bestTime()) {
            return;
        }
        playerStatsMap.put(playerId, playerStats.withUpdatedBestTime(time));
    }

    public void checkPlayerZone(Player player) {
        // Get the player's current location
        Location playerLocation = player.getLocation();

        // Check if the player is in a zone
        for (Zone zone : zoneManager.getZones()) {
            if (zone.containsLocation(playerLocation)) {
                // Player is in a zone, update their current zone ID
                playerStatsMap.put(player.getUniqueId(), playerStatsMap.get(player.getUniqueId()).withZoneId(zone.id()));
                return;
            }
        }

        // Player is not in a zone, set their current zone ID to -1
        playerStatsMap.put(player.getUniqueId(), playerStatsMap.get(player.getUniqueId()).withZoneId(-1));
    }
}
