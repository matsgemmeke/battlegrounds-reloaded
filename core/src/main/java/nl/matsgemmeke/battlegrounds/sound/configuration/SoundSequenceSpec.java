package nl.matsgemmeke.battlegrounds.sound.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SoundSequenceSpec(
        @NotBlank(message = "sequence id must be a text value")
        String id,
        @NotEmpty(message = "sequence must have at least one entry")
        List<@Valid SoundSequenceEntrySpec> entries
) {
}
