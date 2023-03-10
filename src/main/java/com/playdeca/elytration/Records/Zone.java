package com.playdeca.elytration.Records;

import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Zone(int id, String name, World world,
                   CuboidRegion region,
                   List<Checkpoint> checkpoints, Vector startDirection,
                   List<Material> whitelistedBlocks,
                   int startX, int startY, int startZ
) {
    public Zone {
        Objects.requireNonNull(name, "Zone name cannot be null");
    }

    public boolean containsLocation(Location location) {
        // Check if the location is inside the bounding box of the zone
        if (location.getWorld().getName().equals(world.getName()) && region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {

            // Check if the location is inside any of the checkpoints
            for (Checkpoint checkpoint : checkpoints) {
                if (checkpoint.containsLocation(location)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Zone withLastCheckpointIndex(int index) {
        return new Zone(id, name, world, region, checkpoints, startDirection, whitelistedBlocks, startX, startY, startZ);
    }

    public void setLastCheckpointIndex(PlayerStats playerStats, int checkpointIndex) {
        playerStats = playerStats.updateLastCheckpointIndex(checkpointIndex);
    }

    // getCheckpoint method in Zone class
    public Checkpoint getCheckpoint(int checkpointId) {
        for (Checkpoint checkpoint : checkpoints) {
            if (checkpoint.checkpointId() == checkpointId) {
                return checkpoint;
            }
        }
        return null;
    }

    public Zone addCheckpoint(Checkpoint checkpoint) {
        List<Checkpoint> newCheckpoints = new ArrayList<>(checkpoints);
        newCheckpoints.add(checkpoint);

        return new Zone(id, name, world, region, newCheckpoints, startDirection, whitelistedBlocks, startX, startY, startZ);
    }


    public void setStartCheckpoint(Checkpoint location) {
        checkpoints.add(0, new Checkpoint(0, 0, location.world(), location.x(), location.y(), location.z(), 5.0, Particle.END_ROD));
    }

    public void deleteCheckpoint(Checkpoint checkpoint) {
        checkpoints.remove(checkpoint);
    }

    public void updateCheckpoint(Checkpoint checkpoint) {
        checkpoints.remove(checkpoint);
        checkpoints.add(checkpoint);
    }

}
