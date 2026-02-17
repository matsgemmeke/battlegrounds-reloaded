package nl.matsgemmeke.battlegrounds.storage.state.melee;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MeleeWeaponStateRepository {

    void deleteByPlayerUuid(UUID playerUuid);

    List<MeleeWeaponState> findByPlayerUuid(UUID playerUuid);

    void save(Collection<MeleeWeaponState> meleeWeaponStates);
}
