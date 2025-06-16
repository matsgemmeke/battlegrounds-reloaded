package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class ShootHandlerTest {

    private FireMode fireMode;
    private ItemRepresentation itemRepresentation;

    @BeforeEach
    public void setUp() {
        fireMode = mock(FireMode.class);
        itemRepresentation = mock(ItemRepresentation.class);
    }

    @Test
    public void shootStartsFireModeThatActivatesShot() {
        ShotPerformer shotPerformer = mock(ShotPerformer.class);

        ShootHandler shootHandler = new ShootHandler(fireMode, itemRepresentation);
        shootHandler.registerObservers();
        shootHandler.shoot(shotPerformer);

        ArgumentCaptor<ShotObserver> shotObserverCaptor = ArgumentCaptor.forClass(ShotObserver.class);
        verify(fireMode).addShotObserver(shotObserverCaptor.capture());

        shotObserverCaptor.getValue().onShotActivate();

        verify(fireMode).startCycle();
    }
}
