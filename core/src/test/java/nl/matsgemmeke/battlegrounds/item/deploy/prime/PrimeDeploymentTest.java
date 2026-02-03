package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.actor.DeployerActor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
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
class PrimeDeploymentTest {

    private static final UUID DEPLOYER_UNIQUE_ID = UUID.randomUUID();
    private static final DestructionListener LISTENER = () -> {};

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private GameEntityFinder gameEntityFinder;
    @InjectMocks
    private PrimeDeployment deployment;

    @Test
    @DisplayName("perform returns empty optional when no game entity exists for deployer unique id")
    void perform_deployerNotRegistered() {
        Entity deployerEntity = mock(Entity.class);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getUniqueId()).thenReturn(DEPLOYER_UNIQUE_ID);

        when(gameEntityFinder.findGameEntityByUniqueId(DEPLOYER_UNIQUE_ID)).thenReturn(Optional.empty());

        Optional<DeploymentResult> deploymentResultOptional = deployment.perform(deployer, deployerEntity, LISTENER);

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

        Optional<DeploymentResult> deploymentResultOptional = deployment.perform(deployer, deployerEntity, LISTENER);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).satisfies(deploymentObject -> {
                assertThat(deploymentObject.getHealth()).isZero();
                assertThat(deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE)).isTrue();
            });
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

        deployment.configurePrimeSounds(primeSounds);
        Optional<DeploymentResult> deploymentResultOptional = deployment.perform(deployer, deployerEntity, LISTENER);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).satisfies(deploymentObject -> {
                assertThat(deploymentObject.getHealth()).isZero();
                assertThat(deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE)).isTrue();
            });
            assertThat(deploymentResult.actor()).isInstanceOf(DeployerActor.class);
        });

        verify(audioEmitter).playSounds(primeSounds, deployerLocation);
    }
}
