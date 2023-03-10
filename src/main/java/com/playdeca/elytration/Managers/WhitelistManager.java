package com.playdeca.elytration.Managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

public class WhitelistManager {
    private final Map<Integer, Set<Material>> zoneWhitelists;

    public WhitelistManager() {
        zoneWhitelists = new HashMap<>();
    }

    public void addZone(int zoneId) {
        if (!zoneWhitelists.containsKey(zoneId)) {
            zoneWhitelists.put(zoneId, new HashSet<>());
        }
    }

    public void removeZone(int zoneId) {
        zoneWhitelists.remove(zoneId);
    }

    public void addWhitelistedBlock(int zoneId, Material material) {
        Set<Material> whitelist = zoneWhitelists.get(zoneId);
        if (whitelist != null) {
            whitelist.add(material);
        }
    }

    public void removeWhitelistedBlock(int zoneId, Material material) {
        Set<Material> whitelist = zoneWhitelists.get(zoneId);
        if (whitelist != null) {
            whitelist.remove(material);
        }
    }

    public boolean isBlockWhitelisted(int zoneId, Material material) {
        Set<Material> whitelist = zoneWhitelists.get(zoneId);
        return whitelist != null && whitelist.contains(material);
    }
}

