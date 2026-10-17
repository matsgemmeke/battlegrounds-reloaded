package nl.matsgemmeke.battlegrounds.sound.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SoundTemplateSpec(
        @NotBlank(message = "template id must be a text value")
        String id,
        @NotEmpty(message = "template must have at least one entry")
        List<@Valid SoundTemplateEntrySpec> entries
) {
}
