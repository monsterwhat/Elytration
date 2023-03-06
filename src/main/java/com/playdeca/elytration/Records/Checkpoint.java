package com.playdeca.elytration.Records;

import org.bukkit.Particle;

public record Checkpoint(
        int zoneId,
        int checkpointId,
        double x,
        double y,
        double z,
        double radius,
        Particle particle) {

}

