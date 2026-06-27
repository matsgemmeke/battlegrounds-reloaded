package nl.matsgemmeke.battlegrounds.game.arena;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ArenaTest {

    private static final int ID = 1;

    @Mock
    private ArenaSettings settings;

    private Arena arena;

    @BeforeEach
    void setUp() {
        arena = new Arena(ID, settings);
    }

    @Test
    void getSettings() {
        assertThat(arena.getSettings()).isEqualTo(settings);
    }
}
