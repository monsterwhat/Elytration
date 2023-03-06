package com.playdeca.elytration.Records;

import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public record Zone(int id, String name, String worldName,
                   int x1, int y1, int z1, int x2, int y2, int z2,
                   List<Checkpoint> checkpoints, Vector startDirection,
                   List<Material> whitelistedBlocks) {
    public Zone {
        Objects.requireNonNull(name, "Zone name cannot be null");
        Objects.requireNonNull(x1, "Zone x1 cannot be null");
        Objects.requireNonNull(y1, "Zone y1 cannot be null");
        Objects.requireNonNull(z1, "Zone z1 cannot be null");
        Objects.requireNonNull(x2, "Zone x2 cannot be null");
        Objects.requireNonNull(y2, "Zone y2 cannot be null");
        Objects.requireNonNull(z2, "Zone z2 cannot be null");
    }
}

