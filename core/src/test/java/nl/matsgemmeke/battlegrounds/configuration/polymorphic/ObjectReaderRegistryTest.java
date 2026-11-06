package nl.matsgemmeke.battlegrounds.configuration.polymorphic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ObjectReaderRegistryTest {

    private ObjectReaderRegistry<AnimalType, AnimalSpec> objectReaderRegistry;

    @BeforeEach
    void setUp() {
        objectReaderRegistry = new ObjectReaderRegistry<>();
    }

    @Test
    @DisplayName("getSectionReader returns empty optional when given animal type is not registered")
    void getSectionReader_notRegistered() {
        ObjectReader<AnimalSpec> objectReader = mock();

        objectReaderRegistry.register(AnimalType.CAT, objectReader);
        Optional<ObjectReader<AnimalSpec>> objectReaderOptional = objectReaderRegistry.getSectionReader(AnimalType.DOG);

        assertThat(objectReaderOptional).isEmpty();
    }

    @Test
    @DisplayName("getSectionReader returns optional with corresponding object reader")
    void getSectionReader_successful() {
        ObjectReader<AnimalSpec> objectReader = mock();

        objectReaderRegistry.register(AnimalType.CAT, objectReader);
        Optional<ObjectReader<AnimalSpec>> objectReaderOptional = objectReaderRegistry.getSectionReader(AnimalType.CAT);

        assertThat(objectReaderOptional).hasValue(objectReader);
    }
}
