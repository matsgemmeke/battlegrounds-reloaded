package nl.matsgemmeke.battlegrounds.configuration.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.ValidDropping;

public class ValidDroppingValidator implements ConstraintValidator<ValidDropping, EquipmentSpec> {

    private static final String MISSING_DROPPING_PROPERTIES = "drop controls are enabled, therefore a configuration for deploy.dropping is required";
    private static final String MISSING_DROP_ITEM = "drop controls are enabled, therefore a configuration for items.drop-item is required";

    @Override
    public boolean isValid(EquipmentSpec spec, ConstraintValidatorContext constraintValidatorContext) {
        if (spec.controls.drop == null) {
            return true;
        }

        if (spec.deploy.dropping == null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(MISSING_DROPPING_PROPERTIES)
                    .addPropertyNode("deploy.dropping")
                    .addConstraintViolation();

            return false;
        }

        if (spec.items.dropItem == null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(MISSING_DROP_ITEM)
                    .addPropertyNode("items.dropItem")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
