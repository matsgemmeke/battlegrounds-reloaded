package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deployment.DeployableSource;
import nl.matsgemmeke.battlegrounds.item.deployment.DeployedObject;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ActivateFunctionTest {

    private AudioEmitter audioEmitter;
    private DeployableSource item;
    private ItemMechanismActivation mechanismActivation;
    private long delayUntilActivation;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        item = mock(DeployableSource.class);
        mechanismActivation = mock(ItemMechanismActivation.class);
        delayUntilActivation = 1L;
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldNotBeAvailableIfItemHasNoDeployedObjects() {
        when(item.getDeployedObjects()).thenReturn(Collections.emptyList());

        ActivateFunction function = new ActivateFunction(item, mechanismActivation, audioEmitter, taskRunner, delayUntilActivation);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldNotBeAvailableIfMechanismActivationIsNotPrimed() {
        DeployedObject object = mock(DeployedObject.class);

        when(item.getDeployedObjects()).thenReturn(List.of(object));
        when(mechanismActivation.isPrimed()).thenReturn(false);

        ActivateFunction function = new ActivateFunction(item, mechanismActivation, audioEmitter, taskRunner, delayUntilActivation);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldBeAvailableIfItemHasDeployedObjectsAndMechanismActivationIsPrimed() {
        DeployedObject object = mock(DeployedObject.class);

        when(item.getDeployedObjects()).thenReturn(List.of(object));
        when(mechanismActivation.isPrimed()).thenReturn(true);

        ActivateFunction function = new ActivateFunction(item, mechanismActivation, audioEmitter, taskRunner, delayUntilActivation);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void shouldRemoveHeldItemAndPerformDelayedTaskThatActivatesMechanismActivationWhenPerforming() {
        Location location = new Location(null, 1, 1, 1);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(location);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(entity);

        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).then(answer -> {
            answer.getArgument(0, Runnable.class).run();
            return null;
        });

        ActivateFunction function = new ActivateFunction(item, mechanismActivation, audioEmitter, taskRunner, delayUntilActivation);
        function.perform(holder);

        verify(holder).setHeldItem(null);
        verify(mechanismActivation).activate(holder);
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayUntilActivation));
    }
}
