package nl.matsgemmeke.battlegrounds.validation.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import nl.matsgemmeke.battlegrounds.validation.TestValidationObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

class HasWorldValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("isValid returns false when location has no world")
    void isValid_locationWithoutWorld() {
        TestValidationObject object = new TestValidationObject();
        object.hasWorld = new Location(null, 1, 1, 1);

        Set<ConstraintViolation<TestValidationObject>> violations = validator.validate(object);

        assertThat(violations).satisfiesExactly(violation -> {
            assertThat(violation.getMessage()).isEqualTo("location must have a world");
        });
    }

    static Set<Arguments> locationArguments() {
        return Set.of(arguments(new Location(mock(World.class), 1, 1, 1)));
    }

    @ParameterizedTest
    @MethodSource("locationArguments")
    @NullSource
    @DisplayName("isValid returns true when location has a world")
    void isValid_locationWithWorldOrNullLocation(Location location) {
        TestValidationObject object = new TestValidationObject();
        object.hasWorld = location;

        Set<ConstraintViolation<TestValidationObject>> violations = validator.validate(object);

        assertThat(violations).isEmpty();
    }
}
