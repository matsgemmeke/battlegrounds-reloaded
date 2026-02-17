package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.item.effect.ItemEffectSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PolymorphicTypeRegistryTest {

    @Test
    @DisplayName("get returns empty optional when base type is not registered")
    void resolve_baseTypeNotRegistered() {
        Optional<PolymorphicDefinition> definitionOptional = PolymorphicTypeRegistry.get(Object.class);

        assertThat(definitionOptional).isEmpty();
    }

    @Test
    @DisplayName("get returns optional with polymorphic definition when base type is registered")
    void resolve_baseTypeRegistered() {
        Optional<PolymorphicDefinition> definitionOptional = PolymorphicTypeRegistry.get(ItemEffectSpec.class);

        assertThat(definitionOptional).isNotEmpty();
    }
}
