package nl.matsgemmeke.battlegrounds.arena.loading.element;

import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn.SpawnPointData;
import nl.matsgemmeke.battlegrounds.arena.map.element.Element;
import nl.matsgemmeke.battlegrounds.arena.map.element.SpawnPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompositeElementFactoryTest {

    @Mock
    private SpawnPointFactory spawnPointFactory;
    @InjectMocks
    private CompositeElementFactory compositeElementFactory;

    @Test
    void create_spawnPoint() {
        SpawnPointData spawnPointData = new SpawnPointData(null, null, null);
        SpawnPoint spawnPoint = mock(SpawnPoint.class);

        when(spawnPointFactory.create(spawnPointData)).thenReturn(spawnPoint);

        Element element = compositeElementFactory.create(spawnPointData);

        assertThat(element).isEqualTo(spawnPoint);
    }

    @Test
    void create_unsupportedElement() {
        UnsupportedElementData unsupportedElementData = new UnsupportedElementData();

        assertThatThrownBy(() -> compositeElementFactory.create(unsupportedElementData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot convert element data into element because type 'UnsupportedElementData' is not supported");
    }
}
