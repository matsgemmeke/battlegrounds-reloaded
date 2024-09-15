package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.game.damage.check.NegateDefaultExplosionDamageCheck;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeDamageProcessor;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentBehavior;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunBehavior;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrainingModeFactory {

    @NotNull
    private BattlegroundsConfiguration config;
    @NotNull
    private InternalsProvider internals;

    public TrainingModeFactory(@NotNull BattlegroundsConfiguration config, @NotNull InternalsProvider internals) {
        this.config = config;
        this.internals = internals;
    }

    @NotNull
    public TrainingMode make() {
        ItemStorage<Equipment, EquipmentHolder> equipmentStorage = new ItemStorage<>();
        ItemStorage<Gun, GunHolder> gunStorage = new ItemStorage<>();

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentStorage, gunStorage);
        GameContext trainingModeContext = trainingMode.getContext();

        trainingMode.addItemBehavior(new EquipmentBehavior(equipmentStorage));
        trainingMode.addItemBehavior(new GunBehavior(gunStorage));

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext);
        damageProcessor.addDamageCheck(new NegateDefaultExplosionDamageCheck());

        trainingModeContext.setDamageProcessor(damageProcessor);

        this.registerPlayers(trainingModeContext);

        return trainingMode;
    }

    private void registerPlayers(@NotNull GameContext trainingModeContext) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = trainingModeContext.getPlayerRegistry().registerEntity(player);
            gamePlayer.setPassive(config.isEnabledRegisterPlayersAsPassive());
        }
    }
}
