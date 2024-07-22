package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeCollisionDetector;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultTrainingModeContext implements GameContext {

    @NotNull
    private ActionHandler actionHandler;
    @NotNull
    private InternalsProvider internals;
    @NotNull
    private TrainingMode trainingMode;

    public DefaultTrainingModeContext(@NotNull TrainingMode trainingMode, @NotNull InternalsProvider internals) {
        this.trainingMode = trainingMode;
        this.internals = internals;
        this.actionHandler = new DefaultActionHandler(trainingMode);
    }

    @NotNull
    public ActionHandler getActionHandler() {
        return actionHandler;
    }

    @NotNull
    public AudioEmitter getAudioEmitter() {
        return new DefaultAudioEmitter();
    }

    @NotNull
    public CollisionDetector getCollisionDetector() {
        BlockCollisionChecker blockCollisionChecker = new BlockCollisionChecker();

        return new TrainingModeCollisionDetector(blockCollisionChecker);
    }

    @NotNull
    public ItemRegistry<Equipment, EquipmentHolder> getEquipmentRegistry() {
        return new DefaultEquipmentRegistry(trainingMode.getEquipmentStorage());
    }

    @NotNull
    public EntityRegistry<Item, GameItem> getItemRegistry() {
        return new DefaultItemRegistry(trainingMode.getItemStorage());
    }

    @NotNull
    public ItemRegistry<Gun, GunHolder> getGunRegistry() {
        return new DefaultGunRegistry(trainingMode.getGunStorage());
    }

    @NotNull
    public EntityRegistry<Player, GamePlayer> getPlayerRegistry() {
        return new DefaultPlayerRegistry(trainingMode.getPlayerStorage(), internals);
    }
}
