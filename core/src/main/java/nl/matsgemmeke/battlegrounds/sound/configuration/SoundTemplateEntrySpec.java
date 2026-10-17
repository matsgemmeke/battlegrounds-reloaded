package nl.matsgemmeke.battlegrounds.sound.configuration;

import jakarta.validation.constraints.*;
import nl.matsgemmeke.battlegrounds.sound.validation.constraint.ValidSound;

public record SoundTemplateEntrySpec(
        @NotBlank(message = "sound must not be blank")
        @ValidSound
        String sound,
        @PositiveOrZero(message = "volume must be 0 or higher (was ${validatedValue})")
        float volume,
        float pitch,
        @PositiveOrZero(message = "delay must be 0 or higher (was ${validatedValue})")
        int delay
) {
}
