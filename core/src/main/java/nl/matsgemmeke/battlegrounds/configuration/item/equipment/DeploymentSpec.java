package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.deploy.DropPropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.TriggerSpec;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

import java.util.HashMap;
import java.util.Map;

public class DeploymentSpec {

    @Required
    public Double health;

    public Map<String, Double> resistances = new HashMap<>();

    public Map<String, @Valid TriggerSpec> triggers = new HashMap<>();

    @Required
    @Valid
    public DestructionPropertiesSpec onDestruction;

    @Required
    @Valid
    public ResetPropertiesSpec onReset;

    @Valid
    public ThrowPropertiesSpec throwing;

    @Valid
    public PlacePropertiesSpec placing;

    @Valid
    public CookingPropertiesSpec cooking;

    @Valid
    public DropPropertiesSpec dropping;

    @Valid
    public ManualActivationPropertiesSpec manualActivation;
}
