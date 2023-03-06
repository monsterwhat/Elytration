package com.playdeca.elytration.Managers;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ElyManager {

    // Constructor to initialize the HashMap
    public ElyManager() {
        // HashMap to store player's zone data
        HashMap<UUID, PlayerZoneData> playerZoneDataMap = new HashMap<>();
    }

    // Method to check if a player is in a zone
    public void checkPlayerZone(Player player) {
        // TODO: Implement method
    }

    // Method to check if a player has passed through a checkpoint
    public void checkPlayerCheckpoint(Player player) {
        // TODO: Implement method
    }

    // Method to update a player's zone data
    public void updatePlayerZoneData(UUID playerId, int zoneId, int checkpointIndex, long time) {
        // TODO: Implement method
    }

    // Method to get a player's best time for a zone
    public long getPlayerBestTime(UUID playerId, int zoneId) {
        // TODO: Implement method
        return 0L;
    }

    // Record class to store a player's zone data
    private record PlayerZoneData(int zoneId, int currentCheckpoint, long bestTime, HashMap<Material, Boolean> whitelistedBlocks) {
        public PlayerZoneData {
            whitelistedBlocks = new HashMap<>();
        }

        public PlayerZoneData(int zoneId) {
            this(zoneId, 0, Long.MAX_VALUE, new HashMap<>());
        }

        public void addWhitelistedBlock(Material material) {
            whitelistedBlocks.put(material, true);
        }

        public void removeWhitelistedBlock(Material material) {
            whitelistedBlocks.remove(material);
        }
    }
}
