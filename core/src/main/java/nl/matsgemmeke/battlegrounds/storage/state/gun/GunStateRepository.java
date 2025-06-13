package nl.matsgemmeke.battlegrounds.storage.state.gun;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface GunStateRepository {

    void deleteByPlayerUuid(@NotNull UUID playerUuid);

    @NotNull
    List<GunState> findByPlayerUuid(@NotNull UUID playerUuid);

    void save(@NotNull Collection<GunState> gunStates);
}
