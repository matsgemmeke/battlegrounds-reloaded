package nl.matsgemmeke.battlegrounds.storage.state;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface StateStorage {

    @NotNull
    GamePlayerState findGamePlayerStateByPlayerUuid(@NotNull UUID playerUuid);

    void saveGamePlayerState(@NotNull GamePlayerState gamePlayerState);
}
