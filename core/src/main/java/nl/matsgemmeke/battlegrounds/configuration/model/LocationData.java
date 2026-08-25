package nl.matsgemmeke.battlegrounds.configuration.model;

import jakarta.validation.constraints.NotBlank;

/**
 * A configuration representation of a Bukkit location.
 */
public record LocationData(@NotBlank String world, double x, double y, double z, float yaw, float pitch) {
}
