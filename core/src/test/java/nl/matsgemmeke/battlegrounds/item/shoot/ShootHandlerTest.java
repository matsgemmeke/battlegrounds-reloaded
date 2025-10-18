package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.Placeholder;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShootHandlerTest {

    @Mock
    private FireMode fireMode;
    @Mock
    private ItemRepresentation itemRepresentation;
    @Mock
    private ProjectileLauncher projectileLauncher;
    @Mock
    private Recoil recoil;
    @Mock
    private SpreadPattern spreadPattern;

    private AmmunitionStorage ammunitionStorage;
    private ShootHandler shootHandler;

    @BeforeEach
    void setUp() {
        ammunitionStorage = new AmmunitionStorage(10, 20, 10, 20);
        shootHandler = new ShootHandler(fireMode, projectileLauncher, spreadPattern, ammunitionStorage, itemRepresentation, recoil);
    }

    @Test
    void cancelCancelsFireModeAndProjectileLauncher() {
        shootHandler.cancel();

        verify(fireMode).cancelCycle();
        verify(projectileLauncher).cancel();
    }

    @Test
    void shootStartsFireModeThatActivatesShot() {
        Entity entity = mock(Entity.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        Location shootingDirection = new Location(null, 1, 1, 1, 90.0f, 0.0f);

        ShotPerformer performer = mock(ShotPerformer.class);
        when(performer.getEntity()).thenReturn(entity);
        when(performer.getShootingDirection()).thenReturn(shootingDirection);

        when(itemRepresentation.update()).thenReturn(itemStack);
        when(spreadPattern.getShotDirections(shootingDirection)).thenReturn(List.of(shootingDirection));

        shootHandler.registerObservers();
        shootHandler.shoot(performer);

        ArgumentCaptor<ShotObserver> shotObserverCaptor = ArgumentCaptor.forClass(ShotObserver.class);
        verify(fireMode).addShotObserver(shotObserverCaptor.capture());

        shotObserverCaptor.getValue().onShotFired();

        ArgumentCaptor<LaunchContext> launchContextCaptor = ArgumentCaptor.forClass(LaunchContext.class);
        verify(projectileLauncher).launch(launchContextCaptor.capture());

        LaunchContext launchContext = launchContextCaptor.getValue();
        assertThat(launchContext.entity()).isEqualTo(entity);
        assertThat(launchContext.direction()).isEqualTo(shootingDirection);

        verify(fireMode).startCycle();
        verify(itemRepresentation).setPlaceholder(Placeholder.MAGAZINE_AMMO, "9");
        verify(recoil).produceRecoil(performer, shootingDirection);
        verify(performer).setHeldItem(itemStack);
    }

    @Test
    void shootStartsFireModeThatDoesNotActivateShotWhenMagazineIsEmpty() {
        ShotPerformer performer = mock(ShotPerformer.class);

        ammunitionStorage.setMagazineAmmo(0);

        shootHandler.registerObservers();
        shootHandler.shoot(performer);

        ArgumentCaptor<ShotObserver> shotObserverCaptor = ArgumentCaptor.forClass(ShotObserver.class);
        verify(fireMode).addShotObserver(shotObserverCaptor.capture());

        shotObserverCaptor.getValue().onShotFired();

        verify(fireMode).startCycle();
        verifyNoInteractions(itemRepresentation);
        verifyNoInteractions(projectileLauncher);
        verifyNoInteractions(performer);
    }
}
