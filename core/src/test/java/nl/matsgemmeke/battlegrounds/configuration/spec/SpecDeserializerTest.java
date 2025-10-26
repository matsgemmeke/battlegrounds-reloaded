package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SpecDeserializerTest {

    @Test
    public void deserializeSpecThrowsSpecDeserializationExceptionWhenGivenFileDoesNotExist() {
        File file = new File("src/test/resources/does-not-exist.txt");

        SpecDeserializer specDeserializer = new SpecDeserializer();

        assertThatThrownBy(() -> specDeserializer.deserializeSpec(file, GunSpec.class))
                .isInstanceOf(SpecDeserializationException.class)
                .hasMessage("The given spec file at src/test/resources/does-not-exist.txt cannot be found");
    }

    @Test
    public void deserializeSpecReturnsInstanceOfGivenTypeThatIsParsedFromFile() {
        File file = new File("src/main/resources/items/submachine_guns/mp5.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        GunSpec gunSpec = specDeserializer.deserializeSpec(file, GunSpec.class);

        assertThat(gunSpec.name).isEqualTo("MP5");
        assertThat(gunSpec.description).isEqualTo("Fully automatic with good accuracy. Effective at close to medium range.");
        assertThat(gunSpec.gunType).isEqualTo("SUBMACHINE_GUN");
        assertThat(gunSpec.levelUnlocked).isEqualTo(1);
        assertThat(gunSpec.price).isEqualTo(1200);

        assertThat(gunSpec.ammo.magazineSize).isEqualTo(30);
        assertThat(gunSpec.ammo.defaultMagazineAmount).isEqualTo(3);
        assertThat(gunSpec.ammo.maxMagazineAmount).isEqualTo(8);

        assertThat(gunSpec.controls.reload).isEqualTo("LEFT_CLICK");
        assertThat(gunSpec.controls.shoot).isEqualTo("RIGHT_CLICK");
        assertThat(gunSpec.controls.scopeUse).isNull();
        assertThat(gunSpec.controls.scopeStop).isNull();
        assertThat(gunSpec.controls.scopeChangeMagnification).isNull();

        assertThat(gunSpec.item.material).isEqualTo("IRON_HOE");
        assertThat(gunSpec.item.displayName).isEqualTo("&f%name% %magazine_ammo%/%reserve_ammo%");
        assertThat(gunSpec.item.damage).isEqualTo(8);

        assertThat(gunSpec.reloading.type).isEqualTo("MAGAZINE");
        assertThat(gunSpec.reloading.duration).isEqualTo(48);
        assertThat(gunSpec.reloading.reloadSounds).isEqualTo("BLOCK_WOODEN_DOOR_OPEN-1-2-0, ENTITY_SKELETON_AMBIENT-1-0-3, BLOCK_WOODEN_DOOR_CLOSE-1-2-6, ENTITY_SKELETON_STEP-1-0-38, BLOCK_WOODEN_DOOR_CLOSE-1-2-42, BLOCK_STONE_BUTTON_CLICK_ON-1-1-44, BLOCK_WOODEN_DOOR_CLOSE-1-2-44");

        assertThat(gunSpec.shooting.fireMode.type).isEqualTo("FULLY_AUTOMATIC");
        assertThat(gunSpec.shooting.fireMode.amountOfShots).isNull();
        assertThat(gunSpec.shooting.fireMode.rateOfFire).isEqualTo(600);
        assertThat(gunSpec.shooting.fireMode.cycleCooldown).isNull();

        assertThat(gunSpec.shooting.projectile.type).isEqualTo("HITSCAN");
        assertThat(gunSpec.shooting.projectile.headshotDamageMultiplier).isEqualTo(1.4);

        assertThat(gunSpec.shooting.projectile.effect.effectType).isEqualTo("DAMAGE");
        assertThat(gunSpec.shooting.projectile.effect.triggers).isEmpty();

        assertThat(gunSpec.shooting.projectile.trajectoryParticleEffect.particle).isEqualTo("REDSTONE");
        assertThat(gunSpec.shooting.projectile.trajectoryParticleEffect.count).isEqualTo(1);
        assertThat(gunSpec.shooting.projectile.trajectoryParticleEffect.offsetX).isEqualTo(0.0);
        assertThat(gunSpec.shooting.projectile.trajectoryParticleEffect.offsetY).isEqualTo(0.0);
        assertThat(gunSpec.shooting.projectile.trajectoryParticleEffect.offsetZ).isEqualTo(0.0);
        assertThat(gunSpec.shooting.projectile.trajectoryParticleEffect.extra).isEqualTo(0);
        assertThat(gunSpec.shooting.projectile.trajectoryParticleEffect.blockData).isNull();
        assertThat(gunSpec.shooting.projectile.trajectoryParticleEffect.dustOptions.color).isEqualTo("#ffffff");
        assertThat(gunSpec.shooting.projectile.trajectoryParticleEffect.dustOptions.size).isEqualTo(1);

        assertThat(gunSpec.shooting.recoil.type).isEqualTo("CAMERA_MOVEMENT");
        assertThat(gunSpec.shooting.recoil.horizontal).containsExactly(-1.0f, 0.0f, 1.0f);
        assertThat(gunSpec.shooting.recoil.vertical).containsExactly(-1.5f, -2.0f, -2.5f);
        assertThat(gunSpec.shooting.recoil.kickbackDuration).isEqualTo(200L);

        assertThat(gunSpec.shooting.spreadPattern.type).isEqualTo("SINGLE_PROJECTILE");
        assertThat(gunSpec.shooting.spreadPattern.horizontalSpread).isNull();
        assertThat(gunSpec.shooting.spreadPattern.verticalSpread).isNull();
        assertThat(gunSpec.shooting.spreadPattern.projectileAmount).isNull();
    }
}
