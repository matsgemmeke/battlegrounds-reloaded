package nl.matsgemmeke.battlegrounds.item.gun.controls;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.Firearm;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class FirearmControlsFactory {

    @NotNull
    private final BattlegroundsConfiguration configuration;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final ReloadSystemFactory reloadSystemFactory;

    @Inject
    public FirearmControlsFactory(
            @NotNull BattlegroundsConfiguration configuration,
            @NotNull GameContextProvider contextProvider,
            @NotNull ReloadSystemFactory reloadSystemFactory
    ) {
        this.configuration = configuration;
        this.contextProvider = contextProvider;
        this.reloadSystemFactory = reloadSystemFactory;
    }

    @NotNull
    public ItemControls<GunHolder> create(@NotNull Section section, @NotNull Firearm firearm, @NotNull GameKey gameKey) {
        ItemControls<GunHolder> controls = new ItemControls<>();

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

        Section controlsSection = section.getSection("controls");
        String reloadActionValue = controlsSection.getString("reload");
        String changeScopeMagnificationActionValue = controlsSection.getString("scope-change-magnification");
        String stopScopeActionValue = controlsSection.getString("scope-stop");
        String useScopeActionValue = controlsSection.getString("scope-use");
        String shootActionValue = controlsSection.getString("shoot");

        if (useScopeActionValue != null && stopScopeActionValue != null) {
            // Assume the gun also has a configuration for the scope
            Section scopeSection = section.getSection("scope");

            List<Float> magnificationSettings = scopeSection.getFloatList("magnifications");

            DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);

            if (changeScopeMagnificationActionValue != null) {
                List<GameSound> changeMagnificationSounds = DefaultGameSound.parseSounds(scopeSection.getString("change-magnification-sound"));

                ChangeScopeMagnificationFunction changeScopeMagnificationFunction = new ChangeScopeMagnificationFunction(scopeAttachment, audioEmitter);
                changeScopeMagnificationFunction.addSounds(changeMagnificationSounds);

                Action changeScopeMagnificationAction = this.getActionFromConfiguration(firearm, "scope-change-magnification", changeScopeMagnificationActionValue);

                controls.addControl(changeScopeMagnificationAction, changeScopeMagnificationFunction);
            }

            List<GameSound> useScopeSounds = DefaultGameSound.parseSounds(scopeSection.getString("use-sound"));

            UseScopeFunction useScopeFunction = new UseScopeFunction(scopeAttachment, audioEmitter);
            useScopeFunction.addSounds(useScopeSounds);

            List<GameSound> stopScopeSounds = DefaultGameSound.parseSounds(scopeSection.getString("stop-sound"));

            StopScopeFunction stopScopeFunction = new StopScopeFunction(scopeAttachment, audioEmitter);
            stopScopeFunction.addSounds(stopScopeSounds);

            Action useScopeAction = this.getActionFromConfiguration(firearm, "scope-use", useScopeActionValue);
            Action stopScopeAction = this.getActionFromConfiguration(firearm, "scope-stop", stopScopeActionValue);

            controls.addControl(useScopeAction, useScopeFunction);
            controls.addControl(stopScopeAction, stopScopeFunction);
        }

        if (reloadActionValue != null) {
            ReloadSystem reloadSystem = reloadSystemFactory.create(firearm, section.getSection("reloading"), audioEmitter);
            List<GameSound> reloadSounds = DefaultGameSound.parseSounds(section.getString("reloading.sound"));

            ReloadFunction reloadFunction = new ReloadFunction(firearm, reloadSystem);
            reloadFunction.addReloadSounds(reloadSounds);

            Action reloadAction = this.getActionFromConfiguration(firearm, "reload", reloadActionValue);

            controls.addControl(reloadAction, reloadFunction);
        }

        if (shootActionValue != null) {
            FireMode fireMode = firearm.getFireMode();
            List<GameSound> triggerSounds = DefaultGameSound.parseSounds(configuration.getGunTriggerSound());

            ShootFunction shootFunction = new ShootFunction(firearm, audioEmitter, fireMode);
            shootFunction.setTriggerSounds(triggerSounds);

            Action shootAction = this.getActionFromConfiguration(firearm, "shoot", shootActionValue);

            controls.addControl(shootAction, shootFunction);
        }

        return controls;
    }

    @NotNull
    private Action getActionFromConfiguration(@NotNull Firearm firearm, @NotNull String functionName, @NotNull String value) {
        try {
            return Action.valueOf(value);
        } catch (IllegalArgumentException e) {
            TextTemplate textTemplate = new TextTemplate("Error while creating controls for equipment %equipment_name%: " +
                    "the value \"%action_value%\" for function \"%function_name%\" is not a valid action type!");
            Map<String, Object> values = Map.of(
                    "equipment_name", firearm.getName(),
                    "action_value", value,
                    "function_name", functionName
            );
            String message = textTemplate.replace(values);

            throw new FirearmControlsCreationException(message);
        }
    }
}
