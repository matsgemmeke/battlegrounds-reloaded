package nl.matsgemmeke.battlegrounds.sound.validation.validator;

import com.google.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.matsgemmeke.battlegrounds.sound.adapter.SoundRegistryAdapter;
import nl.matsgemmeke.battlegrounds.sound.validation.constraint.ValidSound;

public class ValidSoundValidator implements ConstraintValidator<ValidSound, String> {

    private final SoundRegistryAdapter soundRegistryAdapter;

    @Inject
    public ValidSoundValidator(SoundRegistryAdapter soundRegistryAdapter) {
        this.soundRegistryAdapter = soundRegistryAdapter;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return soundRegistryAdapter.exists(value);
    }
}
