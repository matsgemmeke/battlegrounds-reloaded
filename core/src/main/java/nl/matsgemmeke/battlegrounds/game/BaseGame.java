package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointContainer;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseGame implements Game {

    @NotNull
    protected EntityContainer<GamePlayer> playerContainer;
    @NotNull
    protected ItemContainer<Equipment, EquipmentHolder> equipmentContainer;
    @NotNull
    protected ItemContainer<Gun, GunHolder> gunContainer;
    @NotNull
    protected Set<ItemBehavior> itemBehaviors;
    @NotNull
    protected SpawnPointContainer spawnPointContainer;

    public BaseGame() {
        this.equipmentContainer = new ItemContainer<>();
        this.gunContainer = new ItemContainer<>();
        this.itemBehaviors = new HashSet<>();
        this.playerContainer = new EntityContainer<>();
        this.spawnPointContainer = new SpawnPointContainer();
    }

    @NotNull
    public ItemContainer<Equipment, EquipmentHolder> getEquipmentContainer() {
        return equipmentContainer;
    }

    @NotNull
    public ItemContainer<Gun, GunHolder> getGunContainer() {
        return gunContainer;
    }

    @NotNull
    public Set<ItemBehavior> getItemBehaviors() {
        return itemBehaviors;
    }

    @NotNull
    public EntityContainer<GamePlayer> getPlayerContainer() {
        return playerContainer;
    }

    @NotNull
    public SpawnPointContainer getSpawnPointContainer() {
        return spawnPointContainer;
    }

    public void addItemBehavior(@NotNull ItemBehavior itemBehavior) {
        itemBehaviors.add(itemBehavior);
    }
}
