package nl.matsgemmeke.battlegrounds.item.equipment.controls.activate;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

public interface ActivateFunctionFactory {

    @NotNull
    ItemFunction<EquipmentHolder> create(@NotNull ActivateProperties properties, @NotNull Equipment equipment, @NotNull AudioEmitter audioEmitter);
}
