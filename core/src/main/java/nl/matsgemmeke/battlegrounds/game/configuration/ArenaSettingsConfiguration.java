package nl.matsgemmeke.battlegrounds.game.configuration;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.configuration.BasePluginConfiguration;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.ValidationException;

import java.io.File;
import java.io.InputStream;

public class ArenaSettingsConfiguration extends BasePluginConfiguration {

    private static final String LOBBY_COUNTDOWN_LENGTH_PATH = "lobby-countdown-length";
    private static final String MAX_PLAYERS_PATH = "max-players";
    private static final String MIN_PLAYERS_PATH = "min-players";

    private final ObjectValidator objectValidator;

    @Inject
    public ArenaSettingsConfiguration(ObjectValidator objectValidator, @Assisted File file, @Assisted InputStream resource) {
        super(file, resource, false);
        this.objectValidator = objectValidator;
    }

    public ArenaSettingsSpec getArenaSettings() {
        int lobbyCountdownLength = this.getInteger(LOBBY_COUNTDOWN_LENGTH_PATH);
        int maxPlayers = this.getInteger(MAX_PLAYERS_PATH);
        int minPlayers = this.getInteger(MIN_PLAYERS_PATH);

        ArenaSettingsSpec spec = new ArenaSettingsSpec(lobbyCountdownLength, maxPlayers, minPlayers);

        try {
            objectValidator.validate(spec);
            return spec;
        } catch (ValidationException ex) {
            throw new InvalidArenaConfigurationSpecException("Failed to load arena configuration specification", ex);
        }
    }

    public void saveArenaSettings(ArenaSettingsSpec spec) {
        try {
            objectValidator.validate(spec);
        } catch (ValidationException ex) {
            throw new InvalidArenaConfigurationSpecException("Cannot save invalid arena settings specification", ex);
        }

        this.set(LOBBY_COUNTDOWN_LENGTH_PATH, spec.lobbyCountdownLength());
        this.set(MAX_PLAYERS_PATH, spec.maxPlayers());
        this.set(MIN_PLAYERS_PATH, spec.minPlayers());
        this.save();
    }
}
