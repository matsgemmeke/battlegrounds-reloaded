package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElementDataFactoryTest {

    private ElementDataFactory elementDataFactory;

    @BeforeEach
    void setUp() {
        elementDataFactory = new ElementDataFactory();
    }

    @Test
    @DisplayName("create returns ElementData result from ElementDataReader that corresponds with the given element type")
    void create() {
        ElementData elementData = mock(ElementData.class);
        Section section = mock(Section.class);

        ElementDataReader elementDataReader = mock(ElementDataReader.class);
        when(elementDataReader.read(section)).thenReturn(elementData);

        elementDataFactory.addElementDataReader(ElementType.SPAWN_POINT, elementDataReader);
        ElementData result = elementDataFactory.create(ElementType.SPAWN_POINT, section);

        assertThat(result).isEqualTo(elementData);
    }
}
