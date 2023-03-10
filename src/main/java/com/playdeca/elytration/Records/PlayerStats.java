package com.playdeca.elytration.Records;

import java.util.Objects;
import java.util.UUID;

public record PlayerStats(
        UUID playerId,
        int zoneId,
        int lastCheckpointIndex,
        long bestTime) {
    public PlayerStats {
        Objects.requireNonNull(playerId, "Player ID cannot be null");
    }

    public PlayerStats withZoneId(int zoneId) {
        return new PlayerStats(playerId, zoneId, lastCheckpointIndex, bestTime);
    }

    public PlayerStats withPlayerId(UUID playerId) {
        return new PlayerStats(playerId, zoneId, lastCheckpointIndex, bestTime);
    }

    public PlayerStats withPlayerIdAndZoneId(UUID playerId, int zoneId) {
        return new PlayerStats(playerId, zoneId, lastCheckpointIndex, bestTime);
    }

    public PlayerStats updateLastCheckpointIndex(int index) {
        return new PlayerStats(playerId(), zoneId(), index, bestTime());
    }

    public PlayerStats withUpdatedBestTime(long time) {
        return new PlayerStats(playerId(), zoneId(), lastCheckpointIndex(), time);
    }

    public PlayerStats withBestTime(long time) {
        return new PlayerStats(playerId(), zoneId(), lastCheckpointIndex(), time);
    }

    public PlayerStats withLastCheckpointIndex(int index) {
        return new PlayerStats(playerId(), zoneId(), index, bestTime());
    }


}