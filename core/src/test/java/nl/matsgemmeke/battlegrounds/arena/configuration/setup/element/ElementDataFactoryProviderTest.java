package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element;

import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn.SpawnPointDataReader;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ElementDataFactoryProviderTest {

    @Mock
    private SpawnPointDataReader spawnPointDataReader;
    @InjectMocks
    private ElementDataFactoryProvider provider;

    @Test
    @DisplayName("get returns ElementDataFactory instance with data readers added")
    void get() {
        Section section = mock(Section.class);

        ElementDataFactory factory = provider.get();
        factory.create(ElementType.SPAWN_POINT, section);

        verify(spawnPointDataReader).read(section);
    }
}
