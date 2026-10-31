package nl.matsgemmeke.battlegrounds.sound.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nl.matsgemmeke.battlegrounds.sound.validation.validator.ValidSoundValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSoundValidator.class)
public @interface ValidSound {

    String message() default "unknown Minecraft sound: ${validatedValue}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
