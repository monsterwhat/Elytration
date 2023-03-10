package com.playdeca.elytration.Managers;

import com.playdeca.elytration.Records.Zone;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ElyManager {

    private PlayerStatsManager playerStatsManager;
    private WhitelistManager whitelistManager;
    private ZoneManager zoneManager;

    // Constructor to initialize the managers and HashMaps
    public ElyManager() {
        // HashMaps to store player's zone data and whitelisted blocks for each zone
        HashMap<UUID, PlayerZoneData> playerZoneDataMap = new HashMap<>();
        HashMap<Integer, HashMap<Material, Boolean>> zoneWhitelistedBlocksMap = new HashMap<>();

        // Managers for zones, players, checkpoints, and whitelisted blocks
        zoneManager = new ZoneManager();
        playerStatsManager = new PlayerStatsManager(zoneManager);
        CheckpointManager checkpointManager = new CheckpointManager();
        whitelistManager = new WhitelistManager();
    }

    // Method to check if a player is in a zone
    public void checkPlayerZone(Player player) {
        playerStatsManager.checkPlayerZone(player);
    }

    // Method to check if a player has passed through a checkpoint
    public void checkPlayerCheckpoint(Player player) {
        playerStatsManager.checkPlayerZone(player);
    }

    // Method to update a player's zone data
    public void updatePlayerZoneData(UUID playerId, Zone zone) {
        playerStatsManager.updatePlayerZone(playerId, zone.id());
    }

    // Method to get a player's best time for a zone
    public long getPlayerBestTime(UUID playerId, int zoneId) {
        return playerStatsManager.getPlayerBestTime(playerId, zoneId);
    }

    // Method to add a whitelisted block for a zone
    public void addZoneWhitelistedBlock(int zoneId, Material material) {
        whitelistManager.addWhitelistedBlock(zoneId, material);
    }

    // Method to remove a whitelisted block from a zone
    public boolean removeZoneWhitelistedBlock(int zoneId, Material material) {
        try {
            whitelistManager.removeWhitelistedBlock(zoneId, material);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ZoneManager getZoneManager() {
        return this.zoneManager;
    }


    // Inner class to store a player's zone data
    private static class PlayerZoneData {
        private final int zoneId;
        private int currentCheckpoint;
        private long bestTime;

        public PlayerZoneData(int zoneId) {
            this.zoneId = zoneId;
            this.currentCheckpoint = 0;
            this.bestTime = Long.MAX_VALUE;
        }

        public int getZoneId() {
            return zoneId;
        }

        public int getCurrentCheckpoint() {
            return currentCheckpoint;
        }

        public void setCurrentCheckpoint(int currentCheckpoint) {
            this.currentCheckpoint = currentCheckpoint;
        }

        public long getBestTime() {
            return bestTime;
        }

        public void setBestTime(long bestTime) {
            this.bestTime = bestTime;
        }


    }
}
