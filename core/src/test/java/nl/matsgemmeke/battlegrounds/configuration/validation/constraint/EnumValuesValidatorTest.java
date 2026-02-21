package nl.matsgemmeke.battlegrounds.configuration.validation.constraint;

import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationContext;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationException;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumValuesValidatorTest {

    private final EnumValuesValidator validator = new EnumValuesValidator();

    @Test
    @DisplayName("validate does nothing when field value is null")
    void validate_fieldValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        EnumValues annotation = object.getClass().getDeclaredField("enumValues").getAnnotation(EnumValues.class);
        ValidationContext context = new ValidationContext("enumValues", null, Map.of());

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validate throws ValidationException when field value is an invalid type")
    void validate_invalidType() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        EnumValues annotation = object.getClass().getDeclaredField("enumValuesInvalidType").getAnnotation(EnumValues.class);
        ValidationContext context = new ValidationContext("enumValues", "invalid", Map.of());

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Value must be a collection, but was a String");
    }

    @Test
    @DisplayName("validate throws ValidationException when any entry in the field is not a valid value of a small enum")
    void validate_anyInvalidEntrySmallEnum() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        EnumValues annotation = object.getClass().getDeclaredField("enumValues").getAnnotation(EnumValues.class);
        ValidationContext context = new ValidationContext("enumValues", List.of("HEAD", "invalid"), Map.of());

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid value 'invalid' for enum HitboxComponentType. Allowed values: [HEAD, TORSO, LIMBS]");
    }

    @Test
    @DisplayName("validate throws ValidationException when any entry in the field is not a valid value of a large enum")
    void validate_anyInvalidEntryLargeEnum() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        EnumValues annotation = object.getClass().getDeclaredField("enumValuesLargeEnum").getAnnotation(EnumValues.class);
        ValidationContext context = new ValidationContext("enumValuesLargeEnum", List.of("FLAME", "invalid"), Map.of());

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid value 'invalid' for enum Particle. Allowed values: [EXPLOSION_NORMAL, EXPLOSION_LARGE, EXPLOSION_HUGE, FIREWORKS_SPARK, WATER_BUBBLE, WATER_SPLASH, WATER_WAKE, SUSPENDED, SUSPENDED_DEPTH, CRIT] ... (92 more)");
    }

    @Test
    @DisplayName("validate does nothing when all entries in the field are valid enum values")
    void validate_allValidEntries() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        EnumValues annotation = object.getClass().getDeclaredField("enumValues").getAnnotation(EnumValues.class);
        ValidationContext context = new ValidationContext("enumValues", List.of("HEAD", "TORSO"), Map.of());

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }
}
