package nl.matsgemmeke.battlegrounds.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.matsgemmeke.battlegrounds.validation.constraint.HasWorld;
import org.bukkit.Location;

public class HasWorldValidator implements ConstraintValidator<HasWorld, Location> {

    @Override
    public boolean isValid(Location location, ConstraintValidatorContext context) {
        return location == null || location.getWorld() != null;
    }
}
