package nl.matsgemmeke.battlegrounds.item.validation.validator;

import com.google.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.matsgemmeke.battlegrounds.sound.api.SoundService;
import nl.matsgemmeke.battlegrounds.item.validation.constraint.ValidSoundTemplate;

public class ValidSoundTemplateValidator implements ConstraintValidator<ValidSoundTemplate, String> {

    private final SoundService soundService;

    @Inject
    public ValidSoundTemplateValidator(SoundService soundService) {
        this.soundService = soundService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return soundService.getSoundSequence(value).isPresent();
    }
}
