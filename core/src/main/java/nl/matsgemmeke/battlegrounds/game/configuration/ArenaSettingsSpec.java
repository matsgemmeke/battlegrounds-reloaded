package nl.matsgemmeke.battlegrounds.game.configuration;

import jakarta.validation.constraints.Min;

public record ArenaSettingsSpec(
        @Min(value = 1, message = "lobby countdown length must be at least 1 second")
        int lobbyCountdownLength,
        @Min(value = 1, message = "max players must be at least 1")
        int maxPlayers,
        @Min(value = 1, message = "min players must be at least 1")
        int minPlayers
) {
}
