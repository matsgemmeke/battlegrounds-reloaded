package nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

public interface ThrowFunctionFactory {

    @NotNull
    ItemFunction<EquipmentHolder> create(@NotNull ThrowProperties properties, @NotNull Equipment equipment, @NotNull AudioEmitter audioEmitter);
}
