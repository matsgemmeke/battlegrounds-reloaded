package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.item.actor.HeldItemActor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.deploy.object.HeldDeploymentObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrimeDeploymentActionTest {

    private static final UUID DEPLOYER_UNIQUE_ID = UUID.randomUUID();
    private static final DestructionListener LISTENER = damage -> {};

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private GameEntityFinder gameEntityFinder;
    @InjectMocks
    private PrimeDeploymentAction deploymentAction;

    @Test
    @DisplayName("perform returns empty optional when no game entity exists for deployer unique id")
    void perform_deployerNotRegistered() {
        Entity deployerEntity = mock(Entity.class);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getUniqueId()).thenReturn(DEPLOYER_UNIQUE_ID);

        when(gameEntityFinder.findGameEntityByUniqueId(DEPLOYER_UNIQUE_ID)).thenReturn(Optional.empty());

        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(deployer, deployerEntity, LISTENER);

        assertThat(deploymentResultOptional).isEmpty();
    }

    @Test
    @DisplayName("perform returns optional with DeploymentResult without playing sounds")
    void perform_withoutPlayingSounds() {
        ItemStack itemStack = new ItemStack(Material.STICK);
        GameEntity gameEntity = mock(GameEntity.class);
        Entity deployerEntity = mock(Entity.class);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getHeldItem()).thenReturn(itemStack);
        when(deployer.getUniqueId()).thenReturn(DEPLOYER_UNIQUE_ID);

        when(gameEntityFinder.findGameEntityByUniqueId(DEPLOYER_UNIQUE_ID)).thenReturn(Optional.of(gameEntity));

        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(deployer, deployerEntity, LISTENER);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).isInstanceOf(HeldDeploymentObject.class);
        });

        verifyNoInteractions(audioEmitter);
    }

    @Test
    @DisplayName("perform returns optional with DeploymentResult with playing sounds")
    void perform_withPlayingSounds() {
        ItemStack itemStack = new ItemStack(Material.STICK);
        List<GameSound> primeSounds = List.of(mock(GameSound.class));
        Location deployerLocation = new Location(null, 1, 1, 1);
        GameEntity gameEntity = mock(GameEntity.class);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getHeldItem()).thenReturn(itemStack);
        when(deployer.getUniqueId()).thenReturn(DEPLOYER_UNIQUE_ID);

        Entity deployerEntity = mock(Entity.class);
        when(deployerEntity.getLocation()).thenReturn(deployerLocation);

        when(gameEntityFinder.findGameEntityByUniqueId(DEPLOYER_UNIQUE_ID)).thenReturn(Optional.of(gameEntity));

        deploymentAction.configurePrimeSounds(primeSounds);
        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(deployer, deployerEntity, LISTENER);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).isInstanceOf(HeldDeploymentObject.class);
            assertThat(deploymentResult.actor()).isInstanceOf(HeldItemActor.class);
        });

        verify(audioEmitter).playSounds(primeSounds, deployerLocation);
    }
}
