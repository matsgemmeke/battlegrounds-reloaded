package nl.matsgemmeke.battlegrounds.game.arena.loading;

import com.google.inject.Inject;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.util.TextUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class responsible for loading all existing arenas located in the arenas setup folder.
 */
public class ArenaSetupLoader {

    private final ArenaLoader arenaLoader;
    private final File arenasFolder;
    private final Logger logger;

    @Inject
    public ArenaSetupLoader(ArenaLoader arenaLoader, @Named("ArenasFolder") File arenasFolder, @Named("Battlegrounds") Logger logger) {
        this.arenaLoader = arenaLoader;
        this.arenasFolder = arenasFolder;
        this.logger = logger;
    }

    public void loadArenas() {
        File[] files = arenasFolder.listFiles();

        if (files == null || files.length == 0) {
            logger.info("No saved arenas were found");
            return;
        }

        List<File> arenaFolders = Arrays.stream(files)
                .filter(file -> file.isDirectory() && file.getName().startsWith("arena-"))
                .toList();

        int arenaCount = arenaFolders.size();
        String arenaNoun = TextUtil.pluralize(arenaCount, "arena", "arenas");

        logger.info("Attempting to load %s saved %s".formatted(arenaCount, arenaNoun));

        for (File arenaFolder : arenaFolders) {
            try {
                this.loadArenaFolder(arenaFolder);
            } catch (InvalidArenaSetupException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }

    private void loadArenaFolder(File arenaFolder) {
        String arenaIdValue = arenaFolder.getName().split("-")[1];
        int arenaId;

        try {
            arenaId = Integer.parseInt(arenaIdValue);
        } catch (NumberFormatException ex) {
            throw new InvalidArenaSetupException("Failed to load arena: the id \"%s\" is invalid".formatted(arenaIdValue));
        }

        arenaLoader.loadArena(arenaId, arenaFolder);
    }
}
