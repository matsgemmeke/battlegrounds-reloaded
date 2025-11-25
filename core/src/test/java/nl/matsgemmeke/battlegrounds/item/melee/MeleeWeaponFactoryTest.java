package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MeleeWeaponFactoryTest {

    @Mock
    private MeleeWeaponRegistry meleeWeaponRegistry;
    @InjectMocks
    private MeleeWeaponFactory meleeWeaponFactory;

    @Test
    void createReturnsMeleeWeaponWithoutAssignedHolder() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec();

        MeleeWeapon meleeWeapon = meleeWeaponFactory.create(spec);

        assertThat(meleeWeapon.getName()).isEqualTo("Combat Knife");
        assertThat(meleeWeapon.getDescription()).isEqualTo("Standard issue military knife. Fast, quiet and deadly.");
        assertThat(meleeWeapon.getHolder()).isEmpty();

        verify(meleeWeaponRegistry).register(meleeWeapon);
    }

    @Test
    void createReturnsMeleeWeaponWithAssignedHolder() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = meleeWeaponFactory.create(spec, gamePlayer);

        assertThat(meleeWeapon.getName()).isEqualTo("Combat Knife");
        assertThat(meleeWeapon.getDescription()).isEqualTo("Standard issue military knife. Fast, quiet and deadly.");
        assertThat(meleeWeapon.getHolder()).hasValue(gamePlayer);

        verify(meleeWeaponRegistry).register(meleeWeapon, gamePlayer);
    }

    private MeleeWeaponSpec createMeleeWeaponSpec() {
        File file = new File("src/main/resources/items/melee_weapons/combat_knife.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, MeleeWeaponSpec.class);
    }
}
