package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class ShootHandlerTest {

    private FireMode fireMode;
    private ItemRepresentation itemRepresentation;
    private ProjectileLauncher projectileLauncher;

    @BeforeEach
    public void setUp() {
        fireMode = mock(FireMode.class);
        itemRepresentation = mock(ItemRepresentation.class);
        projectileLauncher = mock(ProjectileLauncher.class);
    }

    @Test
    public void shootStartsFireModeThatActivatesShot() {
        ShotPerformer shotPerformer = mock(ShotPerformer.class);

        ShootHandler shootHandler = new ShootHandler(fireMode, projectileLauncher, itemRepresentation);
        shootHandler.registerObservers();
        shootHandler.shoot(shotPerformer);

        ArgumentCaptor<ShotObserver> shotObserverCaptor = ArgumentCaptor.forClass(ShotObserver.class);
        verify(fireMode).addShotObserver(shotObserverCaptor.capture());

        shotObserverCaptor.getValue().onShotFired();

        verify(fireMode).startCycle();
    }
}
