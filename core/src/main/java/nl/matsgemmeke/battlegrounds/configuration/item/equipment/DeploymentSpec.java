package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.deploy.DropPropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

import java.util.HashMap;
import java.util.Map;

public class DeploymentSpec {

    @Required
    public Double health;

    public Map<String, Double> resistances = new HashMap<>();

    public Map<String, TriggerSpec> triggers = new HashMap<>();

    @Required
    public DestructionPropertiesSpec onDestruction;

    @Required
    public CleanupPropertiesSpec onCleanup;

    public ThrowPropertiesSpec throwing;

    public PlacePropertiesSpec placing;

    public CookingPropertiesSpec cooking;

    public DropPropertiesSpec dropping;

    public ManualActivationPropertiesSpec manualActivation;
}
