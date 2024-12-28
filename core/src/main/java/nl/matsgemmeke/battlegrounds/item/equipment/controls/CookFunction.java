package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunctionException;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectNew;
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
        ItemEffectNew effect = equipment.getEffect();

        return effect != null && !effect.isAwaitingDeployment();
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
        ItemEffectNew effect = equipment.getEffect();

        if (effect == null) {
            throw new ItemFunctionException("Cannot perform cook function for equipment item \"" + equipment.getName() + "\"; it has no effect activation!");
        }

        audioEmitter.playSounds(properties.cookSounds(), holder.getEntity().getLocation());

        HeldItem heldItem = new HeldItem(holder, holder.getHeldItem());
        ItemEffectContext context = new ItemEffectContext(holder, heldItem);

        effect.prime(context);
        return true;
    }
}
