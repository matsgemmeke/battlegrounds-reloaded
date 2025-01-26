package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
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
    protected EntityStorage<GamePlayer> playerStorage;
    @NotNull
    protected ItemStorage<Equipment, EquipmentHolder> equipmentStorage;
    @NotNull
    protected ItemStorage<Gun, GunHolder> gunStorage;
    @NotNull
    protected Set<ItemBehavior> itemBehaviors;
    @NotNull
    protected SpawnPointStorage spawnPointStorage;

    public BaseGame() {
        this.equipmentStorage = new ItemStorage<>();
        this.gunStorage = new ItemStorage<>();
        this.itemBehaviors = new HashSet<>();
        this.playerStorage = new EntityStorage<>();
        this.spawnPointStorage = new SpawnPointStorage();
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
    public Set<ItemBehavior> getItemBehaviors() {
        return itemBehaviors;
    }

    @NotNull
    public EntityStorage<GamePlayer> getPlayerStorage() {
        return playerStorage;
    }

    @NotNull
    public SpawnPointStorage getSpawnPointStorage() {
        return spawnPointStorage;
    }

    public void addItemBehavior(@NotNull ItemBehavior itemBehavior) {
        itemBehaviors.add(itemBehavior);
    }
}
