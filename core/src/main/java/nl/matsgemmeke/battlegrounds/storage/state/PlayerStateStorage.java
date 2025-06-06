package nl.matsgemmeke.battlegrounds.storage.state;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerStateStorage {

    void deletePlayerState(@NotNull UUID playerUuid);

    @NotNull
    PlayerState findPlayerStateByPlayerUuid(@NotNull UUID playerUuid);

    void savePlayerState(@NotNull PlayerState playerState);
}
