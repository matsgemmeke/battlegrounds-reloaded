package nl.matsgemmeke.battlegrounds.game.mapper;

import nl.matsgemmeke.battlegrounds.game.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSettingsSpec;

public class ArenaSettingsMapper {

    public ArenaSettings toDomain(ArenaSettingsSpec spec) {
        int lobbyCountdownLength = spec.lobbyCountdownLength();
        int maxPlayers = spec.maxPlayers();
        int minPlayers = spec.minPlayers();

        return new ArenaSettings(lobbyCountdownLength, maxPlayers, minPlayers);
    }

    public ArenaSettingsSpec toSpec(ArenaSettings settings) {
        int lobbyCountdownLength = settings.getLobbyCountdownLength();
        int maxPlayers = settings.getMaxPlayers();
        int minPlayers = settings.getMinPlayers();

        return new ArenaSettingsSpec(lobbyCountdownLength, maxPlayers, minPlayers);
    }
}
