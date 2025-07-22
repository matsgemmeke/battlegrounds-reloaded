package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.Placeholder;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.*;

public class ShootHandlerTest {

    private AmmunitionStorage ammunitionStorage;
    private FireMode fireMode;
    private ItemRepresentation itemRepresentation;
    private ProjectileLauncher projectileLauncher;
    private Recoil recoil;
    private SpreadPattern spreadPattern;

    @BeforeEach
    public void setUp() {
        ammunitionStorage = new AmmunitionStorage(10, 20, 10, 20);
        fireMode = mock(FireMode.class);
        itemRepresentation = mock(ItemRepresentation.class);
        projectileLauncher = mock(ProjectileLauncher.class);
        recoil = mock(Recoil.class);
        spreadPattern = mock(SpreadPattern.class);
    }

    @Test
    public void shootStartsFireModeThatActivatesShot() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        Location shootingDirection = new Location(null, 1, 1, 1, 90.0f, 0.0f);

        ShotPerformer performer = mock(ShotPerformer.class);
        when(performer.getShootingDirection()).thenReturn(shootingDirection);

        when(itemRepresentation.update()).thenReturn(itemStack);
        when(spreadPattern.getShotDirections(shootingDirection)).thenReturn(List.of(shootingDirection));

        ShootHandler shootHandler = new ShootHandler(fireMode, projectileLauncher, spreadPattern, ammunitionStorage, itemRepresentation, recoil);
        shootHandler.registerObservers();
        shootHandler.shoot(performer);

        ArgumentCaptor<ShotObserver> shotObserverCaptor = ArgumentCaptor.forClass(ShotObserver.class);
        verify(fireMode).addShotObserver(shotObserverCaptor.capture());

        shotObserverCaptor.getValue().onShotFired();

        verify(fireMode).startCycle();
        verify(itemRepresentation).setPlaceholder(Placeholder.MAGAZINE_AMMO, "9");
        verify(projectileLauncher).launch(shootingDirection);
        verify(recoil).produceRecoil(performer, shootingDirection);
        verify(performer).setHeldItem(itemStack);
    }

    @Test
    public void shootStartsFireModeThatDoesNotActivateShotWhenMagazineIsEmpty() {
        ShotPerformer performer = mock(ShotPerformer.class);

        ammunitionStorage.setMagazineAmmo(0);

        ShootHandler shootHandler = new ShootHandler(fireMode, projectileLauncher, spreadPattern, ammunitionStorage, itemRepresentation, recoil);
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
