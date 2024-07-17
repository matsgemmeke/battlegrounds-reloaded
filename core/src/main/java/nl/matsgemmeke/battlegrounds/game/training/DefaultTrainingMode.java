package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.*;
import nl.matsgemmeke.battlegrounds.game.BaseGame;
import nl.matsgemmeke.battlegrounds.game.component.GameContext;
import nl.matsgemmeke.battlegrounds.game.training.component.DefaultTrainingModeContext;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultTrainingMode extends BaseGame implements TrainingMode {

    @NotNull
    private GameContext context;
    @NotNull
    private InternalsProvider internals;
    @NotNull
    private ItemRegister<Equipment, EquipmentHolder> equipmentRegister;
    @NotNull
    private ItemRegister<Gun, GunHolder> gunRegister;

    public DefaultTrainingMode(
            @NotNull InternalsProvider internals,
            @NotNull ItemRegister<Equipment, EquipmentHolder> equipmentRegister,
            @NotNull ItemRegister<Gun, GunHolder> gunRegister
    ) {
        this.internals = internals;
        this.equipmentRegister = equipmentRegister;
        this.gunRegister = gunRegister;
        this.context = new DefaultTrainingModeContext();
    }

    @NotNull
    public GameContext getContext() {
        return context;
    }

    @NotNull
    public ItemRegister<Equipment, EquipmentHolder> getEquipmentRegister() {
        return equipmentRegister;
    }

    @NotNull
    public ItemRegister<Gun, GunHolder> getGunRegister() {
        return gunRegister;
    }

    @NotNull
    public GameItem addItem(@NotNull Item item) {
        GameItem gameItem = new DefaultGameItem(item);

        itemEntityRegister.addEntity(gameItem);

        return gameItem;
    }

    @NotNull
    public GamePlayer addPlayer(@NotNull Player player) {
        GamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

        playerRegister.addEntity(gamePlayer);

        return gamePlayer;
    }

    public double calculateDamage(@NotNull Entity damager, @NotNull Entity entity, double damage) {
        return damage;
    }
}
