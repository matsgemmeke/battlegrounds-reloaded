package nl.matsgemmeke.battlegrounds.item.equipment.controls.activate;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunctionException;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

public class ActivateFunction implements ItemFunction<EquipmentHolder> {

    @NotNull
    private final ActivateProperties properties;
    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final Equipment equipment;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public ActivateFunction(
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull ActivateProperties properties,
            @Assisted @NotNull Equipment equipment,
            @Assisted @NotNull AudioEmitter audioEmitter
    ) {
        this.properties = properties;
        this.equipment = equipment;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
    }

    public boolean isAvailable() {
        Activator activator = equipment.getActivator();

        return activator != null && activator.isReady();
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
        ItemEffect effect = equipment.getEffect();

        if (effect == null) {
            throw new ItemFunctionException("Cannot perform activate function for equipment item \"" + equipment.getName() + "\"; it has no effect!");
        }

        audioEmitter.playSounds(properties.activationSounds(), holder.getEntity().getLocation());

        taskRunner.runTaskLater(effect::activateInstantly, properties.delayUntilActivation());

        holder.setHeldItem(null);

        return true;
    }
}
