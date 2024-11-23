package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunctionException;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.HeldItem;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

public class CookFunction implements ItemFunction<EquipmentHolder> {

    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private CookProperties properties;
    @NotNull
    private Equipment equipment;

    public CookFunction(@NotNull CookProperties properties, @NotNull Equipment equipment, @NotNull AudioEmitter audioEmitter) {
        this.properties = properties;
        this.equipment = equipment;
        this.audioEmitter = audioEmitter;
    }

    public boolean isAvailable() {
        ItemEffectActivation effectActivation = equipment.getEffectActivation();

        return effectActivation != null && !effectActivation.isAwaitingDeployment();
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isPerforming() {
        return false;
    }

    public boolean cancel() {
        return false;
    }

    public boolean perform(@NotNull EquipmentHolder holder) {
        ItemEffectActivation effectActivation = equipment.getEffectActivation();

        if (effectActivation == null) {
            throw new ItemFunctionException("Cannot perform cook function for equipment item \"" + equipment.getName() + "\"; it has no effect activation!");
        }

        System.out.println(equipment);
        System.out.println(effectActivation);

        audioEmitter.playSounds(properties.cookSounds(), holder.getEntity().getLocation());
        effectActivation.prime(holder, new HeldItem(holder, holder.getHeldItem()));
        return true;
    }
}
