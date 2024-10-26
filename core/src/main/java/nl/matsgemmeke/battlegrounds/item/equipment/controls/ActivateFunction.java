package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deployment.DeployableSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemMechanismActivation;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ActivateFunction implements ItemFunction<EquipmentHolder> {

    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private DeployableSource item;
    @NotNull
    private ItemMechanismActivation mechanismActivation;
    @NotNull
    private Iterable<GameSound> sounds;
    private long delayUntilActivation;
    @NotNull
    private TaskRunner taskRunner;

    public ActivateFunction(
            @NotNull DeployableSource item,
            @NotNull ItemMechanismActivation mechanismActivation,
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner,
            long delayUntilActivation
    ) {
        this.item = item;
        this.mechanismActivation = mechanismActivation;
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

        taskRunner.runTaskLater(() -> mechanismActivation.activateDeployedObjects(holder), delayUntilActivation);

        holder.setHeldItem(null);

        return true;
    }
}
