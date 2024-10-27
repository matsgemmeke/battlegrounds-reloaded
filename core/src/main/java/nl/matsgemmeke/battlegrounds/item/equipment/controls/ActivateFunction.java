package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deployment.DeployableSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ActivateFunction implements ItemFunction<EquipmentHolder> {

    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private DeployableSource item;
    @NotNull
    private ItemEffectActivation effectActivation;
    @NotNull
    private Iterable<GameSound> sounds;
    private long delayUntilActivation;
    @NotNull
    private TaskRunner taskRunner;

    public ActivateFunction(
            @NotNull DeployableSource item,
            @NotNull ItemEffectActivation effectActivation,
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner,
            long delayUntilActivation
    ) {
        this.item = item;
        this.effectActivation = effectActivation;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.delayUntilActivation = delayUntilActivation;
        this.sounds = new HashSet<>();
    }

    public void addSounds(@NotNull Iterable<GameSound> sounds) {
        this.sounds = Iterables.concat(this.sounds, sounds);
    }

    public boolean isAvailable() {
        return !item.getDeployedObjects().isEmpty();
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
        audioEmitter.playSounds(sounds, holder.getEntity().getLocation());

        taskRunner.runTaskLater(() -> effectActivation.activateInstantly(holder), delayUntilActivation);

        holder.setHeldItem(null);

        return true;
    }
}
