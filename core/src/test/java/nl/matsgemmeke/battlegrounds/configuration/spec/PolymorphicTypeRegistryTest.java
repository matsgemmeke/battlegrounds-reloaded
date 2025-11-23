package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.item.effect.DamageEffectSpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PolymorphicTypeRegistryTest {

    @Test
    void resolveReturnsEmptyOptionalWhenNoRuleForGivenKeyNameIsRegistered() {
        Optional<Class<?>> type = PolymorphicTypeRegistry.resolve("unregistered-type", "DAMAGE");

        assertThat(type).isEmpty();
    }

    @Test
    void resolveReturnsEmptyOptionalWhenNoRuleForGivenValueNameIsRegistered() {
        Optional<Class<?>> type = PolymorphicTypeRegistry.resolve("effect-type", "UNKNOWN");

        assertThat(type).isEmpty();
    }

    @Test
    void resolveReturnsOptionalWithPolymorphicTypeRegisteredForGivenKeyNameAndValueName() {
        Optional<Class<?>> type = PolymorphicTypeRegistry.resolve("effect-type", "DAMAGE");

        assertThat(type).hasValue(DamageEffectSpec.class);
    }
}
