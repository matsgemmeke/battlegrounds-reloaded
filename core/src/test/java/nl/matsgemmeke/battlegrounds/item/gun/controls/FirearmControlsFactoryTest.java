package nl.matsgemmeke.battlegrounds.item.gun.controls;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.*;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FirearmControlsFactoryTest {

    private BattlegroundsConfiguration configuration;
    private Firearm firearm;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private Section controlsSection;
    private Section rootSection;

    @BeforeEach
    public void setUp() {
        configuration = mock(BattlegroundsConfiguration.class);
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();

        firearm = mock(Firearm.class);
        when(firearm.getName()).thenReturn("test firearm");

        controlsSection = mock(Section.class);
        rootSection = mock(Section.class);
        when(rootSection.getSection("controls")).thenReturn(controlsSection);
    }

    @Test
    public void createMakesItemControlsWithReloadFunction() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("reload")).thenReturn("LEFT_CLICK");

        Section reloadingSection = mock(Section.class);

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");
        when(rootSection.getSection("reloading")).thenReturn(reloadingSection);

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory(configuration, contextProvider);
        ItemControls<GunHolder> controls = controlsFactory.create(rootSection, firearm, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createMakesItemControlsWithShootFunction() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("shoot")).thenReturn("RIGHT_CLICK");

        when(configuration.getGunTriggerSound()).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");

        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);

        FireMode fireMode = mock(FireMode.class);
        when(firearm.getAmmunitionStorage()).thenReturn(ammunitionStorage);
        when(firearm.getFireMode()).thenReturn(fireMode);

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory(configuration, contextProvider);
        ItemControls<GunHolder> controls = controlsFactory.create(rootSection, firearm, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createMakesItemControlsWithScopeUseAndScopeStopFunction() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("scope-use")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("scope-stop")).thenReturn("LEFT_CLICK");

        Section scopeSection = mock(Section.class);
        when(scopeSection.getFloatList("magnifications")).thenReturn(List.of(-0.1f));
        when(scopeSection.getString("stop-sound")).thenReturn("ENTITY_BLAZE_HURT-1-1-0");
        when(scopeSection.getString("use-sound")).thenReturn("ENTITY_BLAZE_HURT-1-1-0");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");
        when(rootSection.getSection("scope")).thenReturn(scopeSection);

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory(configuration, contextProvider);
        ItemControls<GunHolder> controls = controlsFactory.create(rootSection, firearm, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createMakesItemControlsWithScopeChangeMagnificationFunction() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("scope-change-magnification")).thenReturn("SWAP_FROM");
        when(controlsSection.getString("scope-use")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("scope-stop")).thenReturn("LEFT_CLICK");

        Section scopeSection = mock(Section.class);
        when(scopeSection.getString("change-magnification-sound")).thenReturn("AMBIENT_CAVE-1-1-1");
        when(scopeSection.getFloatList("magnifications")).thenReturn(List.of(-0.1f, -0.2f));
        when(scopeSection.getString("stop-sound")).thenReturn("ENTITY_BLAZE_HURT-1-1-0");
        when(scopeSection.getString("use-sound")).thenReturn("ENTITY_BLAZE_HURT-1-1-0");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");
        when(rootSection.getSection("scope")).thenReturn(scopeSection);

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory(configuration, contextProvider);
        ItemControls<GunHolder> controls = controlsFactory.create(rootSection, firearm, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createThrowsFirearmControlsCreationExceptionWhenScopeUseActionConfigurationValueIsInvalid() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("scope-use")).thenReturn("fail");
        when(controlsSection.getString("scope-stop")).thenReturn("LEFT_CLICK");

        Section scopeSection = mock(Section.class);
        when(scopeSection.getFloatList("magnifications")).thenReturn(List.of(-0.1f));
        when(scopeSection.getString("stop-sound")).thenReturn("AMBIENT_CAVE-1-1-1");
        when(scopeSection.getString("use-sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getSection("scope")).thenReturn(scopeSection);

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory(configuration, contextProvider);

        assertThrows(FirearmControlsCreationException.class, () -> controlsFactory.create(rootSection, firearm, gameKey));
    }

    @Test
    public void createThrowsFirearmControlsCreationExceptionWhenScopeStopActionConfigurationValueIsInvalid() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("scope-use")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("scope-stop")).thenReturn("fail");

        Section scopeSection = mock(Section.class);
        when(scopeSection.getFloatList("magnifications")).thenReturn(List.of(-0.1f));
        when(scopeSection.getString("stop-sound")).thenReturn("AMBIENT_CAVE-1-1-1");
        when(scopeSection.getString("use-sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getSection("scope")).thenReturn(scopeSection);

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory(configuration, contextProvider);

        assertThrows(FirearmControlsCreationException.class, () -> controlsFactory.create(rootSection, firearm, gameKey));
    }
}
