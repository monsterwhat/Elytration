package com.playdeca.elytration.Managers;

import com.playdeca.elytration.Records.Checkpoint;
import com.playdeca.elytration.Records.PlayerStats;
import com.playdeca.elytration.Records.Zone;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


import java.util.*;

public class ZoneManager {
    public final List<Zone> zones;
    public final Map<UUID, PlayerStats> playerStatsMap = new HashMap<>();

    public ZoneManager() {
        zones = new ArrayList<>();
    }

    public boolean addZone(Zone zone) {
        try{
            zones.add(zone);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean removeZone(int id) {
        try {
            zones.removeIf(zone -> zone.id() == id);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Zone getZone(int id) {
        return zones.stream().filter(zone -> zone.id() == id).findFirst().orElse(null);
    }

    public List<Zone> getZones() {
        return zones;
    }

    public boolean isCheckpoint(Location location) {
        for (Zone zone : zones) {
            if (zone.containsLocation(location)) {
                for (Checkpoint checkpoint : zone.checkpoints()) {
                    if (checkpoint.containsLocation(location)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public void updateZoneId(Location location, int zoneId) {
        for (Zone zone : zones) {
            if (zone.containsLocation(location)) {
                zone = zone.withLastCheckpointIndex(zoneId);
            }
        }
    }

    public void updatePlayerZone(Player player, Location playerLocation) {
        UUID playerId = player.getUniqueId();

        // Check if the player is in any of the zones
        for (Zone zone : zones) {
            if (zone.containsLocation(playerLocation)) {
                // Player is in a zone, update their current zone ID
                playerStatsMap.put(playerId, playerStatsMap.get(playerId).withZoneId(zone.id()));
                return;
            }
        }

        // Player is not in any of the zones, set their current zone ID to 0
        playerStatsMap.put(playerId, playerStatsMap.get(playerId).withZoneId(0));
    }

    public void setLastCheckpointIndex(Player player, int checkpointIndex) {
        UUID playerId = player.getUniqueId();
        PlayerStats playerStats = playerStatsMap.get(playerId);

        // Get the current zone that the player is in
        int zoneId = playerStats.zoneId();
        Zone currentZone = zones.stream()
                .filter(zone -> zone.id() == zoneId)
                .findFirst()
                .orElse(null);

        // Update the player's last checkpoint index for the current zone
        if (currentZone != null) {
            playerStatsMap.put(playerId, playerStats.withLastCheckpointIndex(checkpointIndex));
            currentZone.setLastCheckpointIndex(playerStats, checkpointIndex);
        }
    }

    public void resetPlayerStats(Player player) {
        playerStatsMap.remove(player.getUniqueId());
    }

    public PlayerStats getPlayerStats(UUID playerId) {
        return playerStatsMap.get(playerId);
    }

    public void addPlayerStats(UUID playerId, PlayerStats playerStats) {
        playerStatsMap.put(playerId, playerStats);
    }

    public void removePlayerStats(UUID playerId) {
        playerStatsMap.remove(playerId);
    }

    public boolean isPlayerInZone(Player player) {
        return playerStatsMap.containsKey(player.getUniqueId()) &&
                playerStatsMap.get(player.getUniqueId()).zoneId() != 0;
    }

    public org.bukkit.util.Vector getPlayerStartDirection(Player player) {
        UUID playerId = player.getUniqueId();
        int zoneId = playerStatsMap.get(playerId).zoneId();
        Zone currentZone = zones.stream()
                .filter(zone -> zone.id() == zoneId)
                .findFirst()
                .orElse(null);
        return currentZone != null ? currentZone.startDirection() : null;
    }

    public Location getPlayerStartLocation(Player player) {
        UUID playerId = player.getUniqueId();
        int zoneId = playerStatsMap.get(playerId).zoneId();
        Zone currentZone = zones.stream()
                .filter(it -> it.id() == zoneId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Zone not found for zoneId: " + zoneId));
        int startX = currentZone.startX();
        int startY = currentZone.startY();
        int startZ = currentZone.startZ();
        Location startLocation = new Location(currentZone.world(), startX, startY, startZ);
        // Rotate the start location in the direction specified by the zone
        Vector startDirection = currentZone.startDirection();
        if (startDirection != null) {
            startLocation.setDirection(startDirection);
        }

        return startLocation;
    }

    public void setPlayerLastCheckpoint(Player player, int checkpointIndex) {
        UUID playerId = player.getUniqueId();
        PlayerStats playerStats = playerStatsMap.get(playerId);
        int zoneId = playerStats.zoneId();
        Zone currentZone = zones.stream()
                .filter(it -> it.id() == zoneId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Zone not found for zoneId: " + zoneId));

        if (checkpointIndex > currentZone.checkpoints().size() - 1) {
            throw new IllegalArgumentException("Invalid checkpoint index: " + checkpointIndex);
        }

        playerStats = playerStats.updateLastCheckpointIndex(checkpointIndex);
        playerStatsMap.put(playerId, playerStats);
    }

    public void setPlayerBestTime(Player player, long time) {
        UUID playerId = player.getUniqueId();
        PlayerStats playerStats = playerStatsMap.get(playerId);
        playerStatsMap.put(playerId, playerStats.withBestTime(time));
    }

    public void savePlayerStats(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerStats playerStats = playerStatsMap.get(playerId);
        //playerStatsDao.savePlayerStats(playerStats);
    }

    public void loadPlayerStats(Player player) {
        UUID playerId = player.getUniqueId();
        //PlayerStats playerStats = playerStatsDao.loadPlayerStats(playerId);
        //playerStatsMap.put(playerId, playerStats);
    }

    public void loadAllPlayerStats() {
        playerStatsMap.clear();
        //List<PlayerStats> allPlayerStats = playerStatsDao.loadAllPlayerStats();
        //for (PlayerStats playerStats : allPlayerStats) {
        //playerStatsMap.put(playerStats.playerId(), playerStats);
        }

    public void clearPlayerStats(Player player) {
        UUID playerId = player.getUniqueId();
        playerStatsMap.remove(playerId);
    }

    public List<PlayerStats> getAllPlayerStats() {
        return new ArrayList<>(playerStatsMap.values());
    }

}
