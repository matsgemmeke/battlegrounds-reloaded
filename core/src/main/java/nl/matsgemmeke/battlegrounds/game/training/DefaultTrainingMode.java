package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.*;
import nl.matsgemmeke.battlegrounds.game.BaseGame;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
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
    private ItemStorage<Equipment, EquipmentHolder> equipmentStorage;
    @NotNull
    private ItemStorage<Gun, GunHolder> gunStorage;

    public DefaultTrainingMode(
            @NotNull InternalsProvider internals,
            @NotNull ItemStorage<Equipment, EquipmentHolder> equipmentStorage,
            @NotNull ItemStorage<Gun, GunHolder> gunStorage
    ) {
        this.internals = internals;
        this.equipmentStorage = equipmentStorage;
        this.gunStorage = gunStorage;
        this.context = new DefaultTrainingModeContext(this);
    }

    @NotNull
    public GameContext getContext() {
        return context;
    }

    @NotNull
    public ItemStorage<Equipment, EquipmentHolder> getEquipmentStorage() {
        return equipmentStorage;
    }

    @NotNull
    public ItemStorage<Gun, GunHolder> getGunStorage() {
        return gunStorage;
    }

    @NotNull
    public GameItem addItem(@NotNull Item item) {
        GameItem gameItem = new DefaultGameItem(item);

        itemStorage.addEntity(gameItem);

        return gameItem;
    }

    @NotNull
    public GamePlayer addPlayer(@NotNull Player player) {
        GamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

        playerStorage.addEntity(gamePlayer);

        return gamePlayer;
    }

    public double calculateDamage(@NotNull Entity damager, @NotNull Entity entity, double damage) {
        return damage;
    }
}
