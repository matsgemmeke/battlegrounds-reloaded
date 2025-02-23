package nl.matsgemmeke.battlegrounds.di;

import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;

public final class TypeLiterals {

    public static final TypeLiteral<ItemFunction<EquipmentHolder>> EQUIPMENT_FUNCTION = new TypeLiteral<>() {};
}
