package com.playdeca.elytration.Managers;

import org.bukkit.Location;

import java.util.HashMap;

public class CheckpointManager {
    private final HashMap<Integer, Checkpoint> checkpoints;

    public CheckpointManager() {
        checkpoints = new HashMap<>();
    }

    public void addCheckpoint(int index, Location location) {
        checkpoints.put(index, new Checkpoint(location));
    }

    public Checkpoint getCheckpoint(int index) {
        return checkpoints.get(index);
    }

    public static record Checkpoint(Location location) {}
}

