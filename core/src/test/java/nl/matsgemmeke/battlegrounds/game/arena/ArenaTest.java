package nl.matsgemmeke.battlegrounds.game.arena;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ArenaTest {

    @Mock
    private ArenaConfiguration configuration;
    @InjectMocks
    private Arena arena;

    @Test
    void getConfiguration() {
        assertThat(arena.getConfiguration()).isEqualTo(configuration);
    }
}
