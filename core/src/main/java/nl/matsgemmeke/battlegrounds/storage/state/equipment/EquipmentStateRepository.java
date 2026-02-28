package nl.matsgemmeke.battlegrounds.storage.state.equipment;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EquipmentStateRepository {

    void deleteByPlayerUuid(UUID playerUuid);

    List<EquipmentState> findByPlayerUuid(UUID playerUuid);

    void save(Collection<EquipmentState> equipmentStates);
}
