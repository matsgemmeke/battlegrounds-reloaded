package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

import java.util.HashMap;
import java.util.Map;

public class DeploymentSpec {

    @Required
    public Double health;
    public Map<String, Double> resistances = new HashMap<>();
    @Required
    public DestructionPropertiesSpec onDestruction;
    @Required
    public CleanupPropertiesSpec onCleanup;
    public ThrowPropertiesSpec throwing;
    public PlacePropertiesSpec placing;
    public ManualActivationPropertiesSpec manualActivation;
}
