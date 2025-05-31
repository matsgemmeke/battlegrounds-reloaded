package nl.matsgemmeke.battlegrounds.storage.state;

import org.jetbrains.annotations.NotNull;

public interface StateStorage {

    void saveGamePlayerState(@NotNull GamePlayerState gamePlayerState);
}
