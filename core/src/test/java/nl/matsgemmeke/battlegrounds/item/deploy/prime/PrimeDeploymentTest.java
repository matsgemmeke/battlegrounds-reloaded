package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentContext;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrimeDeploymentTest {

    private static final DestructionListener LISTENER = () -> {};

    @Mock
    private AudioEmitter audioEmitter;
    @InjectMocks
    private PrimeDeployment deployment;

    @Test
    void createContextReturnsDeploymentContextOptionalWithPrimeDeploymentObjectWithoutPlayingSounds() {
        ItemStack itemStack = new ItemStack(Material.STICK);
        Entity deployerEntity = mock(Entity.class);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getHeldItem()).thenReturn(itemStack);

        Optional<DeploymentContext> deploymentContextOptional = deployment.createContext(deployer, deployerEntity, LISTENER);

        assertThat(deploymentContextOptional).hasValueSatisfying(deploymentContext -> {
            assertThat(deploymentContext.entity()).isEqualTo(deployerEntity);
            assertThat(deploymentContext.deployer()).isEqualTo(deployer);
            assertThat(deploymentContext.deploymentObject()).satisfies(deploymentObject -> {
                assertThat(deploymentObject.getHealth()).isZero();
                assertThat(deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE)).isTrue();
            });
        });

        verifyNoInteractions(audioEmitter);
    }

    @Test
    void createContextReturnsDeploymentContextOptionalWithNewInstanceOfPrimeDeploymentObjectAndPlaysSounds() {
        ItemStack itemStack = new ItemStack(Material.STICK);
        List<GameSound> primeSounds = List.of(mock(GameSound.class));
        Location deployerLocation = new Location(null, 1, 1, 1);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getHeldItem()).thenReturn(itemStack);

        Entity deployerEntity = mock(Entity.class);
        when(deployerEntity.getLocation()).thenReturn(deployerLocation);

        deployment.configurePrimeSounds(primeSounds);
        Optional<DeploymentContext> deploymentContextOptional = deployment.createContext(deployer, deployerEntity, LISTENER);

        assertThat(deploymentContextOptional).hasValueSatisfying(deploymentContext -> {
            assertThat(deploymentContext.entity()).isEqualTo(deployerEntity);
            assertThat(deploymentContext.deployer()).isEqualTo(deployer);
            assertThat(deploymentContext.deploymentObject()).satisfies(deploymentObject -> {
                assertThat(deploymentObject.getHealth()).isZero();
                assertThat(deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE)).isTrue();
            });
        });

        verify(audioEmitter).playSounds(primeSounds, deployerLocation);
    }
}
