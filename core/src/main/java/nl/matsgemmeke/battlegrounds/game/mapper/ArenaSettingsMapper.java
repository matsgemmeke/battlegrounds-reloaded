package nl.matsgemmeke.battlegrounds.game.mapper;

import nl.matsgemmeke.battlegrounds.game.arena.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSettingsSpec;

public class ArenaSettingsMapper {

    public ArenaSettingsSpec toSpec(ArenaSettings settings) {
        int lobbyCountdownLength = settings.getLobbyCountdownLength();
        int maxPlayers = settings.getMaxPlayers();
        int minPlayers = settings.getMinPlayers();

        return new ArenaSettingsSpec(lobbyCountdownLength, maxPlayers, minPlayers);
    }
}
