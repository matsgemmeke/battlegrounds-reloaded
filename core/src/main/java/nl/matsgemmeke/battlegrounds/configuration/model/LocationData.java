package nl.matsgemmeke.battlegrounds.configuration.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * A configuration representation of a Bukkit location.
 */
public record LocationData(
        @NotBlank(message = "locations in configurations must have a defined world")
        String world,
        @NotNull
        Double x,
        @NotNull
        Double y,
        @NotNull
        Double z,
        @NotNull
        Float yaw,
        @NotNull
        Float pitch
) {
}
