package nl.matsgemmeke.battlegrounds.arena.map.element;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElementRegistryTest {

    private ElementRegistry elementRegistry;

    @BeforeEach
    void setUp() {
        elementRegistry = new ElementRegistry();
    }

    @Test
    @DisplayName("generateNextId returns first id that is not yet in use by an element")
    void generateNextId() {
        Element element = mock(Element.class);
        when(element.getId()).thenReturn(1);

        elementRegistry.addElement(element);
        int nextId = elementRegistry.generateNextId();

        assertThat(nextId).isEqualTo(2);
    }
}
