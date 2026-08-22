package nl.matsgemmeke.battlegrounds.configuration.model;

/**
 * A configuration representation of a Bukkit location.
 */
public record LocationData(String world, double x, double y, double z, float yaw, float pitch) {
}
