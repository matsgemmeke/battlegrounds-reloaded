package nl.matsgemmeke.battlegrounds.sound.configuration;

import jakarta.validation.constraints.*;
import nl.matsgemmeke.battlegrounds.sound.validation.constraint.ValidSound;

public record SoundCueSpec(
        @NotBlank(message = "sound must not be blank")
        @ValidSound
        String sound,
        @PositiveOrZero(message = "volume must be 0 or higher (was ${validatedValue})")
        float volume,
        @DecimalMin(value = "0.5", message = "pitch must be at least {value} (was ${validatedValue})")
        @DecimalMax(value = "2.0", message = "pitch must be at most {value} (was ${validatedValue})")
        float pitch,
        @PositiveOrZero(message = "delay must be 0 or higher (was ${validatedValue})")
        int delay
) {
}
