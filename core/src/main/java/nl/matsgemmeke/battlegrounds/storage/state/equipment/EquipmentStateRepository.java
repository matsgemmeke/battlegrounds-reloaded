package nl.matsgemmeke.battlegrounds.storage.state.equipment;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EquipmentStateRepository {

    void deleteByPlayerUuid(@NotNull UUID playerUuid);

    @NotNull
    List<EquipmentState> findByPlayerUuid(@NotNull UUID playerUuid);

    void save(@NotNull Collection<EquipmentState> equipmentStates);
}
