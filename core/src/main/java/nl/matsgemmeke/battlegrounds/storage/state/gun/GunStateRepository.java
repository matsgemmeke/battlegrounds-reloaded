package nl.matsgemmeke.battlegrounds.storage.state.gun;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface GunStateRepository {

    void deleteByPlayerUuid(UUID playerUuid);

    List<GunState> findByPlayerUuid(UUID playerUuid);

    void save(Collection<GunState> gunStates);
}
