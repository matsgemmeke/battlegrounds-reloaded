package nl.matsgemmeke.battlegrounds.arena.map.selection;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ArenaMapSelectorTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();

    private ArenaMapSelector mapSelector;

    @BeforeEach
    void setUp() {
        mapSelector = new ArenaMapSelector();
    }

    @Test
    @DisplayName("getSelection returns empty optional when given player id has no selected map")
    void getSelection_noSelection() {
        Arena arena = mock(Arena.class);
        ArenaMap map = mock(ArenaMap.class);
        ArenaMapSelection selection = new ArenaMapSelection(arena, map);

        mapSelector.select(PLAYER_ID, selection);
        mapSelector.deselect(PLAYER_ID);
        Optional<ArenaMapSelection> selectionOptional = mapSelector.getSelection(PLAYER_ID);

        assertThat(selectionOptional).isEmpty();
    }

    @Test
    @DisplayName("getSelection returns optional with corresponding map selection")
    void getSelection_successful() {
        Arena arena = mock(Arena.class);
        ArenaMap map = mock(ArenaMap.class);
        ArenaMapSelection selection = new ArenaMapSelection(arena, map);

        mapSelector.select(PLAYER_ID, selection);
        Optional<ArenaMapSelection> selectionOptional = mapSelector.getSelection(PLAYER_ID);

        assertThat(selectionOptional).hasValue(selection);
    }
}
