package com.playdeca.elytration.Managers;

import com.playdeca.elytration.Records.Checkpoint;
import com.playdeca.elytration.Records.Zone;
import com.playdeca.elytration.Records.PlayerStats;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private final ElyManager elyManager;

    public CommandManager(ElyManager elyManager) {
        this.elyManager = elyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
            return true;
        }

        int zoneId;
        Zone zone = null;
        Material material;
        Checkpoint checkpoint;
        double radius;

        switch (command.getName().toLowerCase()) {

            case "addwhitelistedblock":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /addWhitelistedBlock <zone_id> <material>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone id must be a number.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone does not exist.");
                    return true;
                }

                try {
                    material = Material.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid material. Material does not exist.");
                    return true;
                }

                if (!zone.whitelistedBlocks().contains(material)) {
                    elyManager.addZoneWhitelistedBlock(zoneId, material);
                    sender.sendMessage(ChatColor.GREEN + "Block whitelisted successfully.");
                } else {
                    sender.sendMessage(ChatColor.RED + "That block is already whitelisted in the zone.");
                }

                break;

            case "removewhitelistedblock":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /removewhitelistedblock <zone_id> <material>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone id must be a number.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone does not exist.");
                    return true;
                }

                try {
                    material = Material.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid material. Material does not exist.");
                    return true;
                }

                if (!elyManager.removeZoneWhitelistedBlock(zoneId, material)) {
                    sender.sendMessage(ChatColor.RED + "Block is not whitelisted in the zone.");
                    return true;
                }

                sender.sendMessage(ChatColor.GREEN + "Block removed from whitelist successfully.");
                break;

            case "createzone":
                if (args.length < 6) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /createZone <id> <name> <world> <x1> <y1> <z1> [x2 y2 z2]");
                    return true;
                }

                int id;
                try {
                    id = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone id must be a number.");
                    return true;
                }

                String name = args[1];
                World world = Bukkit.getWorld(args[2]);
                if (world == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid world name.");
                    return true;
                }

                int x1, y1, z1, x2, y2, z2;
                try {
                    x1 = Integer.parseInt(args[3]);
                    y1 = Integer.parseInt(args[4]);
                    z1 = Integer.parseInt(args[5]);

                    if (args.length >= 9) {
                        x2 = Integer.parseInt(args[6]);
                        y2 = Integer.parseInt(args[7]);
                        z2 = Integer.parseInt(args[8]);
                    } else {
                        x2 = x1 + 10;
                        y2 = y1 + 10;
                        z2 = z1 + 10;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid coordinate format. Coordinates must be numbers.");
                    return true;
                }


                // Create a CuboidRegion object using two locations
                Location location1 = new Location(world, x1, y1, z1);
                Location location2 = new Location(world, x2, y2, z2);
                BlockVector3 min = BlockVector3.at(Math.min(location1.getBlockX(), location2.getBlockX()),
                        Math.min(location1.getBlockY(), location2.getBlockY()),
                        Math.min(location1.getBlockZ(), location2.getBlockZ()));
                BlockVector3 max = BlockVector3.at(Math.max(location1.getBlockX(), location2.getBlockX()),
                        Math.max(location1.getBlockY(), location2.getBlockY()),
                        Math.max(location1.getBlockZ(), location2.getBlockZ()));
                CuboidRegion region = new CuboidRegion(min, max);

                zone = new Zone(id, name, world, region,null,null,null,0,0,0);
                if (elyManager.getZoneManager().addZone(zone)) {
                    sender.sendMessage(ChatColor.GREEN + "Zone created successfully.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to create zone. Zone with that id already exists.");
                }

                break;

            case "deletezone":
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /deleteZone <id>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone id must be a number.");
                    return true;
                }

                if (elyManager.getZoneManager().removeZone(zoneId)) {
                    sender.sendMessage(ChatColor.GREEN + "Zone deleted successfully.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to delete zone. Zone with that id does not exist.");
                }

                break;

            case "listzones":
                ZoneManager zoneManager = elyManager.getZoneManager();
                Collection<Zone> zones = zoneManager.getZones();
                if (zones.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "No zones have been created yet.");
                    return true;
                }
                sender.sendMessage(ChatColor.YELLOW + "List of created zones:");
                for (Zone zone1 : zones) {
                    sender.sendMessage(String.format("%d: %s", zone1.id(), zone1.name()));
                }
                break;

            case "setstartcheckpoint":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /setStartCheckpoint <zone id> <checkpoint id>");
                    return true;
                }

                int checkpointId;
                try {
                    zoneId = Integer.parseInt(args[0]);
                    checkpointId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone or checkpoint id. Zone and checkpoint ids must be numbers.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone with that id does not exist.");
                    return true;
                }

                checkpoint = zone.getCheckpoint(checkpointId);
                if (checkpoint == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid checkpoint id. Checkpoint with that id does not exist in the specified zone.");
                    return true;
                }

                zone.setStartCheckpoint(checkpoint);
                sender.sendMessage(ChatColor.GREEN + "Start checkpoint set for zone " + zone.id() + ".");
                break;

            case "addcheckpoint":
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /addCheckpoint <zone_id>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone id must be a number.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone does not exist.");
                    return true;
                }

                Location location = ((Player) sender).getLocation();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();

                int checkpointNumber = zone.checkpoints().size() + 1;

                checkpoint = new Checkpoint(zoneId, checkpointNumber, zone.world(), x, y, z, 5, Particle.END_ROD);
                zone.addCheckpoint(checkpoint);
                sender.sendMessage(ChatColor.GREEN + "Checkpoint " + checkpointNumber + " added successfully.");

                break;

            case "createcheckpoint":
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /createCheckpoint <zone_id>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone id must be a number.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone does not exist.");
                    return true;
                }

                int newCheckpointId = zone.checkpoints().size() + 1;
                Location playerLocation = ((Player) sender).getLocation();
                checkpoint = new Checkpoint(zoneId, newCheckpointId, playerLocation.getWorld(),
                        playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ(), 5, Particle.END_ROD);
                zone.addCheckpoint(checkpoint);
                sender.sendMessage(ChatColor.GREEN + "Checkpoint created successfully.");
                break;


            case "deletecheckpoint":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /deleteCheckpoint <zone_id> <checkpoint_id>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                    checkpointId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone or checkpoint id. Zone id and checkpoint id must be numbers.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone does not exist.");
                    return true;
                }

                checkpoint = zone.getCheckpoint(checkpointId);
                if (checkpoint == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid checkpoint id. Checkpoint does not exist.");
                    return true;
                }

                zone.deleteCheckpoint(checkpoint);
                sender.sendMessage(ChatColor.GREEN + "Checkpoint deleted successfully.");

                break;

            case "listcheckpoints":
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /listCheckpoints <zone_id>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone id must be a number.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone does not exist.");
                    return true;
                }

                List<Checkpoint> checkpoints = zone.checkpoints();
                if (checkpoints.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "This zone has no checkpoints.");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Checkpoints in zone " + zone.name() + " (" + zoneId + "):");
                    for (Checkpoint checkpointList : checkpoints) {
                        sender.sendMessage("- " + checkpointList.toString());
                    }
                }

                break;

            case "modifycheckpoint":
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /modifyCheckpoint <zone_id> <checkpoint_id> <radius>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                    checkpointId = Integer.parseInt(args[1]);
                    radius = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid parameter format. Zone id and checkpoint id must be numbers. Radius must be a decimal number.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone does not exist.");
                    return true;
                }

                checkpoint = zone.getCheckpoint(checkpointId);
                if (checkpoint == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid checkpoint id. Checkpoint does not exist in zone " + zone.name() + " (" + zoneId + ").");
                    return true;
                }

                checkpoint.setRadius(radius);
                zone.updateCheckpoint(checkpoint);

                sender.sendMessage(ChatColor.GREEN + "Checkpoint " + checkpointId + " in zone " + zone.name() + " (" + zoneId + ") modified successfully.");

                break;

            case "teleporttocheckpoint":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /teleportToCheckpoint <zone_id> <checkpoint_id>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone id must be a number.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone does not exist.");
                    return true;
                }

                try {
                    checkpointId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid checkpoint id. Checkpoint id must be a number.");
                    return true;
                }

                checkpoint = zone.getCheckpoint(checkpointId);
                if (checkpoint == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid checkpoint id. Checkpoint does not exist.");
                    return true;
                }

                Player player = (Player) sender;
                player.teleport(checkpoint.toLocation());
                player.sendMessage(ChatColor.GREEN + "Teleported to checkpoint " + checkpoint.checkpointId() + ".");

                break;

            case "resetzoneprogress":
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /resetZoneProgress <zone_id>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone id must be a number.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone does not exist.");
                    return true;
                }

                for (PlayerStats playerStats : elyManager.getZoneManager().getAllPlayerStats()) {
                    if (playerStats.zoneId() == zone.id()) {
                        elyManager.getZoneManager().removePlayerStats(playerStats.playerId());
                    }
                }

                sender.sendMessage(ChatColor.GREEN + "Zone progress reset successfully.");

                break;

            case "resetzone":
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /resetZone <zone_id>");
                    return true;
                }

                try {
                    zoneId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone id must be a number.");
                    return true;
                }

                zone = elyManager.getZoneManager().getZone(zoneId);
                if (zone == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid zone id. Zone does not exist.");
                    return true;
                }

                elyManager.getZoneManager().removeZone(zone.id());
                sender.sendMessage(ChatColor.GREEN + "Zone removed successfully.");

                break;

            case "liststats":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
                    return true;
                }
                // TODO: listStats implementation
                break;

            case "liststatsall":
                sender.sendMessage(ChatColor.GOLD + "===== Zone Stats for All Players =====");
                // TODO: list all players' stats for each zone they have played in
                return true;

            default:
                sender.sendMessage(ChatColor.RED + "Invalid command.");
                break;
        }

        if(zone != null){
            //elyManager.getZoneManager().saveZone(zone);
        }

        return true;
    }
}
