package com.playdeca.elytration.Records;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public record Checkpoint(
        int zoneId,
        int checkpointId,
        World world,
        double x,
        double y,
        double z,
        double radius,
        Particle particle) {

    public boolean containsLocation(Location location) {
        if (location.getWorld().getName().equals(world().getName())) {
            double distance = location.distance(new Location(location.getWorld(), x(), y(), z()));
            return distance <= radius();
        }
        return false;
    }

    public Location toLocation() {
        return new Location(world(), x(), y(), z());
    }

    public Checkpoint setRadius(double radius) {
        return new Checkpoint(zoneId(), checkpointId(), world(), x(), y(), z(), radius, particle());
    }

}
