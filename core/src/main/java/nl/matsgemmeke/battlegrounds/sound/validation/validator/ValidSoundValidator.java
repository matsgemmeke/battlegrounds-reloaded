package nl.matsgemmeke.battlegrounds.sound.validation.validator;

import com.google.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.matsgemmeke.battlegrounds.sound.BukkitSoundRegistry;
import nl.matsgemmeke.battlegrounds.sound.validation.constraint.ValidSound;

public class ValidSoundValidator implements ConstraintValidator<ValidSound, String> {

    private final BukkitSoundRegistry bukkitSoundRegistry;

    @Inject
    public ValidSoundValidator(BukkitSoundRegistry bukkitSoundRegistry) {
        this.bukkitSoundRegistry = bukkitSoundRegistry;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return bukkitSoundRegistry.exists(value);
    }
}
