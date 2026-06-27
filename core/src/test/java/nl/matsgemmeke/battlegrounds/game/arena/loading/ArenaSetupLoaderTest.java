package nl.matsgemmeke.battlegrounds.game.arena.loading;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArenaSetupLoaderTest {

    @Mock
    private ArenaLoader arenaLoader;
    @Mock
    private Logger logger;

    private ArenaSetupLoader arenaSetupLoader;

    @Test
    @DisplayName("loadArenas logs info message when the arenas folder does not exist")
    void loadArenas_arenasFolderNotExists() {
        File arenasFolder = new File("src/test/resources/arena-setups/nonexistent");

        arenaSetupLoader = new ArenaSetupLoader(arenaLoader, arenasFolder, logger);
        arenaSetupLoader.loadArenas();

        verify(logger).info("No saved arenas were found");
    }

    @Test
    @DisplayName("loadArenas logs info message when the arenas folder is empty")
    void loadArenas_arenasFolderEmpty() {
        File arenasFolder = new File("src/test/resources/arena-setups/empty");
        arenasFolder.mkdirs();

        arenaSetupLoader = new ArenaSetupLoader(arenaLoader, arenasFolder, logger);
        arenaSetupLoader.loadArenas();

        verify(logger).info("No saved arenas were found");
    }

    @Test
    @DisplayName("loadArenas logs error message when the arena folder has an invalid id")
    void loadArenas_invalidArenaFolderName() {
        File arenasFolder = new File("src/test/resources/arena-setups/invalid-arena-id");

        arenaSetupLoader = new ArenaSetupLoader(arenaLoader, arenasFolder, logger);
        arenaSetupLoader.loadArenas();

        verify(logger).severe("Failed to load arena: the id \"fail\" is invalid");
    }

    @Test
    @DisplayName("loadArenas calls arena loader for each arena folder that is found")
    void loadArenas_successful() {
        File arenasFolder = new File("src/test/resources/arena-setups/valid");

        arenaSetupLoader = new ArenaSetupLoader(arenaLoader, arenasFolder, logger);
        arenaSetupLoader.loadArenas();

        verify(logger).info("Attempting to load 1 saved arenas");
        verify(arenaLoader).loadArena(eq(1), argThat(file -> file.equals(new File("src/test/resources/arena-setups/valid/arena-1"))));
    }

    @Test
    @DisplayName("loadArenas logs error message when an error occurs when loading an arena folder")
    void loadArenas_loadArenaFolderError() {
        File arenasFolder = new File("src/test/resources/arena-setups/valid");

        doThrow(new InvalidArenaSetupException("error")).when(arenaLoader).loadArena(eq(1), argThat(file -> file.equals(new File("src/test/resources/arena-setups/valid/arena-1"))));

        arenaSetupLoader = new ArenaSetupLoader(arenaLoader, arenasFolder, logger);
        arenaSetupLoader.loadArenas();

        verify(logger).severe("error");
    }
}
