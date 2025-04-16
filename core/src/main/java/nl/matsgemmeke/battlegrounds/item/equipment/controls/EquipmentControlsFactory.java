package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.prime.PrimeDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.throwing.ThrowDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.throwing.ThrowDeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.cook.CookFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.place.PlaceFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing.ThrowFunction;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EquipmentControlsFactory {

    private static final String NAMESPACED_KEY_NAME = "battlegrounds-equipment";

    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public EquipmentControlsFactory(@NotNull GameContextProvider contextProvider, @NotNull NamespacedKeyCreator namespacedKeyCreator) {
        this.contextProvider = contextProvider;
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    @NotNull
    public ItemControls<EquipmentHolder> create(@NotNull Section rootSection, @NotNull Equipment equipment, @NotNull GameKey gameKey) {
        ItemControls<EquipmentHolder> controls = new ItemControls<>();

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

        Section controlsSection = rootSection.getSection("controls");
        String activateActionValue = controlsSection.getString("activate");
        String cookActionValue = controlsSection.getString("cook");
        String placeActionValue = controlsSection.getString("place");
        String throwActionValue = controlsSection.getString("throw");

        if (throwActionValue != null) {
            Action throwAction = this.getActionFromConfiguration(equipment, "throw", throwActionValue);

            if (cookActionValue != null) {
                Action cookAction = this.getActionFromConfiguration(equipment, "cook", cookActionValue);

                List<GameSound> cookSounds = DefaultGameSound.parseSounds(rootSection.getString("throwing.cook-sounds"));
                PrimeDeployment deployment = new PrimeDeployment(audioEmitter, cookSounds);
                CookFunction cookFunction = new CookFunction(equipment, deployment);

                controls.addControl(cookAction, cookFunction);
            }

            Section throwItemSection = rootSection.getOptionalSection("item.throw-item")
                    .orElseThrow(() -> new EquipmentControlsCreationException("Configuration for throw item template is missing"));

            String materialValue = throwItemSection.getString("material");
            Material material = this.getMaterialFromConfiguration(equipment, "throw item material", materialValue);

            UUID templateId = UUID.randomUUID();
            NamespacedKey key = namespacedKeyCreator.create(NAMESPACED_KEY_NAME);
            int damage = throwItemSection.getInt("damage");

            ItemTemplate itemTemplate = new ItemTemplate(templateId, key, material);
            itemTemplate.setDamage(damage);

            List<GameSound> throwSounds = DefaultGameSound.parseSounds(rootSection.getString("throwing.throw-sounds"));
            List<ProjectileEffect> projectileEffects = List.of();
            Map<DamageType, Double> resistances = this.getResistances(rootSection);
            double health = rootSection.getDouble("deploy.health");
            double velocity = rootSection.getDouble("throwing.velocity");
            long cooldown = rootSection.getLong("throwing.delay-after-throw");

            ThrowDeploymentProperties deploymentProperties = new ThrowDeploymentProperties(itemTemplate, throwSounds, projectileEffects, resistances, health, velocity, cooldown);
            ThrowDeployment deployment = new ThrowDeployment(deploymentProperties, audioEmitter);
            ThrowFunction throwFunction = new ThrowFunction(equipment, deployment);

            controls.addControl(throwAction, throwFunction);
        }

        if (placeActionValue != null) {
            Action placeAction = this.getActionFromConfiguration(equipment, "place", placeActionValue);

            List<GameSound> placeSounds = DefaultGameSound.parseSounds(rootSection.getString("placing.place-sounds"));
            Map<DamageType, Double> resistances = this.getResistances(rootSection);
            Material material = this.getMaterialFromConfiguration(equipment, "place material", rootSection.getString("placing.material"));
            double health = rootSection.getDouble("deploy.health");
            long cooldown = rootSection.getLong("placing.delay-after-placement");

            PlaceDeploymentProperties deploymentProperties = new PlaceDeploymentProperties(placeSounds, resistances, material, health, cooldown);
            PlaceDeployment deployment = new PlaceDeployment(deploymentProperties, audioEmitter);
            PlaceFunction placeFunction = new PlaceFunction(equipment, deployment);

            controls.addControl(placeAction, placeFunction);
        }

        if (activateActionValue != null) {
            Action activateAction = this.getActionFromConfiguration(equipment, "activate", activateActionValue);
            ActivateFunction activateFunction = new ActivateFunction(equipment);

            controls.addControl(activateAction, activateFunction);
        }

        return controls;
    }

    @NotNull
    private Action getActionFromConfiguration(@NotNull Equipment equipment, @NotNull String functionName, @NotNull String value) {
        try {
            return Action.valueOf(value);
        } catch (IllegalArgumentException e) {
            TextTemplate textTemplate = new TextTemplate("Error while creating controls for equipment %equipment_name%: " +
                    "the value \"%action_value%\" for function \"%function_name%\" is not a valid action type!");
            Map<String, Object> values = Map.of(
                    "equipment_name", equipment.getName(),
                    "action_value", value,
                    "function_name", functionName
            );
            String message = textTemplate.replace(values);

            throw new EquipmentControlsCreationException(message);
        }
    }

    @NotNull
    private Material getMaterialFromConfiguration(@NotNull Equipment equipment, @NotNull String role, @NotNull String value) {
        try {
            return Material.valueOf(value);
        } catch (IllegalArgumentException e) {
            TextTemplate textTemplate = new TextTemplate("Error while creating controls for equipment %equipment_name%: " +
                    "the value \"%material_value%\" for the %role% is not a valid material type!");
            Map<String, Object> values = Map.of(
                    "equipment_name", equipment.getName(),
                    "material_value", value,
                    "role", role
            );
            String message = textTemplate.replace(values);

            throw new EquipmentControlsCreationException(message);
        }
    }

    @NotNull
    private Map<DamageType, Double> getResistances(@NotNull Section section) {
        Section resistancesSection = section.getSection("deploy.resistances");

        if (resistancesSection != null) {
            return resistancesSection.getStringRouteMappedValues(false).entrySet().stream()
                    .map(this::convertResistanceValueToEntry)
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (x, y) -> y, HashMap::new));
        } else {
            return Collections.emptyMap();
        }
    }

    @NotNull
    private Entry<DamageType, Double> convertResistanceValueToEntry(@NotNull Entry<String, Object> entry) {
        String key = entry.getKey();
        String value = entry.getValue().toString();
        String resistanceEnumValue = key.replaceAll("-", "_").toUpperCase();

        DamageType damageType;
        double resistanceFactor;

        try {
            damageType = DamageType.valueOf(resistanceEnumValue);
        } catch (IllegalArgumentException e) {
            throw new EquipmentControlsCreationException("Invalid damage type value \"%s\"".formatted(key));
        }

        try {
            resistanceFactor = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new EquipmentControlsCreationException("Invalid resistance factor value; \"%s\" is not a number".formatted(value));
        }

        return new SimpleEntry<>(damageType, resistanceFactor);
    }
}
