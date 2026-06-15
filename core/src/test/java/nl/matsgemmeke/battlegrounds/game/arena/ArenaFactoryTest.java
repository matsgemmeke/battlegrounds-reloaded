package nl.matsgemmeke.battlegrounds.game.arena;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ArenaFactoryTest {

    private static final int ARENA_ID = 1;

    @TempDir
    private File setupFolder;
    @InjectMocks
    private ArenaFactory arenaFactory;

    @Test
    @DisplayName("create returns new arena instance and creates setup file")
    void create() {
        ArenaConfiguration configuration = ArenaConfiguration.getNewConfiguration();

        Arena arena = arenaFactory.create(ARENA_ID, configuration);

        assertThat(arena).isNotNull();
    }
}
