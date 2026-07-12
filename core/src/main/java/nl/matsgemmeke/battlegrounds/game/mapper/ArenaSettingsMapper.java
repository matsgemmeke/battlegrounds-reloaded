package nl.matsgemmeke.battlegrounds.game.mapper;

import nl.matsgemmeke.battlegrounds.arena.configuration.ArenaSettingsSpec;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;

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
