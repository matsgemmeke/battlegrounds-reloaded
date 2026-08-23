package nl.matsgemmeke.battlegrounds.configuration.model;

import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

/**
 * A configuration representation of a Bukkit location.
 */
public record LocationData(@Required String world, double x, double y, double z, float yaw, float pitch) {
}
