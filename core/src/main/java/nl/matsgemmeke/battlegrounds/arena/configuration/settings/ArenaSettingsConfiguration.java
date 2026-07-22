package nl.matsgemmeke.battlegrounds.arena.configuration.settings;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.ValidationException;

public class ArenaSettingsConfiguration {

    private static final String LOBBY_COUNTDOWN_LENGTH_PATH = "lobby-countdown-length";
    private static final String MAX_PLAYERS_PATH = "max-players";
    private static final String MIN_PLAYERS_PATH = "min-players";

    private final ConfigurationFile configurationFile;
    private final ObjectValidator objectValidator;

    @Inject
    public ArenaSettingsConfiguration(ObjectValidator objectValidator, @Assisted ConfigurationFile configurationFile) {
        this.objectValidator = objectValidator;
        this.configurationFile = configurationFile;
    }

    public ArenaSettingsSpec getArenaSettings() {
        int lobbyCountdownLength = this.getInt(LOBBY_COUNTDOWN_LENGTH_PATH);
        int maxPlayers = this.getInt(MAX_PLAYERS_PATH);
        int minPlayers = this.getInt(MIN_PLAYERS_PATH);

        ArenaSettingsSpec spec = new ArenaSettingsSpec(lobbyCountdownLength, maxPlayers, minPlayers);

        try {
            objectValidator.validate(spec);
            return spec;
        } catch (ValidationException ex) {
            throw new InvalidArenaSettingsSpecException("Failed to load arena settings specification", ex);
        }
    }

    private int getInt(String path) {
        return configurationFile.getInt(path).orElseThrow(() -> new InvalidArenaSettingsSpecException("Missing required value at " + path));
    }

    public void saveArenaSettings(ArenaSettingsSpec spec) {
        try {
            objectValidator.validate(spec);
        } catch (ValidationException ex) {
            throw new InvalidArenaSettingsSpecException("Cannot save invalid arena settings specification", ex);
        }

        configurationFile.set(LOBBY_COUNTDOWN_LENGTH_PATH, spec.lobbyCountdownLength());
        configurationFile.set(MAX_PLAYERS_PATH, spec.maxPlayers());
        configurationFile.set(MIN_PLAYERS_PATH, spec.minPlayers());
        configurationFile.save();
    }
}
