package nl.matsgemmeke.battlegrounds.arena.map;

import nl.matsgemmeke.battlegrounds.arena.map.element.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArenaMapTest {

    private static final String NAME = "Level 1";
    private static final Instant CREATED_AT = Instant.parse("2026-05-30T18:00:00Z");
    private static final UUID CREATED_BY = UUID.fromString("2c11afe2-48f0-4399-9a04-195bb8ac640e");

    private ArenaMap map;

    @BeforeEach
    void setUp() {
        map = new ArenaMap(NAME, new ArenaMapMetadata(CREATED_AT, CREATED_BY));
    }

    @Test
    @DisplayName("generateNextId returns first id that is not yet in use by an element")
    void generateNextId() {
        Element element = mock(Element.class);
        when(element.getId()).thenReturn(1);

        map.addElement(element);
        int nextId = map.generateNextElementId();

        assertThat(nextId).isEqualTo(2);
    }
}
