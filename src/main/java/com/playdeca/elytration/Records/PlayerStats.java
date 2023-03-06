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
}