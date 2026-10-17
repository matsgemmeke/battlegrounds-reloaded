package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.item.actor.HeldItemActor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentContext;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.deploy.object.HeldDeploymentObject;
import nl.matsgemmeke.battlegrounds.sound.api.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrimeDeploymentActionTest {

    private static final String ITEM_NAME = "Test Item";
    private static final UUID DEPLOYER_UNIQUE_ID = UUID.randomUUID();
    private static final DestructionListener LISTENER = damage -> {};
    private static final SoundTemplate SOUND_TEMPLATE = new SoundTemplate(null, null);

    @Mock
    private GameEntityFinder gameEntityFinder;
    @Mock
    private SoundEmitter soundEmitter;
    @InjectMocks
    private PrimeDeploymentAction deploymentAction;

    @Test
    @DisplayName("perform returns empty optional when no game entity exists for deployer unique id")
    void perform_deployerNotRegistered() {
        Deployer deployer = mock(Deployer.class);
        when(deployer.getUniqueId()).thenReturn(DEPLOYER_UNIQUE_ID);

        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, LISTENER);

        when(gameEntityFinder.findGameEntityByUniqueId(DEPLOYER_UNIQUE_ID)).thenReturn(Optional.empty());

        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(context);

        assertThat(deploymentResultOptional).isEmpty();
    }

    @Test
    @DisplayName("perform returns optional with DeploymentResult without playing sounds")
    void perform_withoutPlayingSounds() {
        ItemStack itemStack = new ItemStack(Material.STICK);
        GameEntity gameEntity = mock(GameEntity.class);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getHeldItem()).thenReturn(itemStack);
        when(deployer.getUniqueId()).thenReturn(DEPLOYER_UNIQUE_ID);

        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, LISTENER);

        when(gameEntityFinder.findGameEntityByUniqueId(DEPLOYER_UNIQUE_ID)).thenReturn(Optional.of(gameEntity));

        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(context);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).isInstanceOf(HeldDeploymentObject.class);
        });

        verifyNoInteractions(soundEmitter);
    }

    @Test
    @DisplayName("perform returns optional with DeploymentResult with playing sounds")
    void perform_withPlayingSounds() {
        ItemStack itemStack = new ItemStack(Material.STICK);
        Location deployerLocation = new Location(null, 1, 1, 1);
        GameEntity gameEntity = mock(GameEntity.class);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getDeployLocation()).thenReturn(deployerLocation);
        when(deployer.getHeldItem()).thenReturn(itemStack);
        when(deployer.getUniqueId()).thenReturn(DEPLOYER_UNIQUE_ID);

        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, LISTENER);

        when(gameEntityFinder.findGameEntityByUniqueId(DEPLOYER_UNIQUE_ID)).thenReturn(Optional.of(gameEntity));

        deploymentAction.configureSoundTemplate(SOUND_TEMPLATE);
        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(context);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).isInstanceOf(HeldDeploymentObject.class);
            assertThat(deploymentResult.actor()).isInstanceOf(HeldItemActor.class);
        });

        verify(soundEmitter).playSoundTemplate(eq(SOUND_TEMPLATE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == deployerLocation));
    }
}
