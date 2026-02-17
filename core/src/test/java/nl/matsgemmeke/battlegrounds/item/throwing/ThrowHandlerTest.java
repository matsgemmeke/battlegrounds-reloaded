package nl.matsgemmeke.battlegrounds.item.throwing;

import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.Placeholder;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThrowHandlerTest {

    @Mock
    private ItemRepresentation itemRepresentation;
    @Mock
    private ProjectileLauncher projectileLauncher;
    @Mock
    private ThrowPerformer performer;

    private ResourceContainer resourceContainer;
    private ThrowHandler throwHandler;

    @BeforeEach
    void setUp() {
        resourceContainer = new ResourceContainer(0, 0, 0, 0);
        throwHandler = new ThrowHandler(itemRepresentation, projectileLauncher, resourceContainer);
    }

    @Test
    @DisplayName("performThrow does nothing loaded amount is zero")
    void performThrow_withoutLoadedAmount() {
        resourceContainer.setLoadedAmount(0);

        throwHandler.performThrow(performer);

        verifyNoInteractions(itemRepresentation);
        verifyNoInteractions(projectileLauncher);
    }

    @ParameterizedTest
    @CsvSource({ "3,0,1", "1,1,1", "1,0,0" })
    @DisplayName("performThrow starts projectile launcher and updates item representation")
    void performThrow_withLoadedAmount(int loadedAmount, int reserveAmount, int expectedItemStackAmount) {
        World world = mock(World.class);
        Location throwDirection = new Location(world, 1, 1, 1, 0.0f, 0.0f);
        ItemStack updatedItemStack = new ItemStack(Material.IRON_SWORD);

        resourceContainer.setLoadedAmount(loadedAmount);
        resourceContainer.setReserveAmount(reserveAmount);

        when(itemRepresentation.update()).thenReturn(updatedItemStack);
        when(performer.getThrowDirection()).thenReturn(throwDirection);

        throwHandler.performThrow(performer);

        ArgumentCaptor<LaunchContext> launchContextCaptor = ArgumentCaptor.forClass(LaunchContext.class);
        verify(projectileLauncher).launch(launchContextCaptor.capture());

        assertThat(launchContextCaptor.getValue()).satisfies(launchContext -> {
            assertThat(launchContext.damageSource()).isEqualTo(performer);
            assertThat(launchContext.projectileSource()).isEqualTo(performer);
            assertThat(launchContext.direction()).isEqualTo(throwDirection);
            assertThat(launchContext.world()).isEqualTo(world);
        });

        assertThat(resourceContainer.getLoadedAmount()).isEqualTo(loadedAmount - 1);

        verify(itemRepresentation).setPlaceholder(Placeholder.LOADED_AMOUNT, String.valueOf(loadedAmount - 1));
        verify(itemRepresentation).setPlaceholder(Placeholder.RESERVE_AMOUNT, String.valueOf(reserveAmount));
        verify(itemRepresentation).setAmount(expectedItemStackAmount);
        verify(performer).setHeldItem(updatedItemStack);
    }
}
