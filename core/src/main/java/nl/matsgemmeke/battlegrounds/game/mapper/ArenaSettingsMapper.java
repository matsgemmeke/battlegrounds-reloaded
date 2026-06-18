package nl.matsgemmeke.battlegrounds.game.mapper;

import nl.matsgemmeke.battlegrounds.game.arena.ArenaConfiguration;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSettingsSpec;

public class ArenaSettingsMapper {

    public ArenaSettingsSpec toSpec(ArenaConfiguration configuration) {
        int lobbyCountdownLength = configuration.getLobbyCountdownLength();
        int maxPlayers = configuration.getMaxPlayers();
        int minPlayers = configuration.getMinPlayers();

        return new ArenaSettingsSpec(lobbyCountdownLength, maxPlayers, minPlayers);
    }
}
