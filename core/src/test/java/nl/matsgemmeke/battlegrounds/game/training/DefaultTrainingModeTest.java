package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultTrainingModeTest {

    private InternalsProvider internals;
    private ItemStorage<Equipment, EquipmentHolder> equipmentStorage;
    private ItemStorage<Gun, GunHolder> gunStorage;

    @Before
    public void setUp() {
        internals = mock(InternalsProvider.class);
        equipmentStorage = new ItemStorage<>();
        gunStorage = new ItemStorage<>();
    }

    @Test
    public void addingItemsReturnsNewGameItemInstance() {
        Item item = mock(Item.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentStorage, gunStorage);
        GameItem gameItem = trainingMode.addItem(item);

        assertNotNull(gameItem);
    }

    @Test
    public void addingPlayersReturnsNewGamePlayerInstance() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentStorage, gunStorage);
        GamePlayer gamePlayer = trainingMode.addPlayer(player);

        assertNotNull(gamePlayer);
    }

    @Test
    public void shouldReturnMatchingEntityInAnyRegisterWhenGettingGameEntity() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentStorage, gunStorage);
        trainingMode.addPlayer(player);

        GameEntity result = trainingMode.getGameEntity(player);

        assertNotNull(result);
        assertEquals(player, result.getEntity());
    }

    @Test
    public void shouldReturnNullIfNoneOfTheRegistersContainsTheCorrespondingEntityWrapper() {
        Player player = mock(Player.class);
        Player otherPlayer = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentStorage, gunStorage);
        trainingMode.addPlayer(otherPlayer);

        GameEntity result = trainingMode.getGameEntity(player);

        assertNull(result);
    }

    @Test
    public void shouldReturnCorrespondingPlayerWrapperWhenGettingPlayer() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentStorage, gunStorage);
        trainingMode.addPlayer(player);

        GamePlayer result = trainingMode.getGamePlayer(player);

        assertNotNull(result);
        assertEquals(player, result.getEntity());
    }

    @Test
    public void shouldReturnWhetherTheInstanceHasRegisteredEntityWrapper() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentStorage, gunStorage);
        trainingMode.addPlayer(player);

        boolean hasEntity = trainingMode.hasEntity(player);

        assertTrue(hasEntity);
    }

    @Test
    public void shouldReturnWhetherTheInstanceHasRegisteredPlayerWrapper() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentStorage, gunStorage);
        trainingMode.addPlayer(player);

        boolean hasPlayer = trainingMode.hasPlayer(player);

        assertTrue(hasPlayer);
    }
}
