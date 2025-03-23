package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PrimeDeploymentTest {

    @Test
    public void performReturnsNewInstanceOfPrimeDeploymentObject() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        ItemStack itemStack = new ItemStack(Material.STICK);
        List<GameSound> primeSounds = Collections.emptyList();
        Location deployerLocation = new Location(null, 1, 1, 1);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getHeldItem()).thenReturn(itemStack);

        Entity deployerEntity = mock(Entity.class);
        when(deployerEntity.getLocation()).thenReturn(deployerLocation);

        PrimeDeployment deployment = new PrimeDeployment(audioEmitter, primeSounds);
        DeploymentResult result = deployment.perform(deployer, deployerEntity);

        assertThat(result.success()).isTrue();
        assertThat(result.object()).isInstanceOf(PrimeDeploymentObject.class);

        verify(audioEmitter).playSounds(primeSounds, deployerLocation);
    }
}
